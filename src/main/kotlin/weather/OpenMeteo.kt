package weather

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class ForecastResponse(val current: CurrentWeather)

@Serializable
data class CurrentWeather(
    val time: LocalDateTime,

    @SerialName("temperature_2m")
    val temperature: Double,

    @SerialName("weather_code")
    @Serializable(with = WeatherCodeSerializer::class)
    val condition: WeatherCondition
)

object WeatherCodeSerializer : KSerializer<WeatherCondition> {
    override val descriptor = PrimitiveSerialDescriptor("WeatherCode", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder) =
        WeatherCondition.fromCode(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: WeatherCondition) =
        encoder.encodeInt(value.codes.firstOrNull() ?: -1)
}
