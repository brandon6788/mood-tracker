package weather

import api.ErrorResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.weatherApiRoutes(weather: WeatherService) {
    get("/api/v1/weather") {
        val current = weather.currentWeather()
        if (current == null) call.respond(HttpStatusCode.BadGateway, ErrorResponse("Wetterdienst nicht erreichbar"))
        else call.respond(current)
    }
}
