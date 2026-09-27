package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MeepEntity
import com.example.model.Recipe
import com.example.model.ShopCatalog
import com.example.model.TimeOfDay
import com.example.ui.components.AnimatedSquiggleBackground
import com.example.ui.components.MeepBlobView
import com.example.ui.theme.PastelYellowBg
import com.example.ui.theme.PrimaryHoney
import com.example.ui.theme.SecondaryMint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingScreen(
    activeMeep: MeepEntity?,
    timeOfDay: TimeOfDay,
    onBack: () -> Unit,
    onFeedMeal: (Recipe) -> Unit
) {
    var selectedRecipe by remember {
        val recommended = ShopCatalog.RECIPES.firstOrNull { it.timeOfDay == timeOfDay }
        mutableStateOf(recommended ?: ShopCatalog.RECIPES.first())
    }

    val addedIngredients = remember(selectedRecipe) { mutableStateListOf<String>() }
    var cookingProgress by remember(selectedRecipe) { mutableFloatStateOf(0f) }
    var isCooked by remember(selectedRecipe) { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = cookingProgress,
        animationSpec = tween(400),
        label = "progress"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🍳 Meep Kitchen", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_cooking")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PastelYellowBg,
                    titleContentColor = Color(0xFF3E321E)
                )
            )
        }
    ) { padding ->
        AnimatedSquiggleBackground(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Time-based craving banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1C2)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(text = "⏰", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                            Column {
                                Text(
                                    text = "Current Time: ${timeOfDay.label}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5A4418)
                                )
                                Text(
                                    text = if (timeOfDay == TimeOfDay.NIGHT)
                                        "Late night snack cravings!"
                                    else
                                        "${activeMeep?.name ?: "Your meep"} will love fresh ${timeOfDay.label} food!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF6B5829)
                                )
                            }
                        }
                    }
                }

                // Recipe selection scroll
                item {
                    Text(
                        text = "Select a Recipe to Cook",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E321E),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ShopCatalog.RECIPES) { recipe ->
                            val isSelected = recipe.id == selectedRecipe.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) PrimaryHoney else Color.White,
                                shadowElevation = if (isSelected) 4.dp else 1.dp,
                                modifier = Modifier
                                    .clickable {
                                        selectedRecipe = recipe
                                        addedIngredients.clear()
                                        cookingProgress = 0f
                                        isCooked = false
                                    }
                                    .testTag("recipe_${recipe.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(text = recipe.iconEmoji, fontSize = 22.sp, modifier = Modifier.padding(end = 6.dp))
                                    Column {
                                        Text(
                                            text = recipe.name,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else Color(0xFF3E321E)
                                        )
                                        Text(
                                            text = "+${recipe.hungerRestore} Hunger",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color(0xFFFFF7DB) else SecondaryMint
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Cooking station Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Preparing ${selectedRecipe.name}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E321E)
                            )
                            Text(
                                text = "Tap each ingredient to drop into the pot!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Pan / Pot illustration
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEEB8))
                            ) {
                                Text(
                                    text = if (isCooked) selectedRecipe.iconEmoji else "🍲",
                                    fontSize = if (isCooked) 54.sp else 46.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Ingredients checklist
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                selectedRecipe.ingredients.forEach { ing ->
                                    val isAdded = addedIngredients.contains(ing)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isAdded) SecondaryMint else Color(0xFFF0E8D0),
                                        modifier = Modifier
                                            .clickable(enabled = !isAdded && !isCooked) {
                                                addedIngredients.add(ing)
                                                if (addedIngredients.size == selectedRecipe.ingredients.size) {
                                                    cookingProgress = 0.5f
                                                }
                                            }
                                            .testTag("ingredient_$ing")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            if (isAdded) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = ing,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isAdded) Color.White else Color(0xFF4A3E26)
                                            )
                                        }
                                    }
                                }
                            }

                            // Stir & Cook progress
                            AnimatedVisibility(visible = addedIngredients.size == selectedRecipe.ingredients.size && !isCooked) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        color = PrimaryHoney,
                                        trackColor = Color(0xFFFFE8A3),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (cookingProgress < 1f) {
                                                cookingProgress = (cookingProgress + 0.35f).coerceAtMost(1f)
                                                if (cookingProgress >= 1f) {
                                                    isCooked = true
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryHoney),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.testTag("stir_cook_button")
                                    ) {
                                        Icon(Icons.Default.Restaurant, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Stir & Simmer! ✨")
                                    }
                                }
                            }

                            // Dish Ready! Feed Meep button
                            AnimatedVisibility(visible = isCooked) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(top = 10.dp)
                                ) {
                                    Text(
                                        text = "Delicious! It's hot and ready! 😋",
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryMint
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onFeedMeal(selectedRecipe)
                                            onBack()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .testTag("feed_meep_button")
                                    ) {
                                        Text(
                                            "Feed to ${activeMeep?.name ?: "Meep"}! 💕",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Meep anticipation preview
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    if (activeMeep != null) {
                        MeepBlobView(
                            sizeDp = 110.dp,
                            name = activeMeep.name,
                            colorHex = activeMeep.colorHex,
                            hatId = activeMeep.hatId,
                            glassesId = activeMeep.glassesId,
                            neckId = activeMeep.neckId,
                            isEating = isCooked
                        )
                        Text(
                            text = if (isCooked) "Meep can't wait to eat!" else "Watching you cook excitedly!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B5829),
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
