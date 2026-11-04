package weather

enum class WeatherCondition(val description: String, val codes: Set<Int>) {
    CLEAR("Klar", setOf(0)),
    CLOUDY("Bewölkt", setOf(1, 2, 3)),
    FOG("Nebel", setOf(45, 48)),
    RAIN("Regen", setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82)),
    SNOW("Schnee", setOf(71, 73, 75, 77, 85, 86)),
    THUNDERSTORM("Gewitter", setOf(95, 96, 99)),
    UNKNOWN("Unbekannt", emptySet());

    companion object {
        fun fromCode(code: Int): WeatherCondition = entries.firstOrNull {
            code in it.codes
        } ?: UNKNOWN
    }
}
