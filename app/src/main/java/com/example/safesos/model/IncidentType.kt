package com.example.safesos.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The incident types SafeSOS's Emergency Alert flow supports. Each carries its
 * own icon/color and a flag for whether the medical-specific fields (blood
 * type, allergies) apply — everything else in the flow (contacts, location,
 * SMS) is shared across all of them.
 */
enum class IncidentType(
    val id: String,
    val label: String,
    val shortLabel: String,
    val icon: ImageVector,
    val color: Color,
    val colorLight: Color,
    val isMedical: Boolean = false
) {
    MEDICAL(
        id = "medical",
        label = "Medical Emergency",
        shortLabel = "Medical",
        icon = Icons.Filled.Favorite,
        color = Color(0xFFE53935),
        colorLight = Color(0xFFFDE8E8),
        isMedical = true
    ),
    FIRE(
        id = "fire",
        label = "Fire",
        shortLabel = "Fire",
        icon = Icons.Filled.LocalFireDepartment,
        color = Color(0xFFEF6C00),
        colorLight = Color(0xFFFFF0E0)
    ),
    ACCIDENT(
        id = "accident",
        label = "Accident",
        shortLabel = "Accident",
        icon = Icons.Filled.CarCrash,
        color = Color(0xFFF9A825),
        colorLight = Color(0xFFFFF8E1)
    ),
    CRIME(
        id = "crime",
        label = "Crime / Security Threat",
        shortLabel = "Crime",
        icon = Icons.Filled.GppMaybe,
        color = Color(0xFF6A1B9A),
        colorLight = Color(0xFFF3E5F5)
    ),
    DISASTER(
        id = "disaster",
        label = "Natural Disaster",
        shortLabel = "Disaster",
        icon = Icons.Filled.Thunderstorm,
        color = Color(0xFF5D4037),
        colorLight = Color(0xFFEFEBE9)
    ),
    OTHER(
        id = "other",
        label = "Other Emergency",
        shortLabel = "Other",
        icon = Icons.Filled.ReportProblem,
        color = Color(0xFF455A64),
        colorLight = Color(0xFFECEFF1)
    );

    companion object {
        fun fromId(id: String?): IncidentType = entries.firstOrNull { it.id == id } ?: OTHER
    }
}
