package model

enum class MoodLevel(val displayName: String, val range: IntRange, val emoji: String) {
    VERY_BAD("Sehr schlecht", 1..2, "😢"),
    BAD("Schlecht", 3..4, "😟"),
    NEUTRAL("Neutral", 5..6, "😐"),
    GOOD("Gut", 7..8, "😊"),
    VERY_GOOD("Sehr gut", 9..10, "😄");

    companion object {
        fun fromRating(rating: Int): MoodLevel {
            require(rating in 1..10) {
                "Stimmung muss zwischen 1 und 10 liegen"
            }

            return entries.first {
                rating in it.range
            }
        }
    }
}
