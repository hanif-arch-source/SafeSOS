package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.data.local.IncidentEntity
import com.example.safesos.data.repository.IncidentRepository
import com.example.safesos.model.IncidentType
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Read-only log of every alert this user has sent, newest first. Reached from
 * Settings > Incident History. Each row shows what kind of emergency it was,
 * when, where, and whether every contact was successfully reached.
 */
@Composable
fun IncidentHistoryScreen(
    ownerId: Int,
    incidentRepository: IncidentRepository,
    onBack: () -> Unit
) {
    val incidents by incidentRepository.observeHistory(ownerId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = { RedTopBar(title = "Incident History", onBackClick = onBack) },
        containerColor = Color.White
    ) { padding ->
        if (incidents.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(BgLightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.History, contentDescription = null, tint = TextHint, modifier = Modifier.size(30.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("No alerts sent yet", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Every emergency alert you send will show up here, with when it was sent and who was reached.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }
                items(incidents, key = { it.incidentId }) { incident ->
                    IncidentRow(incident)
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun IncidentRow(incident: IncidentEntity) {
    val type = IncidentType.fromId(incident.incidentTypeId)
    val hadFailures = incident.contactsFailedNames.isNotBlank()
    val dateText = remember(incident.timestamp) {
        SimpleDateFormat("MMM d, yyyy \u2022 h:mm a", Locale.getDefault()).format(Date(incident.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Divider, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(type.colorLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(type.icon, contentDescription = null, tint = type.color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(type.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(dateText, fontSize = 11.sp, color = TextSecondary)
            }
            Icon(
                if (hadFailures) Icons.Filled.WarningAmber else Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = if (hadFailures) SosRedDark else SosGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            if (hadFailures)
                "Reached ${incident.contactsNotifiedCount} contact(s) \u2014 couldn't reach: ${incident.contactsFailedNames}"
            else
                "Reached all ${incident.contactsNotifiedCount} saved contact(s)",
            fontSize = 12.sp,
            color = TextPrimary
        )
        if (incident.detailsSummary.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(incident.detailsSummary, fontSize = 12.sp, color = TextSecondary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(incident.locationLine, fontSize = 11.sp, color = TextHint)
    }
}
