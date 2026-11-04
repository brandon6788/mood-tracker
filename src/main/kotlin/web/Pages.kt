package web

import extension.*
import kotlinx.html.*
import model.*
import service.MoodReport

fun HTML.page(pageTitle: String, content: FlowContent.() -> Unit) {
    head {
        meta(charset = "utf-8")
        title {
            +"$pageTitle – MoodTracker"
        }

        link(rel = "stylesheet", href = "/static/styles.css")
    }

    body {
        header {
            nav {
                a("/") {
                    +"Einträge"
                }
                
                a("/stats") {
                    +"Statistik"
                }
            }
        }

        main {
            h1 {
                +pageTitle
            }
            
            content()
        }
    }
}

private fun Entry.moodText(): String = moodRating?.let {
    val level = it.toMoodLevel()
    "$it/10 ${level.emoji} (${level.displayName})"
} ?: "keine Angabe"

fun HTML.homePage(
    entries: List<Entry>,
    minMood: String = "",
    form: EntryForm = EntryForm(),
    errors: List<String> = emptyList()
) = page("Meine Einträge") {
    form(action = "/", method = FormMethod.get, classes = "filter") {
        label {
            +"Mindeststimmung"
            input(InputType.number, name = "minMood") {
                value = minMood
                min = "1"
                max = "10"
            }
        }

        button {
            +"Filtern"
        }
    }

    if (entries.isEmpty()) p {
        +"Noch keine Einträge."
    }

    entries.forEach {
        entryCard(it)
    }

    h2 {
        +"Neuer Eintrag"
    }

    entryForm(form, errors)
}

fun FlowContent.entryForm(form: EntryForm, errors: List<String>) {
    if (errors.isNotEmpty()) ul("errors") {
        errors.forEach {
            li {
                +it
            }
        }
    }

    form(action = "/entries", method = FormMethod.post) {
        label {
            +"Titel"
            input(InputType.text, name = "title") {
                value = form.title
            }
        }

        label {
            +"Inhalt"
            textArea {
                name = "content"
                +form.content
            }
        }

        label {
            +"Stimmung"
            input(InputType.number, name = "moodRating") {
                value = form.moodRating
                min = "1"
                max = "10"
            }
        }

        button(type = ButtonType.submit) {
            +"Speichern"
        }
    }
}

fun FlowContent.deleteButton(entry: Entry) {
    form(action = "/entries/${entry.id.value}/delete", method = FormMethod.post) {
        button(type = ButtonType.submit, classes = "danger") {
            +"Löschen"
        }
    }
}

fun FlowContent.entryCard(entry: Entry) {
    article {
        h2 {
            a("/entries/${entry.id.value}") {
                +entry.title
            }
        }

        p {
            +(entry.content.take(100) + if (entry.content.length > 100) "…" else "")
        }

        p("meta") {
            +"${entry.moodText()} | ${entry.createdAt.toDateString()}"
        }

        deleteButton(entry)
    }
}

fun HTML.detailPage(entry: Entry) = page(entry.title) {
    p {
        +entry.content
    }

    p {
        +entry.moodText()
    }

    p {
        +"Erstellt: ${entry.createdAt.toDateTimeString()}"
    }

    entry.updatedAt?.let {
        p {
            +"Bearbeitet: ${it.toDateTimeString()}"
        }
    }

    p {
        +"Wortanzahl: ${entry.wordCount}"
    }

    deleteButton(entry)
    a("/") {
        +"Zurück"
    }
}

fun HTML.statisticsPage(report: MoodReport) = page("Statistik") {
    p {
        +"Durchschnitt: ${"%.1f".format(report.averageMood)}"
    }

    table {
        tr {
            th {
                +"Stimmung"
            }
            
            th {
                +"Anzahl"
            }
        }

        MoodLevel.entries.forEach { level ->
            tr {
                td {
                    +level.displayName
                }
                
                td {
                    +"${report.distribution[level] ?: 0}"
                }
            }
        }
    }

    report.bestEntry?.let {
        a("/entries/${it.id.value}") {
            +"Bester Eintrag: ${it.title}"
        }
    }
}

fun HTML.notFoundPage() = page("Eintrag nicht gefunden") {
    a("/") {
        +"Zurück"
    }
}
