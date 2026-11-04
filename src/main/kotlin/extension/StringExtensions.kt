package extension

private val USERNAME_REGEX = Regex("^[A-Za-z0-9_]{3,20}$")
fun String.isValidEmail(): Boolean =
    this.contains('@') && this.contains('.')

fun String.isValidUsername(): Boolean =
    USERNAME_REGEX.matches(this)

fun String.normalizeTag(): String =
    trim()
        .lowercase()
        .replace(Regex("\\s+"), " ")
