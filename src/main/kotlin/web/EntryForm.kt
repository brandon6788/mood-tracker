package web

import extension.*
import io.ktor.http.Parameters
import model.*

data class EntryForm(
    val title: String = "",
    val content: String = "",
    val moodRating: String = ""
)

fun Parameters.toEntryForm() =
    EntryForm(this["title"].orEmpty().trim(), this["content"].orEmpty().trim(), this["moodRating"].orEmpty().trim())

fun EntryForm.validate(): List<String> = buildList {
    if (title.isBlank()) add("Titel darf nicht leer sein")
    if (content.isBlank()) add("Inhalt darf nicht leer sein")
    if (moodRating.isNotEmpty() && moodRating.toIntOrNull()?.isValidMoodRating() != true)
        add("Stimmung muss eine Zahl von 1 bis 10 sein")
}

fun EntryForm.toEntry(id: EntryId, userId: UserId) =
    Entry(id, userId, title.trim(), content.trim(), moodRating.takeIf {
        it.isNotEmpty()
    }?.toInt(), currentDateTime())
