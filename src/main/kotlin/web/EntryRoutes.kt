package web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.*
import repository.MoodTrackerRepository
import service.MoodStatisticsService

val CURRENT_USER = UserId(1)
fun ApplicationCall.entryId(): EntryId? = parameters["id"]?.toLongOrNull()?.let {
    EntryId(it)
}

suspend fun MoodTrackerRepository.loadEntries(minMood: String? = null): List<Entry> {
    val minimum = minMood?.toIntOrNull()?.takeIf {
        it in 1..10
    }

    return findAllEntries(CURRENT_USER).filter {
        minimum == null || (it.moodRating ?: 0) >= minimum
    }.sortedByDescending {
        it.createdAt
    }
}

fun Route.entryRoutes(repository: MoodTrackerRepository) {
    get("/") {
        val minimum = call.request.queryParameters["minMood"]
        val entries = repository.loadEntries(minimum)
        call.respondHtml {
            homePage(entries, minimum.orEmpty())
        }
    }

    post("/entries") {
        val form = call.receiveParameters().toEntryForm()
        val errors = form.validate()
        if (errors.isEmpty()) {
            repository.addEntry(form.toEntry(repository.nextEntryId(), CURRENT_USER))
            call.respondRedirect("/")
        } else {
            val entries = repository.loadEntries()
            call.respondHtml(HttpStatusCode.BadRequest) {
                homePage(entries, form = form, errors = errors)
            }
        }
    }

    get("/entries/{id}") {
        val entry = call.entryId()?.let {
            repository.findEntryById(it)
        }

        if (entry == null) call.respondHtml(HttpStatusCode.NotFound) {
            notFoundPage()
        }
        else call.respondHtml {
            detailPage(entry)
        }
    }

    post("/entries/{id}/delete") {
        call.entryId()?.let {
            repository.deleteEntry(it)
        }

        call.respondRedirect("/")
    }
}

fun Route.statisticsRoutes(statistics: MoodStatisticsService) {
    get("/stats") {
        val report = statistics.createReport(CURRENT_USER)
        call.respondHtml {
            statisticsPage(report)
        }
    }
}
