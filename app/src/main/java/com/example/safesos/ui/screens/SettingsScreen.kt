package com.example.safesos.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.data.local.UserEntity
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*

@Composable
fun SettingsScreen(
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onViewIncidentHistory: () -> Unit,
    onEditProfile: () -> Unit,
    onManageMedicalInfo: () -> Unit,
    onAbout: () -> Unit
) {
    var showLogoutConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = { RedTopBar(title = "Settings", onBackClick = onBack) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Profile")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.Person,
                    iconTint = TextSecondary,
                    title = currentUser?.fullName ?: "Not signed in",
                    subtitle = currentUser?.phone ?: currentUser?.email,
                    onClick = onEditProfile
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Activity")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.History,
                    iconTint = SosRed,
                    title = "Incident History",
                    subtitle = "View past emergency alerts you've sent",
                    onClick = onViewIncidentHistory
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Medical Information")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.FavoriteBorder,
                    iconTint = SosRed,
                    title = "Manage Medical Info",
                    onClick = onManageMedicalInfo
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("General")
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.LocationOn,
                    iconTint = TextSecondary,
                    title = "Location Permission",
                    subtitle = "Open app settings to manage access",
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    }
                )
                HorizontalDivider(color = Divider)
                SettingsRow(
                    icon = Icons.Filled.Info,
                    iconTint = TextSecondary,
                    title = "About SafeSOS",
                    onClick = onAbout
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SosRedLight, RoundedCornerShape(12.dp))
                    .clickable { showLogoutConfirm = true }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Logout", color = SosRed, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log out?") },
            text = { Text("You'll need to log in again to send SOS alerts.") },
            confirmButton = {
                TextButton(onClick = { showLogoutConfirm = false; onLogout() }) {
                    Text("Log Out", color = SosRedDark, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Divider, RoundedCornerShape(12.dp))
            .background(Color.White),
        content = content
    )
}

@Composable
private fun SettingsRow(icon: ImageVector, iconTint: Color, title: String, subtitle: String? = null, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(20.dp))
    }
}
