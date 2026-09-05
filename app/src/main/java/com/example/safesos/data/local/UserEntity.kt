package com.example.safesos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordSalt: String,
    val passwordHash: String,
    // Persisted medical profile, editable from Settings > Manage Medical Info.
    // Pre-fills the Emergency Details form so it doesn't need re-entering
    // every time a Medical alert is sent.
    val bloodType: String = "",
    val medicalCondition: String = "",
    val allergies: String = ""
)
