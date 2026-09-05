package com.example.safesos.util

import android.content.Context
import android.os.Build
import android.telephony.SmsManager

/**
 * Wraps Android's SmsManager so the Confirm & Send screen can actually
 * dispatch a text message to each emergency contact, rather than just
 * simulating a "send". Requires the SEND_SMS runtime permission to have
 * already been granted by the caller.
 */
object SmsSender {

    data class SendReport(val successCount: Int, val failedNames: List<String>)

    /**
     * Sends [message] to every (name, phone) pair in [recipients]. Long
     * messages are automatically split into multiple SMS parts. Failures
     * (no SIM, radio off, malformed number, etc.) are caught per-recipient
     * so one bad number doesn't stop the rest from going out.
     */
    fun sendToAll(context: Context, recipients: List<Pair<String, String>>, message: String): SendReport {
        val manager = resolveSmsManager(context)
        val failed = mutableListOf<String>()
        var successCount = 0

        for ((name, phone) in recipients) {
            val sent = sendOne(manager, phone, message)
            if (sent) successCount++ else failed.add(name)
        }
        return SendReport(successCount, failed)
    }

    private fun sendOne(manager: SmsManager, phone: String, message: String): Boolean {
        return try {
            val parts = manager.divideMessage(message)
            if (parts.size > 1) {
                manager.sendMultipartTextMessage(phone, null, parts, null, null)
            } else {
                manager.sendTextMessage(phone, null, message, null, null)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun resolveSmsManager(context: Context): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
    }

    /** Builds the alert body sent to every contact. */
    fun buildAlertMessage(
        incidentLabel: String,
        senderName: String,
        extraDetails: String,
        addressLine: String,
        mapsUrl: String?
    ): String = buildString {
        append("\uD83D\uDEA8 $incidentLabel ALERT\n")
        append(senderName.ifBlank { "A SafeSOS user" })
        append(" needs help.\n")
        if (extraDetails.isNotBlank()) {
            append(extraDetails.trim())
            append("\n")
        }
        append("Location: $addressLine")
        if (!mapsUrl.isNullOrBlank()) {
            append("\n")
            append(mapsUrl)
        }
        append("\n\u2014 Sent via SafeSOS")
    }
}
