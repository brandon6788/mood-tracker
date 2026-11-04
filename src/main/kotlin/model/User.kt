package model

import kotlinx.datetime.LocalDate
import extension.daysSince
import kotlin.jvm.JvmInline

@JvmInline
value class UserId(val value: Long)

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val passwordHash: String,
    val registrationDate: LocalDate,
    val isActive: Boolean = true
) {
    val accountAge: Int
        get() = registrationDate.daysSince()

    val isNewUser: Boolean
        get() = accountAge < 7

    fun deactivate(): User = copy(isActive = false)

    fun activate(): User = copy(isActive = true)
}
