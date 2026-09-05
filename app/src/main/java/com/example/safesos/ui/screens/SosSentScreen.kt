package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.model.IncidentType
import com.example.safesos.ui.theme.*
import com.example.safesos.util.SmsSender

@Composable
fun SosSentScreen(
    incidentType: IncidentType,
    report: SmsSender.SendReport?,
    onBackToHome: () -> Unit
) {
    val allSucceeded = report != null && report.failedNames.isEmpty() && report.successCount > 0
    Scaffold(containerColor = if (allSucceeded) SosGreen else incidentType.color) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (allSucceeded) Icons.Filled.Check else Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    tint = if (allSucceeded) SosGreen else incidentType.color,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                if (allSucceeded) "${incidentType.shortLabel} Alert Sent!" else "Alert Sent — With Issues",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                report?.let {
                    if (it.failedNames.isEmpty())
                        "Your details and location were texted to all ${it.successCount} contact${if (it.successCount == 1) "" else "s"}."
                    else
                        "Sent to ${it.successCount} contact(s). Couldn't reach: ${it.failedNames.joinToString(", ")}."
                } ?: "Your details and location have been sent to your contacts.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Text("What happens next?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                NextStepRow(Icons.Filled.Notifications, "Your contacts will get an SMS immediately.")
                Spacer(modifier = Modifier.height(16.dp))
                NextStepRow(Icons.Filled.LocationOn, "The message includes your location and the emergency details.")
                Spacer(modifier = Modifier.height(16.dp))
                NextStepRow(Icons.Filled.ChatBubble, "Stay reachable or wait for their response.")

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SosRed)
                ) {
                    Text("BACK TO HOME", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun NextStepRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(BgLightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
    }
}
