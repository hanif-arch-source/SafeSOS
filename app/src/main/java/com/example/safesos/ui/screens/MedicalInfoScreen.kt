package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.data.local.UserEntity
import com.example.safesos.data.repository.AuthRepository
import com.example.safesos.ui.components.AuthPrimaryButton
import com.example.safesos.ui.components.AuthTextField
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*
import kotlinx.coroutines.launch

private val BLOOD_TYPES = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

/**
 * Lets the signed-in user save a persisted medical profile (blood type,
 * condition, allergies) once, so it's ready to go the next time they need to
 * send a Medical emergency alert instead of retyping it every time.
 */
@Composable
fun MedicalInfoScreen(
    currentUser: UserEntity,
    authRepository: AuthRepository,
    onBack: () -> Unit,
    onSaved: (UserEntity) -> Unit
) {
    val scope = rememberCoroutineScope()

    var bloodType by remember { mutableStateOf(currentUser.bloodType) }
    var condition by remember { mutableStateOf(currentUser.medicalCondition) }
    var allergies by remember { mutableStateOf(currentUser.allergies) }
    var bloodTypeExpanded by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { RedTopBar(title = "Manage Medical Info", onBackClick = onBack) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Your medical profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                "Saved here once, this is automatically filled in whenever you send a Medical emergency alert.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))
            FieldLabel("Blood Type")
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(InputBg)
                        .clickable { bloodTypeExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (bloodType.isBlank()) "Select blood type" else bloodType,
                        color = if (bloodType.isBlank()) TextHint else TextPrimary,
                        fontSize = 14.sp
                    )
                    Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = TextSecondary)
                }
                DropdownMenu(expanded = bloodTypeExpanded, onDismissRequest = { bloodTypeExpanded = false }) {
                    BLOOD_TYPES.forEach { type ->
                        DropdownMenuItem(text = { Text(type) }, onClick = { bloodType = type; bloodTypeExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabel("Medical Condition (optional)")
            AuthTextField(value = condition, onValueChange = { condition = it; successMessage = null }, placeholder = "E.g. Asthma, Diabetes, etc.")

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabel("Allergies (optional)")
            AuthTextField(value = allergies, onValueChange = { allergies = it; successMessage = null }, placeholder = "E.g. Penicillin, Peanuts, etc.")

            if (successMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(successMessage!!, color = SosGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(22.dp))
            AuthPrimaryButton(
                text = "SAVE MEDICAL INFO",
                isLoading = isSaving,
                onClick = {
                    if (isSaving) return@AuthPrimaryButton
                    isSaving = true
                    scope.launch {
                        val updated = authRepository.updateMedicalInfo(currentUser, bloodType, condition, allergies)
                        isSaving = false
                        successMessage = "Medical info saved."
                        onSaved(updated)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(6.dp))
}
