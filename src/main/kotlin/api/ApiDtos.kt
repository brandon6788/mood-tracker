package api

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime
import model.*
import service.MoodReport
import web.*

@Serializable
data class ErrorResponse(val error: String, val details: List<String> = emptyList())

@Serializable
data class EntryResponse(
    val id: Long,
    val title: String,
    val content: String,
    val moodRating: Int?,
    val moodLevel: MoodLevel?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val wordCount: Int,
    val tags: Set<String>
)

@Serializable
data class CreateEntryRequest(
    val title: String,
    val content: String,
    val moodRating: Int? = null
)

@Serializable
data class StatsResponse(
    val averageMood: Double,
    val distribution: Map<MoodLevel, Int>,
    val bestEntryId: Long?
)

fun Entry.toResponse() = EntryResponse(id.value, title, content, moodRating, moodRating?.let {
    MoodLevel.fromRating(it)
}, createdAt, updatedAt, wordCount, tags)

fun MoodReport.toResponse() = StatsResponse(averageMood, MoodLevel.entries.associateWith {
    distribution[it] ?: 0
}, bestEntry?.id?.value)

private fun CreateEntryRequest.toForm() = EntryForm(title.trim(), content.trim(), moodRating?.toString().orEmpty())
fun CreateEntryRequest.validate(): List<String> = toForm().validate()
fun CreateEntryRequest.toEntry(id: EntryId, userId: UserId): Entry = toForm().toEntry(id, userId)
