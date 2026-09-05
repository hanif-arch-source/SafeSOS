package com.example.safesos.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.safesos.ui.components.BottomNavBar
import com.example.safesos.ui.components.NavTab
import com.example.safesos.ui.theme.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(
    userFirstName: String?,
    addressLine: String,
    currentLatLng: LatLng?,
    hasMapsKey: Boolean,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRefreshLocation: () -> Unit,
    onSosClick: () -> Unit,
    onContactsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSafetyTipsClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavBar(selected = NavTab.HOME) { tab ->
                when (tab) {
                    NavTab.MEDICAL -> onSosClick()
                    NavTab.CONTACTS -> onContactsClick()
                    NavTab.SETTINGS -> onSettingsClick()
                    else -> {}
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // App bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("SafeSOS", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                if (!userFirstName.isNullOrBlank()) {
                    Text("Hi, $userFirstName", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                LiveMap(
                    currentLatLng = currentLatLng,
                    hasMapsKey = hasMapsKey,
                    hasLocationPermission = hasLocationPermission,
                    onRequestLocationPermission = onRequestLocationPermission
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Location row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(1.dp, Divider, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = SosRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(addressLine, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text(
                            if (hasLocationPermission) "Live location" else "Tap Refresh to enable location",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Row(
                        modifier = Modifier.clickable {
                            if (hasLocationPermission) onRefreshLocation() else onRequestLocationPermission()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Refresh", color = SosRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = SosRed, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SOS button
                PulsingSosButton(onClick = onSosClick)

                Spacer(modifier = Modifier.height(18.dp))

                // Quick action cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Warning,
                        iconBg = SosRedLight,
                        iconTint = SosRed,
                        title = "Emergency Alert",
                        subtitle = "Report an emergency\nto your contacts",
                        onClick = onSosClick
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.People,
                        iconBg = SosBlueSoft,
                        iconTint = Color(0xFF1E88E5),
                        title = "My Contacts",
                        subtitle = "Manage your\nemergency contacts",
                        onClick = onContactsClick
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                QuickActionCard(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Filled.Lightbulb,
                    iconBg = Color(0xFFFFF6DA),
                    iconTint = Color(0xFFC98A00),
                    title = "Safety Tips",
                    subtitle = "Get a quick emergency safety tip",
                    onClick = onSafetyTipsClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Safety banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F9F4))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = SosGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Your safety is our priority.", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("In an emergency, help is one tap away.", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Shows a real Google Map centered on the device's current location when a
 * Maps API key is configured and location permission is granted; otherwise
 * falls back to the original decorative placeholder so the screen never
 * looks broken.
 */
@Composable
private fun LiveMap(
    currentLatLng: LatLng?,
    hasMapsKey: Boolean,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFF3EE))
    ) {
        if (hasMapsKey && hasLocationPermission && currentLatLng != null) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
            }
            LaunchedEffect(currentLatLng) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            ) {
                Marker(state = MarkerState(position = currentLatLng), title = "You are here")
            }
        } else {
            MapPlaceholder(
                hasMapsKey = hasMapsKey,
                hasLocationPermission = hasLocationPermission,
                onRequestLocationPermission = onRequestLocationPermission
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (hasMapsKey && hasLocationPermission) SosGreen else TextHint)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                if (hasMapsKey && hasLocationPermission) "Live Location" else "Location unavailable",
                fontSize = 12.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun MapPlaceholder(
    hasMapsKey: Boolean,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = !hasLocationPermission) { onRequestLocationPermission() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0x334A90D9)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A90D9))
                    .border(3.dp, Color.White, CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            when {
                !hasMapsKey -> "Map unavailable"
                !hasLocationPermission -> "Tap to enable location"
                else -> "Locating..."
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Text(
            when {
                !hasMapsKey -> "No Maps API key configured"
                !hasLocationPermission -> "SafeSOS needs location access"
                else -> "Fetching your position"
            },
            fontSize = 11.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PulsingSosButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // outer pulse ring
        Box(
            modifier = Modifier
                .size(230.dp * scale)
                .clip(CircleShape)
                .background(SosRed.copy(alpha = alpha))
        )
        // static soft ring
        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(CircleShape)
                .background(SosRedPale)
        )
        // main button
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .background(SosRed)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text("SOS", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(
                    "TAP TO SEND",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "EMERGENCY ALERT",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Divider, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(subtitle, fontSize = 11.sp, color = TextSecondary, lineHeight = 14.sp)
    }
}
