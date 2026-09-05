package com.example.safesos.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A record of a past emergency alert sent by a user. Related to [UserEntity]
 * via [ownerId] (foreign key, cascades on delete) — this is what lets Settings
 * show "your" incident history rather than everyone's.
 *
 * [contactsFailedNames] is stored as a single comma-separated string rather
 * than a separate table: it's a small, denormalized snapshot of "who wasn't
 * reached" at send time, not data that needs its own CRUD or relationships.
 */
@Entity(
    tableName = "incidents",
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
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val incidentId: Int = 0,
    val ownerId: Int,
    val incidentTypeId: String,
    val timestamp: Long,
    val locationLine: String,
    val detailsSummary: String,
    val contactsNotifiedCount: Int,
    val contactsFailedNames: String
)
