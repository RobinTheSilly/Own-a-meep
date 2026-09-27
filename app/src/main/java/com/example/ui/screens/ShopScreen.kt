package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MeepEntity
import com.example.data.local.OwnedItemEntity
import com.example.model.AccessoryItem
import com.example.model.ItemCategory
import com.example.model.ShopCatalog
import com.example.ui.components.AnimatedSquiggleBackground
import com.example.ui.components.MeepBlobView
import com.example.ui.theme.PastelYellowBg
import com.example.ui.theme.PrimaryHoney
import com.example.ui.theme.SecondaryMint
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    activeMeep: MeepEntity?,
    meepPoints: Int,
    ownedItems: List<OwnedItemEntity>,
    onBack: () -> Unit,
    onEquip: (ItemCategory, String?) -> Unit,
    onBuy: (AccessoryItem, (Boolean, String) -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val categories = listOf(
        ItemCategory.HAT to "👒 Hats",
        ItemCategory.GLASSES to "👓 Glasses",
        ItemCategory.NECK to "🧣 Neck",
        ItemCategory.ENTERTAINMENT to "🧸 Toys"
    )

    val currentCategory = categories[selectedCategoryIndex].first
    val itemsForCategory = remember(currentCategory) {
        ShopCatalog.ITEMS.filter { it.category == currentCategory }
    }

    val ownedItemIds = remember(ownedItems) {
        ownedItems.map { it.itemId }.toSet()
    }

    // Temporary preview accessories
    var previewHatId by remember(activeMeep?.hatId) { mutableStateOf(activeMeep?.hatId) }
    var previewGlassesId by remember(activeMeep?.glassesId) { mutableStateOf(activeMeep?.glassesId) }
    var previewNeckId by remember(activeMeep?.neckId) { mutableStateOf(activeMeep?.neckId) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛍️ Meep Boutique", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_shop")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Points pill
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
                    containerColor = PastelYellowBg,
                    titleContentColor = Color(0xFF3E321E)
                )
            )
        }
    ) { padding ->
        AnimatedSquiggleBackground(modifier = Modifier.padding(padding)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Live Fitting Room Preview Header
                item(span = { GridItemSpan(2) }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "✨ Fitting Room Preview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E321E)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            if (activeMeep != null) {
                                MeepBlobView(
                                    sizeDp = 120.dp,
                                    name = activeMeep.name,
                                    colorHex = activeMeep.colorHex,
                                    hatId = previewHatId,
                                    glassesId = previewGlassesId,
                                    neckId = previewNeckId
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap any accessory below to try it on and buy!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Category Tabs
                item(span = { GridItemSpan(2) }) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedCategoryIndex,
                        containerColor = Color.Transparent,
                        contentColor = PrimaryHoney,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEachIndexed { index, (_, label) ->
                            Tab(
                                selected = selectedCategoryIndex == index,
                                onClick = { selectedCategoryIndex = index },
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                modifier = Modifier.testTag("shop_tab_$index")
                            )
                        }
                    }
                }

                // Shop Items Grid
                items(itemsForCategory) { item ->
                    val isOwned = ownedItemIds.contains(item.id) || item.cost == 0
                    val isEquipped = when (item.category) {
                        ItemCategory.HAT -> activeMeep?.hatId == item.id
                        ItemCategory.GLASSES -> activeMeep?.glassesId == item.id
                        ItemCategory.NECK -> activeMeep?.neckId == item.id
                        else -> false
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEquipped) Color(0xFFF0FDF4) else Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Preview when tapping card
                                when (item.category) {
                                    ItemCategory.HAT -> previewHatId = item.id
                                    ItemCategory.GLASSES -> previewGlassesId = item.id
                                    ItemCategory.NECK -> previewNeckId = item.id
                                    else -> {}
                                }
                            }
                            .testTag("shop_item_${item.id}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF8E7))
                            ) {
                                Text(text = item.iconEmoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF3E321E),
                                maxLines = 1
                            )

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                maxLines = 2,
                                fontSize = 11.sp,
                                modifier = Modifier.height(30.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Price or Owned indicator
                            if (isOwned) {
                                Text(
                                    text = if (isEquipped) "Equipped ✨" else "Owned",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEquipped) SecondaryMint else Color(0xFF6B5829)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${item.cost} pts",
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryHoney,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action Button
                            if (isOwned) {
                                if (isEquipped) {
                                    OutlinedButton(
                                        onClick = {
                                            onEquip(item.category, null)
                                            when (item.category) {
                                                ItemCategory.HAT -> previewHatId = null
                                                ItemCategory.GLASSES -> previewGlassesId = null
                                                ItemCategory.NECK -> previewNeckId = null
                                                else -> {}
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("unequip_${item.id}")
                                    ) {
                                        Text("Unequip", fontSize = 12.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            onEquip(item.category, item.id)
                                            when (item.category) {
                                                ItemCategory.HAT -> previewHatId = item.id
                                                ItemCategory.GLASSES -> previewGlassesId = item.id
                                                ItemCategory.NECK -> previewNeckId = item.id
                                                else -> {}
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("equip_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Equip", fontSize = 12.sp)
                                    }
                                }
                            } else {
                                val canAfford = meepPoints >= item.cost
                                Button(
                                    onClick = {
                                        onBuy(item) { success, msg ->
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(msg)
                                            }
                                            if (success) {
                                                onEquip(item.category, item.id)
                                                when (item.category) {
                                                    ItemCategory.HAT -> previewHatId = item.id
                                                    ItemCategory.GLASSES -> previewGlassesId = item.id
                                                    ItemCategory.NECK -> previewNeckId = item.id
                                                    else -> {}
                                                }
                                            }
                                        }
                                    },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryHoney),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("buy_${item.id}")
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Buy", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
