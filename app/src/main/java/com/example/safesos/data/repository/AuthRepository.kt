package com.example.safesos.data.repository

import com.example.safesos.data.local.UserDao
import com.example.safesos.data.local.UserEntity
import com.example.safesos.util.PasswordUtil
import java.util.regex.Pattern

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

private val EMAIL_PATTERN: Pattern = Pattern.compile(
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
)

class AuthRepository(private val userDao: UserDao) {

    suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): AuthResult {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()

        if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedPhone.isBlank() || password.isBlank()) {
            return AuthResult.Error("Please fill in every field.")
        }
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return AuthResult.Error("Enter a valid email address.")
        }
        if (password.length < 4) {
            return AuthResult.Error("Password must be at least 4 characters.")
        }
        if (password != confirmPassword) {
            return AuthResult.Error("Passwords do not match.")
        }
        if (userDao.countByEmail(trimmedEmail) > 0) {
            return AuthResult.Error("An account with this email already exists.")
        }

        val salt = PasswordUtil.generateSalt()
        val hash = PasswordUtil.hash(password, salt)
        val newUser = UserEntity(
            fullName = trimmedName,
            email = trimmedEmail,
            phone = trimmedPhone,
            passwordSalt = salt,
            passwordHash = hash
        )
        val id = userDao.insert(newUser)
        val saved = userDao.findById(id.toInt()) ?: newUser.copy(userId = id.toInt())
        return AuthResult.Success(saved)
    }

    suspend fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            return AuthResult.Error("Enter both email and password.")
        }
        val user = userDao.findByEmail(trimmedEmail)
            ?: return AuthResult.Error("Invalid email or password.")
        return if (PasswordUtil.verify(password, user.passwordSalt, user.passwordHash)) {
            AuthResult.Success(user)
        } else {
            AuthResult.Error("Invalid email or password.")
        }
    }

    suspend fun getUser(userId: Int): UserEntity? = userDao.findById(userId)

    /** Updates name/email/phone for an existing account, from Settings > Profile. */
    suspend fun updateProfile(user: UserEntity, fullName: String, email: String, phone: String): AuthResult {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()

        if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedPhone.isBlank()) {
            return AuthResult.Error("Please fill in every field.")
        }
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return AuthResult.Error("Enter a valid email address.")
        }
        if (!trimmedEmail.equals(user.email, ignoreCase = true) && userDao.countByEmail(trimmedEmail) > 0) {
            return AuthResult.Error("Another account already uses this email.")
        }

        val updated = user.copy(fullName = trimmedName, email = trimmedEmail, phone = trimmedPhone)
        userDao.update(updated)
        return AuthResult.Success(updated)
    }

    /** Updates the persisted medical profile, from Settings > Manage Medical Info. */
    suspend fun updateMedicalInfo(user: UserEntity, bloodType: String, medicalCondition: String, allergies: String): UserEntity {
        val updated = user.copy(
            bloodType = bloodType.trim(),
            medicalCondition = medicalCondition.trim(),
            allergies = allergies.trim()
        )
        userDao.update(updated)
        return updated
    }
}
