package service

import extension.parseEntryFromCsv
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.UserId
import repository.MoodTrackerRepository

data class ImportProgress(

    val totalLines: Int,

    val processedLines: Int,

    val successfulImports: Int,

    val failedImports: Int

) {
    val isComplete: Boolean
        get() = processedLines == totalLines

    val successRate: Double
        get() = if (processedLines > 0)
            successfulImports.toDouble() / processedLines else 0.0
}

class CsvImportService(private val repository: MoodTrackerRepository) {

    fun importEntries(lines: List<String>, targetUserId: UserId): Flow<ImportProgress> = flow {
        val dataLines = lines.drop(1)
        val total = dataLines.size
        var processed = 0
        var ok = 0
        var fail = 0

        dataLines.forEach { line ->
            delay(50)
            try {
                val entry = line.parseEntryFromCsv(targetUserId)

                repository.addEntry(entry)
                ok++
            } catch (_: IllegalArgumentException) {
                fail++
            } finally {
                processed++
                emit(ImportProgress(total, processed, ok, fail))
            }
        }
    }
}
