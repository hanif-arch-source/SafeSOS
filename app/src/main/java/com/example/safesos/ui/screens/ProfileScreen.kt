package com.example.safesos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.data.local.UserEntity
import com.example.safesos.data.repository.AuthRepository
import com.example.safesos.data.repository.AuthResult
import com.example.safesos.ui.components.AuthPrimaryButton
import com.example.safesos.ui.components.AuthTextField
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*
import kotlinx.coroutines.launch

/** Lets the signed-in user view and edit their own name, email, and phone. */
@Composable
fun ProfileScreen(
    currentUser: UserEntity,
    authRepository: AuthRepository,
    onBack: () -> Unit,
    onProfileUpdated: (UserEntity) -> Unit
) {
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf(currentUser.fullName) }
    var email by remember { mutableStateOf(currentUser.email) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { RedTopBar(title = "Profile", onBackClick = onBack) },
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
            Text("Your details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("These are shared with your contacts when you send an alert.", fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(24.dp))
            FieldLabel("Full Name")
            AuthTextField(value = fullName, onValueChange = { fullName = it; errorMessage = null; successMessage = null }, placeholder = "Full name")

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabel("Email")
            AuthTextField(value = email, onValueChange = { email = it; errorMessage = null; successMessage = null }, placeholder = "you@example.com", keyboardType = KeyboardType.Email)

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabel("Phone Number")
            AuthTextField(value = phone, onValueChange = { phone = it; errorMessage = null; successMessage = null }, placeholder = "+254 7XX XXX XXX", keyboardType = KeyboardType.Phone)

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMessage!!, color = SosRedDark, fontSize = 12.sp)
            }
            if (successMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(successMessage!!, color = SosGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(22.dp))
            AuthPrimaryButton(
                text = "SAVE CHANGES",
                isLoading = isSaving,
                onClick = {
                    if (isSaving) return@AuthPrimaryButton
                    isSaving = true
                    successMessage = null
                    scope.launch {
                        when (val result = authRepository.updateProfile(currentUser, fullName, email, phone)) {
                            is AuthResult.Success -> {
                                isSaving = false
                                successMessage = "Profile updated."
                                onProfileUpdated(result.user)
                            }
                            is AuthResult.Error -> {
                                isSaving = false
                                errorMessage = result.message
                            }
                        }
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
