package repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import model.*

class MoodTrackerRepository {
    private val _entries = MutableStateFlow<List<Entry>>(emptyList())

    val entries: StateFlow<List<Entry>> = _entries.asStateFlow()

    suspend fun findAllEntries(userId: UserId): List<Entry> {
        delay(100)
        return entries.value.filter {
            it.userId == userId
        }
    }

    suspend fun addEntry(entry: Entry): Entry {
        delay(80)
        _entries.update {
            it + entry
        }

        return entry
    }

    fun nextEntryId(): EntryId = EntryId((entries.value.maxOfOrNull {
        it.id.value
    } ?: 0) + 1)

    suspend fun findEntryById(entryId: EntryId): Entry? {
        delay(50)
        return entries.value.firstOrNull {
            it.id == entryId
        }
    }

    suspend fun deleteEntry(entryId: EntryId): Boolean {
        delay(70)
        return _entries.getAndUpdate { list ->
            list.filterNot {
                it.id == entryId
            }
        }.any {
            it.id == entryId
        }
    }

    fun initializeWithTestData() {
        _entries.value = listOf(
            testEntry(1, 7, "Toller Tag", "Projekt abgeschlossen.", 8),
            testEntry(2, 6, "Müde", "Lange Nacht.", 4),
            testEntry(3, 5, "Motiviert", "Viel geschafft.", 9),
            testEntry(4, 4, "Normaler Tag", "Nichts passiert.", null),
            testEntry(5, 3, "Gestresst", "Viel Arbeit.", 3),
            testEntry(6, 2, "Entspannt", "Zeit für mich.", 7),
            testEntry(7, 1, "Produktiv", "Aufgaben erledigt.", 8),
            testEntry(8, 0, "Ruhig", "Spazieren gegangen.", 6)
        )
    }

    private fun testEntry(id: Long, daysAgo: Int, title: String, content: String, moodRating: Int?) =
        Entry(
            EntryId(id), UserId(1), title, content, moodRating,
            (Clock.System.now() - daysAgo.days).toLocalDateTime(TimeZone.currentSystemDefault())
        )
}
