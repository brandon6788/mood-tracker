package extension

import kotlinx.datetime.*
import kotlinx.datetime.format.char
import kotlin.time.Clock

fun currentDate(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
fun currentDateTime(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
fun LocalDate.isToday(): Boolean = this == currentDate()
fun LocalDate.isInPast(): Boolean = this < currentDate()
fun LocalDate.daysSince(): Int = daysUntil(currentDate())
private val germanDate = LocalDate.Format {
    day()
    char('.')
    monthNumber()
    char('.')
    year()
}

private val germanDateTime = LocalDateTime.Format {
    date(germanDate)
    char(' ')
    hour()
    char(':')
    minute()
}

fun LocalDateTime.toDateString(): String = germanDate.format(date)
fun LocalDateTime.toDateTimeString(): String = germanDateTime.format(this)
