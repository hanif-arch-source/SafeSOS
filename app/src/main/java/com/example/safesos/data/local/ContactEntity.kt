package com.example.safesos.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One emergency contact belonging to a [UserEntity]. The foreign key + cascade
 * delete is what makes this a genuinely *related* table, not just a
 * co-located one: deleting a user's account removes their saved contacts too.
 */
@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerId")]
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val contactId: Int = 0,
    val ownerId: Int,
    val name: String,
    val phone: String,
    val isPrimary: Boolean = false
)
