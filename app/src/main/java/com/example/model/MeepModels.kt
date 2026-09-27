package com.example.model

import java.util.Calendar

enum class TimeOfDay(val label: String, val greeting: String) {
    BREAKFAST("Brekkie Time", "Good morning! Time for delicious brekkie!"),
    LUNCH("Lunch Time", "Yay, it's lunch time! Let's eat together!"),
    DINNER("Dinner Time", "Mmm, dinner time is here! Smells good!"),
    NIGHT("Bedtime", "Yaaawn... Time to curl up and sleep... Zzz");

    companion object {
        fun current(): TimeOfDay {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 6..10 -> BREAKFAST
                in 11..15 -> LUNCH
                in 17..21 -> DINNER
                else -> NIGHT
            }
        }
    }
}

enum class ItemCategory {
    HAT, GLASSES, NECK, ENTERTAINMENT, FOOD
}

data class AccessoryItem(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val cost: Int,
    val description: String,
    val iconEmoji: String
)

data class Recipe(
    val id: String,
    val name: String,
    val timeOfDay: TimeOfDay?,
    val ingredients: List<String>,
    val iconEmoji: String,
    val hungerRestore: Int = 35
)

enum class ParkInteractionType(
    val title: String,
    val actionText: String,
    val points: Int,
    val iconEmoji: String
) {
    HUG("Warm Hug", "squishes into a super sweet warm hug!", 30, "🤗"),
    TAG("Play Tag", "zooms around giggling playing tag!", 35, "🏃"),
    SNACK("Share a Snack", "shares a delicious strawberry pastry!", 25, "🧁"),
    DANCE("Meep Dance", "does a synchronized happy wiggle dance!", 40, "💃"),
    JOKE("Tell a Joke", "tells an adorable squeaky meep joke!", 20, "😆")
}

data class ParkMeep(
    val id: String,
    val name: String,
    val ownerName: String,
    val colorHex: Long = 0xFFFFFFFF,
    val hatId: String? = null,
    val glassesId: String? = null,
    val neckId: String? = null,
    val posX: Float = 0.5f,
    val posY: Float = 0.5f,
    val status: String = "Waddling around happily!",
    val isOnline: Boolean = true
)

enum class ThoughtType(
    val label: String,
    val iconEmoji: String,
    val actionHint: String
) {
    CRAVE_BREAKFAST("Craving brekkie pancakes!", "🥞", "Tap to cook brekkie"),
    CRAVE_LUNCH("Craving a tasty sandwich!", "🥪", "Tap to cook lunch"),
    CRAVE_DINNER("Craving warm dinner ramen!", "🍜", "Tap to cook dinner"),
    CRAVE_SLEEP("So sleepy... needs a nap!", "💤", "Tap to tuck into bed"),
    CRAVE_MOVIE("Wants to watch a movie!", "🎬", "Tap to visit Cinema"),
    CRAVE_PLAY("Wants to play tag & games!", "🎈", "Tap to play mini-game"),
    CRAVE_PARK("Wants to meet friends at Meep Park!", "🌳", "Tap to go to Park"),
    CRAVE_HUG("Wants a warm gentle pet & hug!", "💖", "Tap to pet your meep")
}

