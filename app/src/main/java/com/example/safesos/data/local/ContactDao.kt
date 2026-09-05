package com.example.safesos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts WHERE ownerId = :ownerId ORDER BY isPrimary DESC, contactId ASC")
    fun observeContacts(ownerId: Int): Flow<List<ContactEntity>>

    @Query("SELECT COUNT(*) FROM contacts WHERE ownerId = :ownerId")
    suspend fun countForUser(ownerId: Int): Int

    @Insert
    suspend fun insert(contact: ContactEntity): Long

    @Update
    suspend fun update(contact: ContactEntity)

    @Delete
    suspend fun delete(contact: ContactEntity)

    @Query("SELECT * FROM contacts WHERE ownerId = :ownerId ORDER BY isPrimary DESC, contactId ASC")
    suspend fun getContactsOnce(ownerId: Int): List<ContactEntity>
}
