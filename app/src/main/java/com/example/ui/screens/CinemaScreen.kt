package com.example.ui.screens

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.model.MovieInfo
import com.example.model.ShopCatalog
import com.example.ui.components.MeepBlobView
import com.example.ui.theme.PrimaryHoney
import com.example.ui.theme.SecondaryMint
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaScreen(
    activeMeep: MeepEntity?,
    onBack: () -> Unit,
    onWatchMovie: (MovieInfo) -> Unit,
    onFeedPopcorn: () -> Unit
) {
    var selectedMovie by remember { mutableStateOf(ShopCatalog.MOVIES.first()) }
    var isPlaying by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "cinema_anim")
    val starMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starMove"
    )

    val bounceRocket by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rocket"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎬 Meep Cinema Theater", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_cinema")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1E1724),
                    titleContentColor = Color(0xFFFFD54F)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF140E1B))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Theater Screen Frame
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2236)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                    ) {
                        // Animated Movie Projection
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Stars in space / background
                            for (i in 0..20) {
                                val sx = ((i * 45f + starMove) % w)
                                val sy = ((i * 37f) % h)
                                drawCircle(Color.White.copy(alpha = 0.8f), radius = 2.5f, center = Offset(sx, sy))
                            }

                            // Glowing colorful planet / cloud
                            drawCircle(
                                color = Color(0xFF7E57C2),
                                radius = 45f,
                                center = Offset(w * 0.8f, h * 0.35f)
                            )
                            drawCircle(
                                color = Color(0xFFBA68C8).copy(alpha = 0.6f),
                                radius = 60f,
                                center = Offset(w * 0.8f, h * 0.35f)
                            )

                            // Animated little meep hero in spaceship on screen
                            val heroX = w * 0.45f
                            val heroY = h * 0.55f + bounceRocket
                            drawOval(
                                color = Color(0xFFFFCA28),
                                topLeft = Offset(heroX - 40f, heroY - 20f),
                                size = Size(80f, 40f)
                            )
                            // Glass cockpit
                            drawCircle(
                                color = Color(0xFF80DEEA),
                                radius = 20f,
                                center = Offset(heroX, heroY - 10f)
                            )
                            // Little blob head inside
                            drawCircle(
                                color = Color.White,
                                radius = 12f,
                                center = Offset(heroX, heroY - 10f)
                            )
                        }

                        // Play overlay if not playing
                        if (!isPlaying) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier.clickable {
                                    isPlaying = true
                                    onWatchMovie(selectedMovie)
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFFFD54F))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Play ${selectedMovie.title}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Movie Titles selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ShopCatalog.MOVIES) { movie ->
                        val isSelected = movie.id == selectedMovie.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PrimaryHoney else Color(0xFF2C2236),
                            modifier = Modifier
                                .clickable {
                                    selectedMovie = movie
                                    isPlaying = false
                                }
                                .testTag("movie_${movie.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(text = movie.iconEmoji, fontSize = 20.sp, modifier = Modifier.padding(end = 6.dp))
                                Text(
                                    text = movie.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cinema Seats & Meep Audience
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF381418)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Meep sitting in cozy theater seat
                        if (activeMeep != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                MeepBlobView(
                                    sizeDp = 100.dp,
                                    name = activeMeep.name,
                                    colorHex = activeMeep.colorHex,
                                    hatId = activeMeep.hatId,
                                    glassesId = activeMeep.glassesId,
                                    neckId = activeMeep.neckId
                                )
                                Text(
                                    text = if (isPlaying) "🍿 Eating popcorn & watching!" else "Excited for the show!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }

                        // Popcorn bucket & Interaction
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🍿", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = onFeedPopcorn,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryHoney),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("feed_popcorn_button")
                            ) {
                                Text("Feed Popcorn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