object ShopCatalog {
    val ITEMS: List<AccessoryItem> = listOf(
        // Hats
        AccessoryItem("hat_sprout", "Leaf Sprout", ItemCategory.HAT, 0, "A fresh green sprout growing on top!", "🌱"),
        AccessoryItem("hat_party", "Party Cone Hat", ItemCategory.HAT, 50, "Festive colorful party cone with pompoms!", "🎉"),
        AccessoryItem("hat_straw", "Sunny Straw Hat", ItemCategory.HAT, 75, "Great for warm sunny park days.", "👒"),
        AccessoryItem("hat_flower", "Flower Crown", ItemCategory.HAT, 90, "Handmade wreath of spring daisies.", "🌸"),
        AccessoryItem("hat_bunny", "Fluffy Bunny Ears", ItemCategory.HAT, 120, "Soft white bunny ears that twitch.", "🐰"),
        AccessoryItem("hat_chef", "Chef Toque", ItemCategory.HAT, 100, "For master culinary meeps!", "👨‍🍳"),
        AccessoryItem("hat_crown", "Golden Royal Crown", ItemCategory.HAT, 180, "Sparkling royal gold crown.", "👑"),
        AccessoryItem("hat_tophat", "Dapper Top Hat", ItemCategory.HAT, 150, "A sophisticated gentleman's hat.", "🎩"),
        AccessoryItem("hat_detective", "Detective Cap", ItemCategory.HAT, 130, "For solving mysterious crumbs.", "🕵️"),

        // Glasses
        AccessoryItem("glasses_round", "Round Wire Frames", ItemCategory.GLASSES, 60, "Classic cute scholarly frames.", "👓"),
        AccessoryItem("glasses_sun", "Cool Star Shades", ItemCategory.GLASSES, 110, "Rad star-shaped sunglasses!", "⭐"),
        AccessoryItem("glasses_heart", "Heart Specs", ItemCategory.GLASSES, 95, "Lovely pink heart-tinted glasses.", "💕"),
        AccessoryItem("glasses_monocle", "Vintage Monocle", ItemCategory.GLASSES, 85, "Fancy gold eyepiece on a chain.", "🧐"),

        // Neck
        AccessoryItem("neck_bowtie", "Red Silk Bowtie", ItemCategory.NECK, 50, "A crisp dapper red bow.", "🎀"),
        AccessoryItem("neck_scarf", "Cozy Wool Scarf", ItemCategory.NECK, 85, "Warm knitted winter scarf.", "🧣"),
        AccessoryItem("neck_bell", "Golden Bell Ribbon", ItemCategory.NECK, 95, "Jingles gently when meep waddles!", "🔔"),
        AccessoryItem("neck_cape", "Hero Red Cape", ItemCategory.NECK, 140, "Flutters bravely in the park breeze.", "🦸"),

        // Entertainment
        AccessoryItem("toy_ball", "Rainbow Bouncy Ball", ItemCategory.ENTERTAINMENT, 40, "Meeps love bouncing it!", "⚽"),
        AccessoryItem("toy_bubbles", "Magic Bubble Wand", ItemCategory.ENTERTAINMENT, 65, "Blows floating shimmering bubbles.", "🫧"),
        AccessoryItem("toy_guitar", "Tiny Meep Ukulele", ItemCategory.ENTERTAINMENT, 125, "Strums cute high-pitched tunes.", "🎸"),
        AccessoryItem("toy_movie_pass", "Cinema Golden Ticket", ItemCategory.ENTERTAINMENT, 80, "Unlimited cozy movie screenings.", "🎟️"),
        AccessoryItem("toy_console", "Mini Game Boy", ItemCategory.ENTERTAINMENT, 160, "Play retro 8-bit meep adventures.", "🎮"),
        AccessoryItem("toy_plushie", "Mini Meep Plushie", ItemCategory.ENTERTAINMENT, 90, "A tiny cuddly plushie friend.", "🧸")
    )

    val RECIPES: List<Recipe> = listOf(
        Recipe("rec_pancakes", "Berry Fluffy Pancakes", TimeOfDay.BREAKFAST, listOf("Flour", "Sweet Berries", "Fresh Milk"), "🥞", 40),
        Recipe("rec_egg_toast", "Sunny Egg & Avocado Toast", TimeOfDay.BREAKFAST, listOf("Bread", "Golden Egg", "Avocado"), "🍳", 35),
        Recipe("rec_sandwich", "Triple Club Sandwich", TimeOfDay.LUNCH, listOf("Bread", "Cheddar Cheese", "Crisp Lettuce"), "🥪", 40),
        Recipe("rec_bento", "Kawaii Panda Bento", TimeOfDay.LUNCH, listOf("Rice", "Nori Seaweed", "Mini Sausage"), "🍱", 45),
        Recipe("rec_ramen", "Warm Cozy Ramen", TimeOfDay.DINNER, listOf("Noodles", "Savory Broth", "Soft Egg"), "🍜", 50),
        Recipe("rec_stew", "Garden Veggie Stew", TimeOfDay.DINNER, listOf("Carrots", "Potatoes", "Sweet Peas"), "🍲", 45),
        Recipe("rec_cupcake", "Strawberry Star Cupcake", null, listOf("Flour", "Cream", "Sugar Sprinkles"), "🧁", 30),
        Recipe("rec_popcorn", "Buttery Movie Popcorn", null, listOf("Corn", "Golden Butter", "Pinch of Salt"), "🍿", 25)
    )

    val MOVIES: List<MovieInfo> = listOf(
        MovieInfo("m1", "The Great Cloud Adventure", "A brave little Meep builds a propeller hat to discover the legendary rainbow cloud.", "☁️", 15),
        MovieInfo("m2", "Journey to Meep Mountain", "Three meep friends embark on an epic quest for the ancient Golden Acorn.", "🏔️", 18),
        MovieInfo("m3", "Meep Space Odyssey", "Floating zero-gravity blob adventures across candy nebula constellations.", "🚀", 20),
        MovieInfo("m4", "The Mystery of the Missing Donut", "Detective Meep searches the cozy kitchen for the midnight sprinkle thief.", "🔍", 14)
    )
}

data class MovieInfo(
    val id: String,
    val title: String,
    val synopsis: String,
    val iconEmoji: String,
    val durationSeconds: Int
)
