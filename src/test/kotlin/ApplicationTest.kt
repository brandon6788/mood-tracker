import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import repository.MoodTrackerRepository
import model.UserId
import kotlin.test.*

class ApplicationTest {

    private fun htmlTest(block: suspend ApplicationTestBuilder.(MoodTrackerRepository) -> Unit) = testApplication {
        val repository = MoodTrackerRepository().apply {
            initializeWithTestData()
        }

        application {
            module(repository)
        }

        block(repository)
    }

    @Test

    fun home() = htmlTest {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Toller Tag")
    }

    @Test

    fun filter() = htmlTest {
        val text = client.get("/?minMood=8").bodyAsText()
        assertContains(text, "Motiviert")
        assertFalse(text.contains("Müde"))
    }

    @Test

    fun create() = htmlTest { repository ->
        val browser = createClient {
            followRedirects = false
        }

        val response = browser.submitForm("/entries", parameters {
            append("title", "Neu")
            append("content", "Text")
            append("moodRating", "7")
        })
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/", response.headers[HttpHeaders.Location])
        assertTrue(repository.findAllEntries(UserId(1)).any {
            it.title == "Neu"
        })
    }

    @Test

    fun invalid() = htmlTest {
        val response = client.submitForm("/entries", parameters {
            append("title", "")
            append("content", "Behalten")
        })
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Titel darf nicht leer sein")
        assertContains(response.bodyAsText(), "Behalten")
    }
}
