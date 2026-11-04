import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import repository.MoodTrackerRepository
import service.MoodStatisticsService
import web.*
import api.*
import weather.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module(
    repository: MoodTrackerRepository = MoodTrackerRepository().apply {
        initializeWithTestData()
    },
    weather: WeatherService = WeatherService(HttpClient(CIO) {
        weatherClientConfig()
    })
) {
    install(CallLogging) {
        level = Level.INFO
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }

    val statistics = MoodStatisticsService(repository)
    routing {
        staticResources("/static", "static")
        entryRoutes(repository)
        statisticsRoutes(statistics)
        entryApiRoutes(repository, statistics)
        weatherApiRoutes(weather)
    }
}
