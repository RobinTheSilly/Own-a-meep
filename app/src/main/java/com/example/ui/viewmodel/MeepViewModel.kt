package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MeepEntity
import com.example.data.local.MeepRepository
import com.example.data.local.OwnedItemEntity
import com.example.data.local.ParkInteractionHistoryEntity
import com.example.data.local.UserAccountEntity
import com.example.model.AccessoryItem
import com.example.model.ItemCategory
import com.example.model.MovieInfo
import com.example.model.ParkInteractionType
import com.example.model.ParkMeep
import com.example.model.Recipe
import com.example.model.ShopCatalog
import com.example.model.ThoughtType
import com.example.model.TimeOfDay
import com.example.notification.MeepNotificationHelper
import com.example.sound.MeepVoiceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MeepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MeepRepository
    val voiceManager: MeepVoiceManager = MeepVoiceManager(application)

    val allMeeps: StateFlow<List<MeepEntity>>
    val activeMeep: StateFlow<MeepEntity?>
    val userAccount: StateFlow<UserAccountEntity?>
    val ownedItems: StateFlow<List<OwnedItemEntity>>
    val parkHistory: StateFlow<List<ParkInteractionHistoryEntity>>

    private val _timeOfDay = MutableStateFlow(TimeOfDay.current())
    val timeOfDay: StateFlow<TimeOfDay> = _timeOfDay.asStateFlow()

    private val _currentThought = MutableStateFlow<ThoughtType?>(null)
    val currentThought: StateFlow<ThoughtType?> = _currentThought.asStateFlow()

    private val _isWalking = MutableStateFlow(false)
    val isWalking: StateFlow<Boolean> = _isWalking.asStateFlow()

    private val _speechText = MutableStateFlow<String?>(null)
    val speechText: StateFlow<String?> = _speechText.asStateFlow()

    private val _parkMeeps = MutableStateFlow<List<ParkMeep>>(emptyList())
    val parkMeeps: StateFlow<List<ParkMeep>> = _parkMeeps.asStateFlow()

    private val _activeParkInteraction = MutableStateFlow<String?>(null)
    val activeParkInteraction: StateFlow<String?> = _activeParkInteraction.asStateFlow()

    // Autonomous wandering coroutine
    private var wanderJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MeepRepository(database.meepDao())

        allMeeps = repository.allMeeps.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        activeMeep = repository.activeMeep.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        userAccount = repository.userAccount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        ownedItems = repository.ownedItems.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        parkHistory = repository.parkHistory.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            initParkMeeps()
            startAutonomousRoutine()
        }

        // Welcome back greeting
        viewModelScope.launch {
            delay(1000)
            val current = activeMeep.value
            val name = current?.name ?: "Meep"
            sayMeep("Meep meep! Welcome back, friend! I missed you!")
        }
    }

    private fun initParkMeeps() {
        val defaultParkMeeps = listOf(
            ParkMeep("p1", "Boba", "Alex", 0xFFFFF1E6, "hat_straw", "glasses_round", null, 0.22f, 0.55f, "Enjoying the sun!"),
            ParkMeep("p2", "Mochi", "Luna", 0xFFFFE5EC, "hat_flower", null, "neck_bowtie", 0.48f, 0.45f, "Looking for snacks!"),
            ParkMeep("p3", "Ziggy", "Milo", 0xFFE8F0FE, "hat_tophat", "glasses_sun", "neck_cape", 0.76f, 0.60f, "Practicing hero jumps!"),
            ParkMeep("p4", "Peaches", "Daisy", 0xFFFFF3CD, "hat_bunny", "glasses_heart", "neck_bell", 0.35f, 0.72f, "Waddling with flowers!"),
            ParkMeep("p5", "Pip", "Oliver", 0xFFE2F0D9, "hat_chef", null, "neck_scarf", 0.65f, 0.38f, "Looking for cooking buddies!")
        )
        _parkMeeps.value = defaultParkMeeps
    }

    private fun startAutonomousRoutine() {
        wanderJob?.cancel()
        wanderJob = viewModelScope.launch {
            while (true) {
                delay((5000..9000).random().toLong())
                _timeOfDay.value = TimeOfDay.current()

                val meep = activeMeep.value ?: continue
                if (meep.isSleeping) {
                    _currentThought.value = ThoughtType.CRAVE_SLEEP
                    continue
                }

                // Determine dynamic thought bubble based on time of day & stats
                updateThoughtBubble(meep)

                // Autonomous waddle around the room
                val targetX = (20..80).random() / 100f
                _isWalking.value = true
                delay(1200)
                repository.updateMeepPosition(meep.id, targetX, meep.posY)
                _isWalking.value = false

                // Random high-pitched chirp or cute meep remark
                if ((0..3).random() == 0) {
                    val remarks = listOf(
                        "Meep!",
                        "I'm feeling cozy!",
                        "Look at my cute waddle!",
                        "Can we play in the park?",
                        "Meep meep! ♪"
                    )
                    sayMeep(remarks.random(), speakOut = false) // play chirp
                    voiceManager.playMeepChirp(frequency = 1200f)
                }
            }
        }
    }

    private fun updateThoughtBubble(meep: MeepEntity) {
        val tod = _timeOfDay.value
        _currentThought.value = when {
            meep.hunger < 50 && tod == TimeOfDay.BREAKFAST -> ThoughtType.CRAVE_BREAKFAST
            meep.hunger < 50 && tod == TimeOfDay.LUNCH -> ThoughtType.CRAVE_LUNCH
            meep.hunger < 50 && tod == TimeOfDay.DINNER -> ThoughtType.CRAVE_DINNER
            tod == TimeOfDay.NIGHT -> ThoughtType.CRAVE_SLEEP
            meep.funLevel < 60 -> ThoughtType.CRAVE_PLAY
            (0..4).random() == 1 -> ThoughtType.CRAVE_PARK
            (0..4).random() == 2 -> ThoughtType.CRAVE_MOVIE
            else -> ThoughtType.CRAVE_HUG
        }
    }

    fun sayMeep(text: String, speakOut: Boolean = true) {
        _speechText.value = text
        if (speakOut) {
            voiceManager.speak(text)
        }
        viewModelScope.launch {
            delay(4000)
            if (_speechText.value == text) {
                _speechText.value = null
            }
        }
    }

    fun petMeep() {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            repository.playOrPetMeep(meep.id, funGain = 15)
            voiceManager.playMeepChirp(frequency = 1350f, durationMs = 120)
            val happyLines = listOf(
                "Meep meep! That tickles!",
                "I love hugs and head pats!",
                "Purr... meep!",
                "You're my best friend!"
            )
            sayMeep(happyLines.random())
            _currentThought.value = null
        }
    }

    fun feedMeepWithRecipe(recipe: Recipe) {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            repository.feedMeep(meep.id, recipe.hungerRestore)
            voiceManager.playMunchSound()
            sayMeep("Yummy yummy ${recipe.name}! My tummy is so happy!")
            _currentThought.value = null
        }
    }

    fun feedQuickSnack() {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            repository.feedMeep(meep.id, 25)
            voiceManager.playMunchSound()
            sayMeep("Nom nom nom! Delicious sweet snack!")
            _currentThought.value = null
        }
    }

    fun toggleSleep() {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            val newSleepState = !meep.isSleeping
            repository.setSleeping(meep.id, newSleepState)
            if (newSleepState) {
                sayMeep("Yawn... Going to sleep now. Goodnight! Zzz")
            } else {
                sayMeep("Good morning! I feel all refreshed! Meep meep!")
            }
        }
    }

    fun setMeepPosition(x: Float, y: Float) {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            repository.updateMeepPosition(meep.id, x, y)
        }
    }

    fun equipAccessory(category: ItemCategory, itemId: String?) {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            repository.equipItem(meep.id, category, itemId)
            voiceManager.playMeepChirp(frequency = 1250f)
            sayMeep(if (itemId != null) "Oooh! Look at my new look!" else "Took that off!")
        }
    }

    fun buyItem(item: AccessoryItem, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val success = repository.buyItem(item)
            if (success) {
                voiceManager.playHappyFanfare()
                sayMeep("Yay! We bought the ${item.name}!")
                onResult(true, "Purchased ${item.name}!")
            } else {
                onResult(false, "Not enough Meep Points!")
            }
        }
    }

    fun interactInPark(
        targetMeep: ParkMeep,
        interactionType: ParkInteractionType,
        onComplete: (Int) -> Unit
    ) {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            _activeParkInteraction.value = "${meep.name} and ${targetMeep.name}: ${interactionType.actionText}"
            voiceManager.playHappyFanfare()
            repository.recordParkInteraction(
                targetMeep.name,
                interactionType,
                interactionType.points
            )
            repository.playOrPetMeep(meep.id, funGain = 20)

            val lines = when (interactionType) {
                ParkInteractionType.HUG -> "Meep meep! What a cozy squishy hug with ${targetMeep.name}!"
                ParkInteractionType.TAG -> "Tag, you're it ${targetMeep.name}! Zoom zoom!"
                ParkInteractionType.SNACK -> "Sharing yummy treats with ${targetMeep.name} is the best!"
                ParkInteractionType.DANCE -> "Wiggle wiggle dance with ${targetMeep.name}!"
                ParkInteractionType.JOKE -> "Haha! ${targetMeep.name} loved my squeaky joke!"
            }
            sayMeep(lines)
            onComplete(interactionType.points)

            delay(3500)
            _activeParkInteraction.value = null
        }
    }

    fun watchMovie(movie: MovieInfo, onFinished: () -> Unit) {
        val meep = activeMeep.value ?: return
        viewModelScope.launch {
            sayMeep("Yay, movie time! ${movie.title} is starting!")
            voiceManager.playMeepChirp(1000f)
            // Restore fun and award some bonus points
            repository.playOrPetMeep(meep.id, funGain = 35)
            repository.addPoints(15)
            delay(2000)
            sayMeep("Meep! That was such a good movie! I loved the popcorn!")
            onFinished()
        }
    }

    fun createMeep(name: String, colorHex: Long, personality: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.createNewMeep(name, colorHex, personality)
            if (success) {
                voiceManager.playHappyFanfare()
                sayMeep("A new Meep has arrived! Welcome $name!")
            }
            onResult(success)
        }
    }

    fun switchMeep(meepId: Long) {
        viewModelScope.launch {
            repository.switchActiveMeep(meepId)
            voiceManager.playMeepChirp(1150f)
            val current = allMeeps.value.find { it.id == meepId }
            sayMeep("Switched to ${current?.name ?: "Meep"}! Hello!")
        }
    }

    fun deleteMeep(meepId: Long) {
        viewModelScope.launch {
            repository.deleteMeep(meepId)
        }
    }

    fun signInAccount(email: String, username: String) {
        viewModelScope.launch {
            repository.updateAccount(
                email = email.ifBlank { "meep.player@gmail.com" },
                username = username.ifBlank { "MeepMaster" },
                isOnline = true
            )
            repository.addPoints(50) // Bonus points for going online!
            voiceManager.playHappyFanfare()
            sayMeep("You're online now! Meep Park is fully unlocked! +50 Meep Points!")
        }
    }

    fun saveAndLeave(onDone: () -> Unit) {
        val meep = activeMeep.value
        val meepCount = allMeeps.value.size
        // Schedule / check lonely notification if user has only 1 meep
        if (meep != null && meepCount == 1) {
            MeepNotificationHelper.notifyLonelyIfNeeded(
                getApplication(),
                meep.name,
                meepCount
            )
        }
        voiceManager.sayGoodbye {
            onDone()
        }
    }

    override fun onCleared() {
        super.onCleared()
        wanderJob?.cancel()
        voiceManager.release()
    }
}
