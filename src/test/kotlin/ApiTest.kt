import api.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import model.EntryId
import repository.MoodTrackerRepository
import weather.*
import kotlin.test.*

private const val FORECAST_JSON =
    """{"latitude":54.78,"longitude":9.44,"elevation":11.0,"current_units":{"temperature_2m":"°C"},"current":{"time":"2026-10-28T10:15","interval":900,"temperature_2m":13.2,"weather_code":61}}"""
class ApiTest {

    private fun apiTest(
        forecast: suspend RoutingContext.() -> Unit = {
            call.respondText(FORECAST_JSON, ContentType.Application.Json)
        }, block: suspend ApplicationTestBuilder.(MoodTrackerRepository, HttpClient) -> Unit
    ) = testApplication {
        externalServices {
            hosts("https://api.open-meteo.com") {
                routing {
                    get("/v1/forecast") {
                        forecast()
                    }
                }
            }
        }

        val repository = MoodTrackerRepository().apply {
            initializeWithTestData()
        }

        val weather = WeatherService(createClient {
            weatherClientConfig()
        })
        application {
            module(repository, weather)
        }

        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        block(repository, jsonClient)
    }

    @Test
    fun list() = apiTest { _, client ->
        val response = client.get("/api/v1/entries")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters())
        assertEquals(8, response.body<List<EntryResponse>>().size)
    }

    @Test
    fun missing() = apiTest { _, client ->
        val response = client.get("/api/v1/entries/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Eintrag nicht gefunden", response.body<ErrorResponse>().error)
    }

    @Test
    fun create() = apiTest { repository, client ->
        val response = client.post("/api/v1/entries") {
            contentType(ContentType.Application.Json)
            setBody(CreateEntryRequest("Spaziergang", "Am Hafen.", 7))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val entry = response.body<EntryResponse>()
        assertEquals("/api/v1/entries/${entry.id}", response.headers[HttpHeaders.Location])
        assertEquals("Spaziergang", repository.findEntryById(EntryId(entry.id))?.title)
    }

    @Test
    fun invalid() = apiTest { _, client ->
        val response = client.post("/api/v1/entries") {
            contentType(ContentType.Application.Json)
            setBody(CreateEntryRequest("", "Text", 11))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(2, response.body<ErrorResponse>().details.size)
    }

    @Test
    fun delete() = apiTest { _, client ->
        assertEquals(HttpStatusCode.NoContent, client.delete("/api/v1/entries/1").status)
        assertEquals(HttpStatusCode.NotFound, client.delete("/api/v1/entries/1").status)
    }

    @Test
    fun weather() = apiTest { _, client ->
        val response = client.get("/api/v1/weather")
        assertEquals(HttpStatusCode.OK, response.status)
        val weather = response.body<WeatherResponse>()
        assertEquals(WeatherCondition.RAIN, weather.condition)
        assertEquals(13.2, weather.temperature)
    }
}
