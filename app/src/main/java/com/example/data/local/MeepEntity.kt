package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meeps")
data class MeepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: Long = 0xFFFFFFFF,
    val hatId: String? = "hat_sprout",
    val glassesId: String? = null,
    val neckId: String? = null,
    val hunger: Int = 85,
    val funLevel: Int = 90,
    val energy: Int = 95,
    val posX: Float = 0.5f,
    val posY: Float = 0.7f,
    val personality: String = "Playful",
    val isSleeping: Boolean = false,
    val lastInteractedTimestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey
    val id: Int = 1,
    val email: String = "",
    val username: String = "MeepFriend",
    val isOnline: Boolean = false,
    val meepPoints: Int = 200,
    val totalInteractions: Int = 0
)

@Entity(tableName = "owned_items")
data class OwnedItemEntity(
    @PrimaryKey
    val itemId: String,
    val category: String,
    val purchasedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "park_history")
data class ParkInteractionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partnerName: String,
    val interactionType: String,
    val pointsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
