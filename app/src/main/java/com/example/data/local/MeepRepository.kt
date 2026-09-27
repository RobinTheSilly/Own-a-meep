package com.example.data.local

import com.example.model.AccessoryItem
import com.example.model.ItemCategory
import com.example.model.ParkInteractionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MeepRepository(private val meepDao: MeepDao) {

    val allMeeps: Flow<List<MeepEntity>> = meepDao.getAllMeeps()
    val activeMeep: Flow<MeepEntity?> = meepDao.getActiveMeep()
    val userAccount: Flow<UserAccountEntity?> = meepDao.getUserAccount()
    val ownedItems: Flow<List<OwnedItemEntity>> = meepDao.getOwnedItems()
    val parkHistory: Flow<List<ParkInteractionHistoryEntity>> = meepDao.getParkHistory()

    suspend fun initializeDefaultsIfNeeded() {
        val existingMeeps = allMeeps.firstOrNull() ?: emptyList()
        if (existingMeeps.isEmpty()) {
            val defaultMeep = MeepEntity(
                name = "Snowy",
                colorHex = 0xFFFFFFFF,
                hatId = "hat_sprout",
                glassesId = null,
                neckId = null,
                hunger = 85,
                funLevel = 90,
                energy = 95,
                posX = 0.5f,
                posY = 0.65f,
                personality = "Playful",
                isSleeping = false,
                isActive = true
            )
            meepDao.insertMeep(defaultMeep)
            meepDao.insertOwnedItem(OwnedItemEntity("hat_sprout", ItemCategory.HAT.name))
        }

        val account = userAccount.firstOrNull()
        if (account == null) {
            meepDao.insertOrUpdateAccount(
                UserAccountEntity(
                    id = 1,
                    email = "",
                    username = "MeepCaregiver",
                    isOnline = false,
                    meepPoints = 200,
                    totalInteractions = 0
                )
            )
        }
    }

    suspend fun updateMeepPosition(id: Long, posX: Float, posY: Float) {
        val meep = meepDao.getMeepById(id) ?: return
        meepDao.updateMeep(meep.copy(posX = posX.coerceIn(0.1f, 0.9f), posY = posY.coerceIn(0.4f, 0.85f)))
    }

    suspend fun feedMeep(id: Long, amount: Int = 30) {
        val meep = meepDao.getMeepById(id) ?: return
        val newHunger = (meep.hunger + amount).coerceAtMost(100)
        val newFun = (meep.funLevel + 10).coerceAtMost(100)
        meepDao.updateMeep(
            meep.copy(
                hunger = newHunger,
                funLevel = newFun,
                lastInteractedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun playOrPetMeep(id: Long, funGain: Int = 20) {
        val meep = meepDao.getMeepById(id) ?: return
        val newFun = (meep.funLevel + funGain).coerceAtMost(100)
        val newHunger = (meep.hunger - 2).coerceAtLeast(10)
        meepDao.updateMeep(
            meep.copy(
                funLevel = newFun,
                hunger = newHunger,
                lastInteractedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun setSleeping(id: Long, sleeping: Boolean) {
        val meep = meepDao.getMeepById(id) ?: return
        val newEnergy = if (sleeping) 100 else meep.energy
        meepDao.updateMeep(meep.copy(isSleeping = sleeping, energy = newEnergy))
    }

    suspend fun equipItem(meepId: Long, category: ItemCategory, itemId: String?) {
        val meep = meepDao.getMeepById(meepId) ?: return
        val updated = when (category) {
            ItemCategory.HAT -> meep.copy(hatId = itemId)
            ItemCategory.GLASSES -> meep.copy(glassesId = itemId)
            ItemCategory.NECK -> meep.copy(neckId = itemId)
            else -> meep
        }
        meepDao.updateMeep(updated)
    }

    suspend fun buyItem(item: AccessoryItem): Boolean {
        val account = userAccount.firstOrNull() ?: return false
        if (account.meepPoints < item.cost) return false

        val rowsAffected = meepDao.spendPoints(item.cost)
        if (rowsAffected > 0) {
            meepDao.insertOwnedItem(OwnedItemEntity(item.id, item.category.name))
            return true
        }
        return false
    }

    suspend fun addPoints(amount: Int) {
        meepDao.addPoints(amount)
    }

    suspend fun recordParkInteraction(
        partnerName: String,
        type: ParkInteractionType,
        points: Int
    ) {
        meepDao.addPoints(points)
        meepDao.insertParkHistory(
            ParkInteractionHistoryEntity(
                partnerName = partnerName,
                interactionType = type.name,
                pointsEarned = points,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun createNewMeep(name: String, colorHex: Long, personality: String): Boolean {
        val list = allMeeps.firstOrNull() ?: emptyList()
        if (list.size >= 3) return false

        val newMeep = MeepEntity(
            name = name.ifBlank { "Meepy" },
            colorHex = colorHex,
            hatId = null,
            glassesId = null,
            neckId = null,
            hunger = 90,
            funLevel = 90,
            energy = 95,
            posX = 0.5f,
            posY = 0.65f,
            personality = personality,
            isSleeping = false,
            isActive = true
        )
        val id = meepDao.insertMeep(newMeep)
        meepDao.setActiveMeep(id)
        return true
    }

    suspend fun switchActiveMeep(id: Long) {
        meepDao.setActiveMeep(id)
    }

    suspend fun deleteMeep(id: Long) {
        val list = allMeeps.firstOrNull() ?: emptyList()
        if (list.size <= 1) return // Must keep at least one meep
        meepDao.deleteMeep(id)
        val remaining = list.filter { it.id != id }
        if (remaining.isNotEmpty()) {
            meepDao.setActiveMeep(remaining.first().id)
        }
    }

    suspend fun updateAccount(email: String, username: String, isOnline: Boolean) {
        val account = userAccount.firstOrNull() ?: UserAccountEntity()
        meepDao.insertOrUpdateAccount(
            account.copy(
                email = email,
                username = username.ifBlank { "MeepFriend" },
                isOnline = isOnline
            )
        )
    }
}
