package service

import extension.averageMood
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import model.*
import repository.MoodTrackerRepository

data class MoodReport(
    val averageMood: Double,
    val distribution: Map<MoodLevel, Int>,
    val bestEntry: Entry?
)

class MoodStatisticsService(private val repository: MoodTrackerRepository) {

    suspend fun createReport(userId: UserId): MoodReport = coroutineScope {
        val entries = repository.findAllEntries(userId)
        val average = async {
            average(entries)
        }

        val distribution = async {
            distribution(entries)
        }

        val best = async {
            best(entries)
        }

        MoodReport(average.await(), distribution.await(), best.await())
    }

    private suspend fun average(entries: List<Entry>): Double {
        delay(300)
        return entries.averageMood()
    }

    private suspend fun distribution(entries: List<Entry>): Map<MoodLevel, Int> {
        delay(300)
        return entries.mapNotNull {
            it.moodRating
        }.groupingBy {
            MoodLevel.fromRating(it)
        }.eachCount()
    }

    private suspend fun best(entries: List<Entry>): Entry? {
        delay(300)
        return entries.filter {
            it.moodRating != null
        }.maxByOrNull {
            it.moodRating ?: 0
        }
    }

    fun averageMoodUpdates(userId: UserId): Flow<Double> = repository.entries
        .map { entries ->
            entries.filter {
                it.userId == userId
            }.averageMood()
        }.distinctUntilChanged()
}
