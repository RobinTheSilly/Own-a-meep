package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MeepEntity
import com.example.ui.theme.PrimaryHoney
import com.example.ui.theme.SecondaryMint

@Composable
fun MeepManagerDialog(
    allMeeps: List<MeepEntity>,
    activeMeepId: Long?,
    onDismiss: () -> Unit,
    onSelectMeep: (Long) -> Unit,
    onCreateMeep: (String, Long, String) -> Unit,
    onDeleteMeep: (Long) -> Unit
) {
    var isCreatingNew by remember { mutableStateOf(false) }
    var newMeepName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(0xFFFFFFFF) }
    var selectedPersonality by remember { mutableStateOf("Playful") }

    val colorOptions = listOf(
        0xFFFFFFFF to "Classic White",
        0xFFFFE5EC to "Rosy Petal",
        0xFFE8F0FE to "Sky Breeze",
        0xFFE2F0D9 to "Mint Sprout",
        0xFFF3E5F5 to "Lavender Dream"
    )

    val personalities = listOf("Playful", "Foodie", "Cuddly", "Adventurous")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = PrimaryHoney)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCreatingNew) "Adopt a New Meep" else "My Meeps (${allMeeps.size}/3)",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                if (isCreatingNew) {
                    Text(
                        text = "Customize your new blob friend!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newMeepName,
                        onValueChange = { newMeepName = it },
                        label = { Text("Meep Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_new_meep_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Skin Color:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorOptions.forEach { (colorVal, _) ->
                            val isChosen = selectedColor == colorVal
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorVal))
                                    .border(
                                        width = if (isChosen) 3.dp else 1.dp,
                                        color = if (isChosen) PrimaryHoney else Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = colorVal }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Personality:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(personalities) { p ->
                            val isSel = selectedPersonality == p
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) PrimaryHoney else Color(0xFFF5EEDC),
                                modifier = Modifier.clickable { selectedPersonality = p }
                            ) {
                                Text(
                                    text = p,
                                    color = if (isSel) Color.White else Color(0xFF4A3E26),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "You can have up to 3 meeps. When you have more than 1, they keep each other company and never feel lonely!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B5829)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    allMeeps.forEach { meep ->
                        val isActive = meep.id == activeMeepId
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isActive) Color(0xFFFFF3CD) else Color(0xFFF9F6EE),
                            border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryHoney) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelectMeep(meep.id) }
                                .testTag("select_meep_${meep.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(meep.colorHex))
                                            .border(1.dp, Color.Gray, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = meep.name + if (isActive) " (Active)" else "",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${meep.personality} • Hunger ${meep.hunger}%",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                if (allMeeps.size > 1 && !isActive) {
                                    IconButton(onClick = { onDeleteMeep(meep.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    if (allMeeps.size < 3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { isCreatingNew = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("adopt_new_meep_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Adopt Another Meep (${allMeeps.size}/3)")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isCreatingNew) {
                Button(
                    onClick = {
                        onCreateMeep(newMeepName.ifBlank { "Meepy" }, selectedColor, selectedPersonality)
                        isCreatingNew = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryHoney),
                    modifier = Modifier.testTag("confirm_create_meep")
                ) {
                    Text("Welcome Home!")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Done")
                }
            }
        },
        dismissButton = {
            if (isCreatingNew) {
                TextButton(onClick = { isCreatingNew = false }) {
                    Text("Back")
                }
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun AccountDialog(
    currentEmail: String,
    currentUsername: String,
    isOnline: Boolean,
    onDismiss: () -> Unit,
    onSignIn: (String, String) -> Unit
) {
    var emailInput by remember { mutableStateOf(if (currentEmail.isNotBlank()) currentEmail else "johnnieisle@gmail.com") }
    var usernameInput by remember { mutableStateOf(if (currentUsername.isNotBlank()) currentUsername else "MeepExplorer") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isOnline) "🟢 Online Account" else "🌐 Connect Online", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Sign in for free with your email or Google account to visit Meep Park, play with friends, and earn bonus Meep Points!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email or Google Account") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_account_email")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    label = { Text("Meep Caregiver Nickname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_account_username")
                )

                if (isOnline) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Status: Online & Synced with Meep Park",
                        fontWeight = FontWeight.Bold,
                        color = SecondaryMint,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSignIn(emailInput, usernameInput)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryHoney),
                modifier = Modifier.testTag("sign_in_account_button")
            ) {
                Text(if (isOnline) "Update Profile" else "Go Online & Get +50 Pts!")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White
    )
}
