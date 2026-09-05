package com.example.safesos.data.repository

import com.example.safesos.data.local.ContactDao
import com.example.safesos.data.local.ContactEntity
import kotlinx.coroutines.flow.Flow

sealed class ContactResult {
    object Success : ContactResult()
    data class Error(val message: String) : ContactResult()
}

class ContactsRepository(private val contactDao: ContactDao) {

    fun observeContacts(ownerId: Int): Flow<List<ContactEntity>> =
        contactDao.observeContacts(ownerId)

    suspend fun getContactsOnce(ownerId: Int): List<ContactEntity> =
        contactDao.getContactsOnce(ownerId)

    suspend fun addContact(ownerId: Int, name: String, phone: String): ContactResult {
        val trimmedName = name.trim()
        val trimmedPhone = phone.trim()
        if (trimmedName.isBlank() || trimmedPhone.isBlank()) {
            return ContactResult.Error("Name and phone number are required.")
        }
        val isFirstContact = contactDao.countForUser(ownerId) == 0
        contactDao.insert(
            ContactEntity(
                ownerId = ownerId,
                name = trimmedName,
                phone = trimmedPhone,
                isPrimary = isFirstContact
            )
        )
        return ContactResult.Success
    }

    suspend fun updateContact(contact: ContactEntity, name: String, phone: String): ContactResult {
        val trimmedName = name.trim()
        val trimmedPhone = phone.trim()
        if (trimmedName.isBlank() || trimmedPhone.isBlank()) {
            return ContactResult.Error("Name and phone number are required.")
        }
        contactDao.update(contact.copy(name = trimmedName, phone = trimmedPhone))
        return ContactResult.Success
    }

    suspend fun deleteContact(contact: ContactEntity): ContactResult {
        contactDao.delete(contact)
        return ContactResult.Success
    }
}
