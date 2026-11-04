package api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import repository.MoodTrackerRepository
import service.MoodStatisticsService
import web.*

private suspend fun ApplicationCall.entryNotFound() =
    respond(HttpStatusCode.NotFound, ErrorResponse("Eintrag nicht gefunden"))

fun Route.entryApiRoutes(repository: MoodTrackerRepository, statistics: MoodStatisticsService) {
    route("/api/v1") {
        get("/entries") {
            call.respond(repository.loadEntries(call.request.queryParameters["minMood"]).map {
                it.toResponse()
            })
        }

        get("/entries/{id}") {
            val entry = call.entryId()?.let {
                repository.findEntryById(it)
            }

            if (entry == null) call.entryNotFound() else call.respond(entry.toResponse())
        }

        post("/entries") {
            val request = try {
                call.receive<CreateEntryRequest>()
            } catch (_: BadRequestException) {
                return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ungültiger Request-Body"))
            }

            val errors = request.validate()
            if (errors.isNotEmpty()) return@post call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Validierung fehlgeschlagen", errors)
            )
            val entry = repository.addEntry(request.toEntry(repository.nextEntryId(), CURRENT_USER))
            call.response.header(HttpHeaders.Location, "/api/v1/entries/${entry.id.value}")
            call.respond(HttpStatusCode.Created, entry.toResponse())
        }

        delete("/entries/{id}") {
            val deleted = call.entryId()?.let {
                repository.deleteEntry(it)
            } ?: false
            if (deleted) call.respond(HttpStatusCode.NoContent) else call.entryNotFound()
        }

        get("/stats") {
            call.respond(statistics.createReport(CURRENT_USER).toResponse())
        }
    }
}
