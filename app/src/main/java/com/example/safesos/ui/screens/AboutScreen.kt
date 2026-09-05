package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { RedTopBar(title = "About SafeSOS", onBackClick = onBack) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(SosRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("SafeSOS", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Version 1.0", fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(24.dp))
            AboutSection(
                title = "What is SafeSOS?",
                body = "SafeSOS is an emergency alert app that lets you reach the people who matter most, the moment something goes wrong. With one tap \u2014 or a single shake of your phone \u2014 SafeSOS sends your live location and emergency details straight to your saved contacts by SMS."
            )
            AboutSection(
                title = "Key features",
                body = "\u2022 One-tap emergency alerts by SMS, with live location\n" +
                    "\u2022 Shake-to-alert \u2014 no need to unlock or navigate menus\n" +
                    "\u2022 Choose the type of emergency (medical, fire, accident, crime, disaster, or other)\n" +
                    "\u2022 A persisted medical profile so responders have what they need\n" +
                    "\u2022 A full history of every alert you've sent\n" +
                    "\u2022 Curated, genuinely emergency-relevant safety tips"
            )
            AboutSection(
                title = "Your privacy",
                body = "Your account, contacts, medical profile, and incident history are stored locally on your device. Your details are only shared with the emergency contacts you choose to add, and only at the moment you send an alert."
            )
            AboutSection(
                title = "About this project",
                body = "SafeSOS was built as a mobile application development project (IBL23305), demonstrating authentication, a local database, live location and mapping, SMS integration, and sensor-driven functionality in a real, working Android app."
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AboutSection(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgLightGray)
            .padding(16.dp)
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Text(body, fontSize = 13.sp, color = TextSecondary, lineHeight = 19.sp)
    }
    Spacer(modifier = Modifier.height(14.dp))
}
