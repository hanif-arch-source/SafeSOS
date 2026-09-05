package com.example.safesos.model

data class Contact(
    val id: Int,
    val name: String,
    val phone: String,
    val isPrimary: Boolean = false
)

val sampleContacts = listOf(
    Contact(1, "Aisha Hassan", "+254 712 345 678", isPrimary = true),
    Contact(2, "Omar Ali", "+254 733 987 654"),
    Contact(3, "Mary Wanjiku", "+254 722 456 789"),
    Contact(4, "Brian Otieno", "+254 700 111 222")
)
