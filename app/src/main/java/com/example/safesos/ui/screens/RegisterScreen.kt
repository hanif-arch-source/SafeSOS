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
import com.example.safesos.ui.components.AuthPasswordField
import com.example.safesos.ui.components.AuthPrimaryButton
import com.example.safesos.ui.components.AuthTextField
import com.example.safesos.ui.components.RedTopBar
import com.example.safesos.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    authRepository: AuthRepository,
    onBack: () -> Unit,
    onRegisterSuccess: (UserEntity) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { RedTopBar(title = "Create Account", onBackClick = onBack) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Join SafeSOS so your emergency contacts can find you when it matters.",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(22.dp))

            FieldLabelReg("Full Name")
            AuthTextField(value = fullName, onValueChange = { fullName = it; errorMessage = null }, placeholder = "Enter your full name")

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabelReg("Email")
            AuthTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                placeholder = "you@example.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabelReg("Phone Number")
            AuthTextField(
                value = phone,
                onValueChange = { phone = it; errorMessage = null },
                placeholder = "+254 7XX XXX XXX",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabelReg("Password")
            AuthPasswordField(value = password, onValueChange = { password = it; errorMessage = null }, placeholder = "At least 4 characters")

            Spacer(modifier = Modifier.height(14.dp))
            FieldLabelReg("Confirm Password")
            AuthPasswordField(value = confirmPassword, onValueChange = { confirmPassword = it; errorMessage = null }, placeholder = "Re-enter your password")

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMessage!!, color = SosRedDark, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(22.dp))
            AuthPrimaryButton(
                text = "CREATE ACCOUNT",
                isLoading = isLoading,
                onClick = {
                    errorMessage = null
                    isLoading = true
                    scope.launch {
                        when (val result = authRepository.register(fullName, email, phone, password, confirmPassword)) {
                            is AuthResult.Success -> {
                                isLoading = false
                                onRegisterSuccess(result.user)
                            }
                            is AuthResult.Error -> {
                                isLoading = false
                                errorMessage = result.message
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", fontSize = 13.sp, color = TextSecondary)
                Text(
                    "Log In",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SosRed,
                    modifier = Modifier.clickableNoRipple { onBack() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FieldLabelReg(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    Spacer(modifier = Modifier.height(6.dp))
}
