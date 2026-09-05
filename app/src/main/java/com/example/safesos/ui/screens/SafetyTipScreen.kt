package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*

/**
 * Curated, emergency-only safety tips. Kept local (no network call) so the
 * content is always genuinely relevant — a general "advice" REST API can't
 * be trusted to only return emergency-related results.
 */
private val EMERGENCY_TIPS = listOf(
    "During a fire, stay low to the ground to avoid smoke inhalation, and never use an elevator.",
    "In a car accident, if it's safe to do so, turn on hazard lights before checking on anyone injured.",
    "During an earthquake: Drop, Cover, and Hold On — get under sturdy furniture and stay away from windows.",
    "If someone is choking and can't speak or cough, perform the Heimlich maneuver immediately.",
    "In a flood, avoid walking or driving through moving water — as little as 15cm can knock you off your feet.",
    "If you smell gas, don't turn on any lights or switches — leave the area and call for help from outside.",
    "During severe bleeding, apply firm, direct pressure with a clean cloth and keep the injured area elevated.",
    "If someone is having a seizure, clear the area around them and cushion their head — don't hold them down.",
    "In a home invasion or break-in, prioritize getting to safety and calling for help over confronting an intruder.",
    "During a lightning storm, avoid open fields, tall isolated trees, and metal objects.",
    "If someone is unresponsive and not breathing, start CPR immediately and keep going until help arrives.",
    "In a burn injury, cool the area with running water for at least 10 minutes — avoid ice, butter, or ointments.",
    "During a building evacuation, use stairs, not elevators, and help those who move slower if you safely can.",
    "If you're being followed or feel unsafe, move toward a public, well-lit place and call someone you trust.",
    "In extreme heat, watch for signs of heatstroke — confusion, hot dry skin, rapid pulse — and cool the person immediately.",
    "Keep your phone charged and know your exact address — it's the first thing responders will ask for."
)

@Composable
fun SafetyTipScreen(onBack: () -> Unit) {
    var currentTip by remember { mutableStateOf(EMERGENCY_TIPS.random()) }

    fun showAnotherTip() {
        var next = EMERGENCY_TIPS.random()
        // Avoid immediately repeating the same tip when there's more than one to choose from.
        while (next == currentTip && EMERGENCY_TIPS.size > 1) {
            next = EMERGENCY_TIPS.random()
        }
        currentTip = next
    }

    Scaffold(
        topBar = { RedTopBar(title = "Safety Tips", onBackClick = onBack) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SosRedLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = SosRed, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                "\u201C$currentTip\u201D",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Emergency safety tip",
                fontSize = 11.sp,
                color = TextHint,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            OutlinedButton(
                onClick = { showAnotherTip() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, tint = SosRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("GET ANOTHER TIP", color = SosRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
