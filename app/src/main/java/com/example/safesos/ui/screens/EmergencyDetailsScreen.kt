package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.model.IncidentType
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*

/** Shared across the whole Emergency Alert flow (details -> confirm -> sent). */
data class EmergencyFormState(
    var incidentTypeId: String = IncidentType.OTHER.id,
    var fullName: String = "",
    var age: String = "",
    var bloodType: String = "",
    var condition: String = "",
    var allergies: String = "",
    var description: String = "",
    var location: String = "Fetching location..."
)

@Composable
fun EmergencyDetailsScreen(
    incidentType: IncidentType,
    formState: EmergencyFormState,
    liveLocation: String,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var fullName by remember { mutableStateOf(formState.fullName) }
    var age by remember { mutableStateOf(formState.age) }
    var bloodType by remember { mutableStateOf(formState.bloodType) }
    var condition by remember { mutableStateOf(formState.condition) }
    var allergies by remember { mutableStateOf(formState.allergies) }
    var description by remember { mutableStateOf(formState.description) }
    var bloodTypeExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Scaffold(
        topBar = { RedTopBar(title = incidentType.label, onBackClick = onBack) },
        containerColor = Color.White,
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                if (errorMessage != null) {
                    Text(errorMessage!!, color = SosRedDark, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(
                    onClick = {
                        val missing = validate(incidentType, fullName, age, bloodType, description)
                        if (missing != null) {
                            errorMessage = missing
                            return@Button
                        }
                        errorMessage = null
                        formState.incidentTypeId = incidentType.id
                        formState.fullName = fullName
                        formState.age = age
                        formState.bloodType = bloodType
                        formState.condition = condition
                        formState.allergies = allergies
                        formState.description = description
                        onContinue()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = incidentType.color)
                ) {
                    Text("CONTINUE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(incidentType.colorLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(incidentType.icon, contentDescription = null, tint = incidentType.color, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                incidentType.label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Provide a few details below.\nThis will be sent to your contacts.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionLabel("Personal Information")
            Spacer(modifier = Modifier.height(10.dp))

            FieldLabel("Full Name", required = true)
            SosTextField(value = fullName, onValueChange = { fullName = it }, placeholder = "Enter your full name")

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabel("Age", required = incidentType.isMedical)
            SosTextField(
                value = age,
                onValueChange = { age = it },
                placeholder = "Enter your age",
                keyboardType = KeyboardType.Number
            )

            if (incidentType.isMedical) {
                Spacer(modifier = Modifier.height(14.dp))
                FieldLabel("Blood Type", required = true)
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
                            if (bloodType.isEmpty()) "Select blood type" else bloodType,
                            color = if (bloodType.isEmpty()) TextHint else TextPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = TextSecondary)
                    }
                    DropdownMenu(expanded = bloodTypeExpanded, onDismissRequest = { bloodTypeExpanded = false }) {
                        bloodTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = {
                                bloodType = type
                                bloodTypeExpanded = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel("Medical Information")
                Spacer(modifier = Modifier.height(10.dp))

                FieldLabel("Medical Condition (Optional)")
                SosTextField(value = condition, onValueChange = { condition = it }, placeholder = "E.g. Asthma, Diabetes, etc.")

                Spacer(modifier = Modifier.height(14.dp))
                FieldLabel("Allergies (Optional)")
                SosTextField(value = allergies, onValueChange = { allergies = it }, placeholder = "E.g. Penicillin, Peanuts, etc.")
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel("Emergency Details")
                Spacer(modifier = Modifier.height(10.dp))

                FieldLabel("What's happening?", required = true)
                SosMultilineField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Briefly describe the ${incidentType.shortLabel.lowercase()} emergency"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabel("Current Location")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBg)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = incidentType.color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(liveLocation, fontSize = 13.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun validate(
    incidentType: IncidentType,
    fullName: String,
    age: String,
    bloodType: String,
    description: String
): String? {
    if (fullName.isBlank()) return "Full name is required."
    return if (incidentType.isMedical) {
        when {
            age.isBlank() -> "Age is required."
            bloodType.isBlank() -> "Please select a blood type."
            else -> null
        }
    } else {
        if (description.isBlank()) "Please describe what's happening." else null
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
}

@Composable
private fun FieldLabel(text: String, required: Boolean = false) {
    Row {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        if (required) Text(" *", fontSize = 13.sp, color = SosRed)
    }
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun SosTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextHint, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = InputBg,
            focusedContainerColor = InputBg,
            unfocusedBorderColor = InputBg,
            focusedBorderColor = SosRed
        )
    )
}

@Composable
private fun SosMultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextHint, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(10.dp),
        singleLine = false,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = InputBg,
            focusedContainerColor = InputBg,
            unfocusedBorderColor = InputBg,
            focusedBorderColor = SosRed
        )
    )
}
