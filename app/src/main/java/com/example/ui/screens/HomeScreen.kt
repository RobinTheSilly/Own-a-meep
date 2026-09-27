package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MeepEntity
import com.example.data.local.UserAccountEntity
import com.example.model.ThoughtType
import com.example.model.TimeOfDay
import com.example.ui.components.AnimatedSquiggleBackground
import com.example.ui.components.MeepBlobView
import com.example.ui.theme.PastelYellowBg
import com.example.ui.theme.PastelYellowDarker
import com.example.ui.theme.PrimaryHoney
import com.example.ui.theme.SecondaryMint

@Composable
fun HomeScreen(
    activeMeep: MeepEntity?,
    allMeeps: List<MeepEntity>,
    userAccount: UserAccountEntity?,
    timeOfDay: TimeOfDay,
    currentThought: ThoughtType?,
    speechText: String?,
    isWalking: Boolean,
    onPetMeep: () -> Unit,
    onToggleSleep: () -> Unit,
    onMoveMeep: (Float, Float) -> Unit,
    onThoughtClick: (ThoughtType) -> Unit,
    onNavigateKitchen: () -> Unit,
    onNavigateShop: () -> Unit,
    onNavigatePark: () -> Unit,
    onNavigateCinema: () -> Unit,
    onOpenMeepManager: () -> Unit,
    onOpenAccount: () -> Unit,
    onSaveAndExit: () -> Unit
) {
    var showGoodbyeDialog by remember { mutableStateOf(false) }

    Scaffold { padding ->
        AnimatedSquiggleBackground(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header: Meep selector, Points, Time of day, Account
                Surface(
                    color = PastelYellowDarker.copy(alpha = 0.85f),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        // Meep Switcher Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .clickable { onOpenMeepManager() }
                                .testTag("top_meep_selector")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Pets, contentDescription = null, tint = PrimaryHoney, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${activeMeep?.name ?: "Meep"} (${allMeeps.size}/3)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF4A3E26)
                                )
                            }
                        }

                        // Time of Day Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when (timeOfDay) {
                                TimeOfDay.BREAKFAST -> Color(0xFFFFF3CD)
                                TimeOfDay.LUNCH -> Color(0xFFE8F5E9)
                                TimeOfDay.DINNER -> Color(0xFFFFE0B2)
                                TimeOfDay.NIGHT -> Color(0xFFD1C4E9)
                            }
                        ) {
                            Text(
                                text = when (timeOfDay) {
                                    TimeOfDay.BREAKFAST -> "🥞 Brekkie"
                                    TimeOfDay.LUNCH -> "🥪 Lunch"
                                    TimeOfDay.DINNER -> "🍜 Dinner"
                                    TimeOfDay.NIGHT -> "💤 Night"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF4A3E26),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }

                        // Points Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF1C2),
                            modifier = Modifier
                                .clickable { onNavigateShop() }
                                .testTag("top_points_counter")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🪙", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${userAccount?.meepPoints ?: 0}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B4908)
                                )
                            }
                        }

                        // Online Badge / Sign-in
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (userAccount?.isOnline == true) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                            modifier = Modifier
                                .clickable { onOpenAccount() }
                                .testTag("top_account_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (userAccount?.isOnline == true) Color(0xFF4CAF50) else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (userAccount?.isOnline == true) "Online" else "Sign In",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userAccount?.isOnline == true) Color(0xFF2E7D32) else Color.Gray
                                )
                            }
                        }
                    }
                }

                // Main Room Canvas & Interactive Play Area
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val normX = (offset.x / size.width).coerceIn(0.15f, 0.85f)
                                val normY = (offset.y / size.height).coerceIn(0.45f, 0.82f)
                                onMoveMeep(normX, normY)
                            }
                        }
                ) {
                    val roomW = maxWidth
                    val roomH = maxHeight

                    // Room Background Canvas: Window, Sky, Soft carpet rug, Feeding bowl
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Cute Arched Window
                        val windowLeft = w * 0.68f
                        val windowTop = h * 0.08f
                        val windowW = w * 0.24f
                        val windowH = h * 0.28f

                        // Sky through window
                        val skyColor = when (timeOfDay) {
                            TimeOfDay.BREAKFAST -> Color(0xFFFFE0B2) // Warm sunrise
                            TimeOfDay.LUNCH -> Color(0xFFB3E5FC) // Bright blue
                            TimeOfDay.DINNER -> Color(0xFFFFCCBC) // Sunset peach
                            TimeOfDay.NIGHT -> Color(0xFF1A237E) // Starry indigo
                        }
                        drawRoundRect(
                            color = skyColor,
                            topLeft = Offset(windowLeft, windowTop),
                            size = Size(windowW, windowH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(windowW / 2f, windowW / 2f)
                        )
                        // Sun or Moon in window
                        if (timeOfDay == TimeOfDay.NIGHT) {
                            drawCircle(Color(0xFFFFF9C4), radius = 12f, center = Offset(windowLeft + windowW * 0.7f, windowTop + windowH * 0.35f))
                        } else {
                            drawCircle(Color(0xFFFFEE58), radius = 14f, center = Offset(windowLeft + windowW * 0.7f, windowTop + windowH * 0.35f))
                        }
                        // Window Frame
                        drawRoundRect(
                            color = Color(0xFF8D6E63),
                            topLeft = Offset(windowLeft, windowTop),
                            size = Size(windowW, windowH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(windowW / 2f, windowW / 2f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                        )

                        // Room Floor Base line
                        drawLine(
                            color = PastelYellowDarker,
                            start = Offset(0f, h * 0.52f),
                            end = Offset(w, h * 0.52f),
                            strokeWidth = 3f
                        )

                        // Cozy Room Pastel Rug
                        drawOval(
                            color = Color(0xFFFFF1C2),
                            topLeft = Offset(w * 0.12f, h * 0.56f),
                            size = Size(w * 0.76f, h * 0.35f)
                        )
                        drawOval(
                            color = Color(0xFFF7E69B).copy(alpha = 0.5f),
                            topLeft = Offset(w * 0.15f, h * 0.58f),
                            size = Size(w * 0.70f, h * 0.31f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                        )

                        // Cute Feeding Dish on the side
                        drawOval(
                            color = Color(0xFFFF8BA7),
                            topLeft = Offset(w * 0.08f, h * 0.75f),
                            size = Size(54f, 22f)
                        )
                    }

                    // Render Other Meeps in room (if user adopted 2 or 3 meeps)
                    allMeeps.filter { it.id != activeMeep?.id }.forEachIndexed { idx, friendMeep ->
                        val friendX = roomW * (if (idx == 0) 0.22f else 0.78f)
                        val friendY = roomH * 0.60f

                        Box(
                            modifier = Modifier
                                .offset(x = friendX - 35.dp, y = friendY - 35.dp)
                                .clickable { onOpenMeepManager() }
                        ) {
                            MeepBlobView(
                                sizeDp = 75.dp,
                                name = friendMeep.name,
                                colorHex = friendMeep.colorHex,
                                hatId = friendMeep.hatId,
                                glassesId = friendMeep.glassesId,
                                neckId = friendMeep.neckId
                            )
                        }
                    }

                    // Render Active Meep
                    if (activeMeep != null) {
                        val meepX = roomW * activeMeep.posX
                        val meepY = roomH * activeMeep.posY

                        Box(
                            modifier = Modifier
                                .offset(x = meepX - 70.dp, y = meepY - 70.dp)
                                .testTag("active_meep_blob")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Speech Bubble above Meep
                                AnimatedVisibility(visible = speechText != null) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.White,
                                        shadowElevation = 4.dp,
                                        modifier = Modifier
                                            .padding(bottom = 6.dp)
                                            .testTag("meep_speech_bubble")
                                    ) {
                                        Text(
                                            text = speechText ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4A3E26),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                MeepBlobView(
                                    sizeDp = 140.dp,
                                    name = activeMeep.name,
                                    colorHex = activeMeep.colorHex,
                                    hatId = activeMeep.hatId,
                                    glassesId = activeMeep.glassesId,
                                    neckId = activeMeep.neckId,
                                    isSleeping = activeMeep.isSleeping,
                                    isWalking = isWalking,
                                    thought = currentThought,
                                    onThoughtClick = onThoughtClick,
                                    onMeepClick = onPetMeep
                                )
                            }
                        }
                    }

                    // Multi-meep company badge
                    if (allMeeps.size > 1) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "🐾 Meeps are keeping each other company!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Vitals & Stats Bar
                if (activeMeep != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            // Hunger
                            StatBarItem(
                                label = "Hunger",
                                icon = "🍎",
                                value = activeMeep.hunger,
                                color = PrimaryHoney
                            )
                            // Fun
                            StatBarItem(
                                label = "Happiness",
                                icon = "💖",
                                value = activeMeep.funLevel,
                                color = Color(0xFFFF8BA7)
                            )
                            // Energy
                            StatBarItem(
                                label = "Energy",
                                icon = "⚡",
                                value = activeMeep.energy,
                                color = SecondaryMint
                            )
                        }
                    }
                }

                // Bottom Action Dock
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        ActionButton(
                            icon = Icons.Default.Restaurant,
                            label = "Kitchen",
                            color = PrimaryHoney,
                            tag = "nav_kitchen",
                            onClick = onNavigateKitchen
                        )
                        ActionButton(
                            icon = Icons.Default.ShoppingBag,
                            label = "Shop",
                            color = Color(0xFFFF8BA7),
                            tag = "nav_shop",
                            onClick = onNavigateShop
                        )
                        ActionButton(
                            icon = Icons.Default.Park,
                            label = "Meep Park",
                            color = SecondaryMint,
                            tag = "nav_park",
                            onClick = onNavigatePark
                        )
                        ActionButton(
                            icon = Icons.Default.Movie,
                            label = "Cinema",
                            color = Color(0xFF7E57C2),
                            tag = "nav_cinema",
                            onClick = onNavigateCinema
                        )
                        ActionButton(
                            icon = if (activeMeep?.isSleeping == true) Icons.Default.WbSunny else Icons.Default.Bedtime,
                            label = if (activeMeep?.isSleeping == true) "Wake" else "Nap",
                            color = Color(0xFF5C6BC0),
                            tag = "nav_sleep",
                            onClick = onToggleSleep
                        )
                        ActionButton(
                            icon = Icons.Default.ExitToApp,
                            label = "Save & Exit",
                            color = Color(0xFF8D6E63),
                            tag = "nav_exit",
                            onClick = { showGoodbyeDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Save & Leave Confirmation Dialog
    if (showGoodbyeDialog) {
        AlertDialog(
            onDismissRequest = { showGoodbyeDialog = false },
            icon = {
                Text("👋", fontSize = 32.sp)
            },
            title = {
                Text("Save & Leave Game", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Your meep's current position, hunger (${activeMeep?.hunger}%), and accessories will stay saved right here for when you return.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4A3E26)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (allMeeps.size == 1) {
                        Text(
                            text = "💡 Tip: You only have 1 meep, so it might feel lonely while you're away! We'll notify you if it misses you.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF856404)
                        )
                    } else {
                        Text(
                            text = "🐾 Your ${allMeeps.size} meeps will play together while you're away!",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryMint
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showGoodbyeDialog = false
                        onSaveAndExit()
                    },
                    modifier = Modifier.testTag("confirm_save_and_exit")
                ) {
                    Text("Say Goodbye & Exit", fontWeight = FontWeight.Bold, color = PrimaryHoney)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoodbyeDialog = false }) {
                    Text("Stay & Play")
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun StatBarItem(
    label: String,
    icon: String,
    value: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (value / 100f).coerceIn(0f, 1f) },
            color = color,
            trackColor = Color(0xFFF0E8D0),
            modifier = Modifier
                .width(85.dp)
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Text(
            text = "$value%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5A4418)
        )
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(tag)
            .padding(horizontal = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3E321E)
        )
    }
}
