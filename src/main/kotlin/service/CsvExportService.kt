package service

import extension.*
import model.UserId
import repository.MoodTrackerRepository

class CsvExportService(private val repository: MoodTrackerRepository) {

    suspend fun exportEntries(userId: UserId): Sequence<String> {
        val entries = repository.findAllEntries(userId)
        return sequence {
            yield(CSV_HEADER)
            yieldAll(entries.asSequence().map {
                it.toCsvLine()
            })
        }
    }
}
