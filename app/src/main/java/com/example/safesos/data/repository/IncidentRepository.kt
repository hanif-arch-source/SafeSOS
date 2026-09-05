package com.example.safesos.data.repository

import com.example.safesos.data.local.IncidentDao
import com.example.safesos.data.local.IncidentEntity
import com.example.safesos.model.IncidentType
import com.example.safesos.util.SmsSender
import kotlinx.coroutines.flow.Flow

class IncidentRepository(private val incidentDao: IncidentDao) {

    fun observeHistory(ownerId: Int): Flow<List<IncidentEntity>> =
        incidentDao.observeForUser(ownerId)

    /** Logs a just-sent alert so it shows up in the user's Incident History. */
    suspend fun logIncident(
        ownerId: Int,
        incidentType: IncidentType,
        locationLine: String,
        detailsSummary: String,
        report: SmsSender.SendReport
    ) {
        incidentDao.insert(
            IncidentEntity(
                ownerId = ownerId,
                incidentTypeId = incidentType.id,
                timestamp = System.currentTimeMillis(),
                locationLine = locationLine,
                detailsSummary = detailsSummary,
                contactsNotifiedCount = report.successCount,
                contactsFailedNames = report.failedNames.joinToString(", ")
            )
        )
    }
}
