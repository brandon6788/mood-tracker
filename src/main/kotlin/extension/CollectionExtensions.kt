package extension

import model.Entry

fun List<Entry>.entriesWithMood(): List<Entry> =
    filter {
        it.moodRating != null
    }

fun List<Entry>.averageMood(): Double {
    val ratings = mapNotNull {
        it.moodRating
    }

    return if (ratings.isEmpty()) 0.0 else ratings.average()
}

fun List<Entry>.entriesWithTag(tag: String): List<Entry> {
    val normalTag = tag.normalizeTag()
    return filter { e ->
        e.tags.any {
            it.normalizeTag() == normalTag
        }
    }
}
