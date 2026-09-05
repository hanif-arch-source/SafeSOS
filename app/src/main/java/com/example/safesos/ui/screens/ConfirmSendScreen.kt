package com.example.safesos.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.safesos.data.local.ContactEntity
import com.example.safesos.model.IncidentType
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*
import com.example.safesos.util.SmsSender
import kotlinx.coroutines.launch

@Composable
fun ConfirmSendScreen(
    incidentType: IncidentType,
    formState: EmergencyFormState,
    contacts: List<ContactEntity>,
    liveLocation: String,
    mapsUrl: String?,
    onBack: () -> Unit,
    onSent: (SmsSender.SendReport) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSending by remember { mutableStateOf(false) }
    var permissionDeniedNotice by remember { mutableStateOf(false) }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            sendNow(context, scope, incidentType, formState, contacts, liveLocation, mapsUrl) { report ->
                isSending = false
                onSent(report)
            }
        } else {
            isSending = false
            permissionDeniedNotice = true
        }
    }

    fun handleSendClick() {
        permissionDeniedNotice = false
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        isSending = true
        if (hasPermission) {
            sendNow(context, scope, incidentType, formState, contacts, liveLocation, mapsUrl) { report ->
                isSending = false
                onSent(report)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    Scaffold(
        topBar = { RedTopBar(title = "Confirm & Send", onBackClick = onBack) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(SosBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Description, contentDescription = null, tint = Color(0xFF3D6FB4), modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("Confirm & Send", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Review your details. They will be\nsent as an SMS to the contacts below.",
                fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Divider, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text("${incidentType.label} — Your Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow("Name", formState.fullName.ifBlank { "\u2014" })
                if (incidentType.isMedical) {
                    DetailRow("Age", formState.age.ifBlank { "\u2014" })
                    DetailRow("Blood Type", formState.bloodType.ifBlank { "\u2014" })
                    DetailRow("Medical Condition", formState.condition.ifBlank { "None stated" })
                    DetailRow("Allergies", formState.allergies.ifBlank { "None" })
                } else {
                    if (formState.age.isNotBlank()) DetailRow("Age", formState.age)
                    DetailRow("Details", formState.description.ifBlank { "\u2014" })
                }
                DetailRow("Location", liveLocation)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Sending to ${contacts.size} contact${if (contacts.size == 1) "" else "s"}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            if (contacts.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SosRedLight)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = SosRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("You have no saved contacts. Add one before sending an alert.", fontSize = 12.sp, color = TextPrimary)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    contacts.take(3).forEach { contact ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SosBlueSoft)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(contact.name.first().uppercaseChar().toString(), color = Color(0xFF3D6FB4), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (contacts.size > 3) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(InputBg)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text("+${contacts.size - 3} more", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            if (permissionDeniedNotice) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    "SMS permission was denied, so the alert can't be sent as a text message. Allow it in system settings and try again.",
                    fontSize = 12.sp,
                    color = SosRedDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { handleSendClick() },
                enabled = contacts.isNotEmpty() && !isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SosRed, disabledContainerColor = SosRed.copy(alpha = 0.5f))
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SEND ${incidentType.shortLabel.uppercase()} ALERT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("CANCEL", color = TextSecondary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Divider)
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sent as a real SMS, directly to your emergency contacts.", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

private fun sendNow(
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    incidentType: IncidentType,
    formState: EmergencyFormState,
    contacts: List<ContactEntity>,
    liveLocation: String,
    mapsUrl: String?,
    onDone: (SmsSender.SendReport) -> Unit
) {
    val details = if (incidentType.isMedical) {
        listOfNotNull(
            formState.bloodType.takeIf { it.isNotBlank() }?.let { "Blood type: $it" },
            formState.condition.takeIf { it.isNotBlank() }?.let { "Condition: $it" },
            formState.allergies.takeIf { it.isNotBlank() }?.let { "Allergies: $it" }
        ).joinToString("\n")
    } else {
        formState.description
    }
    val message = SmsSender.buildAlertMessage(
        incidentLabel = incidentType.label,
        senderName = formState.fullName,
        extraDetails = details,
        addressLine = liveLocation,
        mapsUrl = mapsUrl
    )
    val recipients = contacts.map { it.name to it.phone }
    scope.launch {
        val report = SmsSender.sendToAll(context, recipients, message)
        onDone(report)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, textAlign = TextAlign.End, modifier = Modifier.weight(1f, fill = false))
    }
}
