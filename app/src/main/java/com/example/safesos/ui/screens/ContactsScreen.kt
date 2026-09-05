package com.example.safesos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safesos.data.local.ContactEntity
import com.example.safesos.data.repository.ContactsRepository
import com.example.safesos.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ContactsScreen(
    ownerId: Int,
    contactsRepository: ContactsRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val contacts by contactsRepository.observeContacts(ownerId).collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<ContactEntity?>(null) }
    var deletingContact by remember { mutableStateOf<ContactEntity?>(null) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SosRed)
                    .padding(top = 24.dp, bottom = 14.dp, start = 4.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Text("My Contacts", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Emergency Contacts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("These contacts will receive your SOS alerts.", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))

            if (contacts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = TextHint, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No contacts yet", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("Tap + above to add your first emergency contact.", fontSize = 12.sp, color = TextSecondary)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(contacts, key = { it.contactId }) { contact ->
                        ContactRow(
                            contact = contact,
                            onEdit = { editingContact = contact },
                            onDelete = { deletingContact = contact }
                        )
                        HorizontalDivider(color = Divider)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF2F9F4))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Shield, contentDescription = null, tint = SosGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("At least one contact is required to send SOS alerts.", fontSize = 12.sp, color = TextPrimary)
            }
        }
    }

    if (showAddDialog) {
        ContactEditDialog(
            title = "Add Contact",
            initialName = "",
            initialPhone = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone ->
                scope.launch {
                    contactsRepository.addContact(ownerId, name, phone)
                    showAddDialog = false
                }
            }
        )
    }

    editingContact?.let { contact ->
        ContactEditDialog(
            title = "Edit Contact",
            initialName = contact.name,
            initialPhone = contact.phone,
            onDismiss = { editingContact = null },
            onConfirm = { name, phone ->
                scope.launch {
                    contactsRepository.updateContact(contact, name, phone)
                    editingContact = null
                }
            }
        )
    }

    deletingContact?.let { contact ->
        AlertDialog(
            onDismissRequest = { deletingContact = null },
            title = { Text("Remove contact?") },
            text = { Text("${contact.name} will no longer receive your SOS alerts.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        contactsRepository.deleteContact(contact)
                        deletingContact = null
                    }
                }) {
                    Text("Remove", color = SosRedDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingContact = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ContactEditDialog(
    title: String,
    initialName: String,
    initialPhone: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; error = null },
                    label = { Text("Phone number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error!!, color = SosRedDark, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank() || phone.isBlank()) {
                    error = "Name and phone number are required."
                } else {
                    onConfirm(name, phone)
                }
            }) { Text("Save", color = SosRed, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ContactRow(contact: ContactEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SosBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(contact.name.first().uppercaseChar().toString(), color = Color(0xFF3D6FB4), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(contact.phone, fontSize = 12.sp, color = TextSecondary)
        }
        if (contact.isPrimary) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SosRedLight)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Primary", fontSize = 10.sp, color = SosRed, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.width(6.dp))
        }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = TextSecondary)
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    onClick = { menuExpanded = false; onEdit() }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = SosRedDark) },
                    onClick = { menuExpanded = false; onDelete() }
                )
            }
        }
    }
}
