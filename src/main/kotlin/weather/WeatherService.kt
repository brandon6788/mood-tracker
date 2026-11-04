package weather

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.LocalDateTime
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun HttpClientConfig<*>.weatherClientConfig() {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

@Serializable
data class WeatherResponse(
    val observedAt: LocalDateTime,
    val temperature: Double,
    val condition: WeatherCondition,
    val description: String
)

class WeatherService(private val client: HttpClient) {

    suspend fun currentWeather(): WeatherResponse? = try {
        val forecast = client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", 54.78)
            parameter("longitude", 9.44)
            parameter("current", "temperature_2m,weather_code")
        }.body<ForecastResponse>().current
        WeatherResponse(forecast.time, forecast.temperature, forecast.condition, forecast.condition.description)
    } catch (_: ResponseException) {
        null
    } catch (_: IOException) {
        null
    }
}
