package extension

import model.Entry
import model.EntryId
import model.UserId
import kotlinx.datetime.LocalDateTime

const val CSV_HEADER = "id,userId,createdAt,title,content,moodRating"

fun Entry.toCsvLine(): String = listOf(
    id.value.toString(),
    userId.value.toString(),
    createdAt.toString(),
    title,
    content,
    moodRating?.toString() ?: ""
).joinToString(",")

fun String.parseEntryFromCsv(targetUserId: UserId): Entry {
    val parts = this.split(",")
    require(parts.size == 6) {
        "CSV lines müssen genau 6 Felder haben!"
    }

    val id = parts[0].toLong()

    val createdAt = LocalDateTime.parse(parts[2])
    val title = parts[3]
    val content = parts[4]
    val moodRating = parts[5].takeIf {
        it.isNotEmpty()
    }?.toInt()

    return Entry(
        id = EntryId(id),
        userId = targetUserId,
        title = title,
        content = content,
        moodRating = moodRating,
        createdAt = createdAt,
        updatedAt = null,
        tags = emptySet()
    )
}
