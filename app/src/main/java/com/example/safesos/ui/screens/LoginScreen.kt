package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import com.example.safesos.data.local.UserEntity
import com.example.safesos.data.repository.AuthRepository
import com.example.safesos.data.repository.AuthResult
import com.example.safesos.ui.components.AuthPasswordField
import com.example.safesos.ui.components.AuthPrimaryButton
import com.example.safesos.ui.components.AuthTextField
import com.example.safesos.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: (UserEntity) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(SosRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                "Welcome to SafeSOS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Sign in to reach your emergency contacts fast.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("Email", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            AuthTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                placeholder = "you@example.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text("Password", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            AuthPasswordField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                placeholder = "Enter your password"
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMessage!!, color = SosRedDark, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(22.dp))
            AuthPrimaryButton(
                text = "LOG IN",
                isLoading = isLoading,
                onClick = {
                    errorMessage = null
                    isLoading = true
                    scope.launch {
                        when (val result = authRepository.login(email, password)) {
                            is AuthResult.Success -> {
                                isLoading = false
                                onLoginSuccess(result.user)
                            }
                            is AuthResult.Error -> {
                                isLoading = false
                                errorMessage = result.message
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", fontSize = 13.sp, color = TextSecondary)
                Text(
                    "Register",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SosRed,
                    modifier = Modifier.clickableNoRipple { onNavigateToRegister() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/** Small helper so text links don't show a Material ripple like a button would. */
@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)
