package com.example.safesos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.ui.theme.NavInactive
import com.example.safesos.ui.theme.SosRed

enum class NavTab { HOME, MEDICAL, CONTACTS, SETTINGS }

@Composable
fun BottomNavBar(selected: NavTab, onTabSelected: (NavTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavItem("Home", Icons.Filled.Home, selected == NavTab.HOME) { onTabSelected(NavTab.HOME) }
        NavItem("Alert", Icons.Filled.Warning, selected == NavTab.MEDICAL) { onTabSelected(NavTab.MEDICAL) }
        NavItem("Contacts", Icons.Filled.People, selected == NavTab.CONTACTS) { onTabSelected(NavTab.CONTACTS) }
        NavItem("Settings", Icons.Filled.Settings, selected == NavTab.SETTINGS) { onTabSelected(NavTab.SETTINGS) }
    }
}

@Composable
private fun NavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val tint = if (isSelected) SosRed else NavInactive
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
