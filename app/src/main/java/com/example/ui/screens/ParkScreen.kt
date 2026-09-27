package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MeepEntity
import com.example.model.ParkInteractionType
import com.example.model.ParkMeep
import com.example.ui.components.MeepBlobView
import com.example.ui.theme.PastelYellowBg
import com.example.ui.theme.PrimaryHoney
import com.example.ui.theme.SecondaryMint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkScreen(
    activeMeep: MeepEntity?,
    parkMeeps: List<ParkMeep>,
    meepPoints: Int,
    activeBannerText: String?,
    onBack: () -> Unit,
    onInteract: (ParkMeep, ParkInteractionType, (Int) -> Unit) -> Unit,
    onSendEmote: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedParkMeep by remember { mutableStateOf<ParkMeep?>(null) }
    var pointsEarnedToast by remember { mutableStateOf<Int?>(null) }

    // Cloud floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "park_anim")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud"
    )

    // Fountain water ripples
    val fountainRipple by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fountain"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Park, contentDescription = null, tint = SecondaryMint)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Meep Park Online", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_park")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF1C2),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("🪙", fontSize = 16.sp, modifier = Modifier.padding(end = 4.dp))
                            Text(
                                text = "$meepPoints",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B4908),
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE8F5E9),
                    titleContentColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE8F5E9))
        ) {
            // Park Background Graphics: Sky, Grassy hills, Pond, Flowers, Benches
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Gentle blue sky at top
                drawRect(
                    color = Color(0xFFDFF1FD),
                    topLeft = Offset.Zero,
                    size = Size(w, h * 0.35f)
                )

                // Fluffy clouds
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 35f, center = Offset(cloudOffset, h * 0.10f))
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 50f, center = Offset(cloudOffset + 35f, h * 0.12f))
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 35f, center = Offset(cloudOffset + 70f, h * 0.10f))

                // Rolling green hills
                drawOval(
                    color = Color(0xFFC8E6C9),
                    topLeft = Offset(-w * 0.2f, h * 0.22f),
                    size = Size(w * 1.4f, h * 0.35f)
                )
                drawOval(
                    color = Color(0xFFA5D6A7),
                    topLeft = Offset(-w * 0.1f, h * 0.32f),
                    size = Size(w * 1.2f, h * 0.8f)
                )

                // Park cobblestone paths
                drawOval(
                    color = Color(0xFFFFF7DB),
                    topLeft = Offset(w * 0.15f, h * 0.50f),
                    size = Size(w * 0.7f, h * 0.32f)
                )

                // Central bubbling water fountain
                val fountainCenter = Offset(w * 0.5f, h * 0.65f)
                drawOval(
                    color = Color(0xFFB0BEC5),
                    topLeft = Offset(fountainCenter.x - 55f, fountainCenter.y - 30f),
                    size = Size(110f, 60f)
                )
                drawOval(
                    color = Color(0xFF4FC3F7),
                    topLeft = Offset(fountainCenter.x - 45f, fountainCenter.y - 25f),
                    size = Size(90f, 50f)
                )
                // Ripples
                drawOval(
                    color = Color.White.copy(alpha = 0.6f),
                    topLeft = Offset(fountainCenter.x - 20f - fountainRipple, fountainCenter.y - 10f - fountainRipple / 2f),
                    size = Size(40f + fountainRipple * 2f, 20f + fountainRipple)
                )

                // Blooming daisies and tulips
                val flowerPositions = listOf(
                    Offset(w * 0.12f, h * 0.42f),
                    Offset(w * 0.85f, h * 0.40f),
                    Offset(w * 0.20f, h * 0.80f),
                    Offset(w * 0.80f, h * 0.78f)
                )
                flowerPositions.forEach { pos ->
                    drawCircle(Color(0xFFFF80AB), radius = 8f, center = pos)
                    drawCircle(Color(0xFFFFEB3B), radius = 4f, center = pos)
                }
            }

            // Interactive Playground Area with All Meeps
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val areaW = maxWidth
                val areaH = maxHeight

                // Render Other Players' Meeps in Park
                parkMeeps.forEach { meep ->
                    val posX = areaW * meep.posX
                    val posY = areaH * meep.posY

                    Box(
                        modifier = Modifier
                            .offset(x = posX - 45.dp, y = posY - 45.dp)
                            .clickable { selectedParkMeep = meep }
                            .testTag("park_meep_${meep.name.lowercase()}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Owner label bubble
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.9f),
                                shadowElevation = 2.dp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = "${meep.name} (${meep.ownerName})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            MeepBlobView(
                                sizeDp = 86.dp,
                                name = meep.name,
                                colorHex = meep.colorHex,
                                hatId = meep.hatId,
                                glassesId = meep.glassesId,
                                neckId = meep.neckId
                            )
                        }
                    }
                }

                // Render Player's Active Meep in Park
                if (activeMeep != null) {
                    val playerX = areaW * activeMeep.posX
                    val playerY = areaH * (activeMeep.posY - 0.05f)

                    Box(
                        modifier = Modifier
                            .offset(x = playerX - 50.dp, y = playerY - 50.dp)
                            .testTag("player_meep_in_park")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryHoney,
                                shadowElevation = 3.dp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = "⭐ You (${activeMeep.name})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            MeepBlobView(
                                sizeDp = 98.dp,
                                name = activeMeep.name,
                                colorHex = activeMeep.colorHex,
                                hatId = activeMeep.hatId,
                                glassesId = activeMeep.glassesId,
                                neckId = activeMeep.neckId
                            )
                        }
                    }
                }
            }

            // Top Status / Active Interaction Banner
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AnimatedVisibility(visible = activeBannerText != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text("💖", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = activeBannerText ?: "",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B4908)
                            )
                        }
                    }
                }

                // Points celebration toast
                AnimatedVisibility(visible = pointsEarnedToast != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SecondaryMint,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+$pointsEarnedToast Meep Points!",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Bottom Park Bar: Online Roster & Emote bar
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🐾 Meeps in Park (Tap to Play!)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(parkMeeps) { meep ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF1F8E9),
                                modifier = Modifier
                                    .clickable { selectedParkMeep = meep }
                                    .testTag("park_roster_${meep.name.lowercase()}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4CAF50))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${meep.name} (${meep.ownerName})",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = Color(0xFF33691E)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Emote Bar
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val emotes = listOf("Meep! 👋", "Wanna play? 🎈", "Yummy! 🍰", "Besties! 💖")
                        emotes.forEach { emote ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFF9C4),
                                modifier = Modifier
                                    .clickable { onSendEmote(emote) }
                                    .testTag("emote_$emote")
                            ) {
                                Text(
                                    text = emote,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B4908),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Interaction Selection Dialog
    selectedParkMeep?.let { targetMeep ->
        AlertDialog(
            onDismissRequest = { selectedParkMeep = null },
            icon = {
                Text(text = "🌸", fontSize = 28.sp)
            },
            title = {
                Text(
                    text = "Play with ${targetMeep.name}!",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${targetMeep.ownerName}'s meep is waiting! Choose a cute activity to earn Meep Points:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4A3E26)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ParkInteractionType.entries.forEach { interaction ->
                        Button(
                            onClick = {
                                selectedParkMeep = null
                                onInteract(targetMeep, interaction) { pts ->
                                    pointsEarnedToast = pts
                                    coroutineScope.launch {
                                        delay(2600)
                                        pointsEarnedToast = null
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (interaction) {
                                    ParkInteractionType.HUG -> Color(0xFFFF8BA7)
                                    ParkInteractionType.TAG -> PrimaryHoney
                                    ParkInteractionType.SNACK -> SecondaryMint
                                    ParkInteractionType.DANCE -> Color(0xFF9C27B0)
                                    ParkInteractionType.JOKE -> Color(0xFF42A5F5)
                                }
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("interact_${interaction.name.lowercase()}")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = interaction.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = interaction.title, fontWeight = FontWeight.Bold)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "+${interaction.points} pts",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedParkMeep = null }) {
                    Text("Close")
                }
            },
            containerColor = Color.White
        )
    }
}
