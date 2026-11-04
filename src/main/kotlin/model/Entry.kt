package model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.jvm.JvmInline
import extension.normalizeTag
import extension.currentDateTime

@JvmInline
value class EntryId(val value: Long)

data class Entry(
    val id: EntryId,
    val userId: UserId,
    val title: String,
    val content: String,
    val moodRating: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val tags: Set<String> = emptySet()
) {
    init {
        require(moodRating == null || moodRating in 1..10) {
            "Stimmung muss zwischen 1 und 10 liegen"
        }
    }

    val wordCount: Int
        get() = content.trim().split(Regex("\\s+")).filter {
            it.isNotEmpty()
        }.size

    val hasGoodMood: Boolean
        get() = (moodRating ?: 0) >= 7

    val hasPoorMood: Boolean
        get() = (moodRating ?: 11) <= 3

    val isEdited: Boolean
        get() = updatedAt != null

    val createdDate: LocalDate
        get() = createdAt.date

    fun updateContent(newContent: String): Entry =
        copy(content = newContent, updatedAt = currentDateTime())

    fun updateMood(newRating: Int): Entry {
        require(newRating in 1..10) {
            "Mood rating must be between 1 and 10"
        }

        return copy(moodRating = newRating, updatedAt = currentDateTime())
    }

    fun addTag(tag: String): Entry {
        val norm = tag.normalizeTag()
        return if (norm.isEmpty() || tags.any {
                it.normalizeTag() == norm
            }) this else copy(tags = tags + norm)
    }

    fun removeTag(tag: String): Entry {
        val norm = tag.normalizeTag()
        return if (tags.none {
                it.normalizeTag() == norm
            }) this else copy(tags = tags.filterNot {
            it.normalizeTag() == norm
        }.toSet())
    }
}
