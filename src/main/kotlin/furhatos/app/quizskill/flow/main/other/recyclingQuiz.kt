package furhatos.app.quizskill.flow.main.other

import furhatos.app.quizskill.flow.Parent
import furhatos.app.quizskill.flow.main.Idle
import furhatos.app.quizskill.flow.wizardButtons
import furhatos.flow.kotlin.*

data class RecyclingQuestion(val article: String, val item: String, val bin: Bin)

val questions = listOf(
    RecyclingQuestion("ein", "Taschentuch", Bin.PAPIERMUELL),
    RecyclingQuestion("eine", "Bananenschale", Bin.BIOMUELL),
    RecyclingQuestion("eine", "Milchtüte", Bin.PLASTIKMUELL),
    RecyclingQuestion("eine", "Zeitung", Bin.PAPIERMUELL),
    RecyclingQuestion("eine", "Brotkruste", Bin.BIOMUELL),
    RecyclingQuestion("eine", "Plastikflasche", Bin.PLASTIKMUELL),
    RecyclingQuestion("ein", "Messer", Bin.RESTMUELL),
    RecyclingQuestion("ein", "Aludeckel", Bin.WERTSTOFFCONTAINER),
    RecyclingQuestion("eine", "Batterie", Bin.SONDERMUELL),
    RecyclingQuestion("eine", "Weinflasche", Bin.GLASCONTAINER),
    RecyclingQuestion("eine", "Pizza-Box", Bin.PAPIERMUELL),
    RecyclingQuestion("einen", "Joghurtbecher", Bin.PLASTIKMUELL),
    RecyclingQuestion("eine", "Eierschale", Bin.BIOMUELL),
    RecyclingQuestion("einen", "Kugelschreiber", Bin.RESTMUELL),
    RecyclingQuestion("eine", "Dose", Bin.WERTSTOFFCONTAINER),
    RecyclingQuestion("eine", "Kerze", Bin.RESTMUELL),
    RecyclingQuestion("eine", "Schraube", Bin.WERTSTOFFCONTAINER),
    RecyclingQuestion("eine", "CD", Bin.SONDERMUELL),
    RecyclingQuestion("eine", "Gemüseschale", Bin.BIOMUELL),
    RecyclingQuestion("einen", "Pappkarton", Bin.PAPIERMUELL),
    RecyclingQuestion("einen", "Nagellack", Bin.SONDERMUELL),
    RecyclingQuestion("eine", "Glühbirne", Bin.SONDERMUELL),
    RecyclingQuestion("einen", "Briefumschlag", Bin.PAPIERMUELL),
    RecyclingQuestion("ein", "Salatblatt", Bin.BIOMUELL),
    RecyclingQuestion("eine", "Plastiktüte", Bin.PLASTIKMUELL)
)

enum class Bin(val names: List<String>) {

    PAPIERMUELL(listOf("Papier", "Papiermüll", "Papier Müll")),
    RESTMUELL(listOf("Rest", "Restmüll")),
    BIOMUELL(listOf("Bio", "Biotonne", "Biomüll")),
    PLASTIKMUELL(listOf("Plastik", "Plastikmüll", "Kunststoffbehälter")),
    WERTSTOFFCONTAINER(listOf("Wertstoffcontainer", "Wertstoffmüll")),
    SONDERMUELL(listOf("Sondermüll")),
    GLASCONTAINER(listOf("Glascontainer"));

    companion object {
        fun fromName(name: String): Bin? {
            for (enum in values()) {
                if (name in enum.names) {
                    return enum
                }
            }
            return null
        }
    }
}

private const val NUM_QUESTIONS = 3
var successCounter = 0

val Quiz : State = state(Parent) {
    include(wizardButtons)

    onEntry {
        furhat.say("Willkommen beim Recycling-Quiz. Ich stelle Dir jetzt $NUM_QUESTIONS Fragen.")
        val randomQuestions = questions.shuffled().take(NUM_QUESTIONS)
        successCounter = 0
        goto(askQuestion(randomQuestions))
    }
}

fun askQuestion(questions: List<RecyclingQuestion>) : State = state {
    lateinit var question: RecyclingQuestion

    onEntry {
        question = questions.first()
        furhat.ask("In welchen Müllbehälter wirft man ${question.article} ${question.item}?")
    }

    onResponse {
        println("User response: $it.text")
        val userResponse = it.text
        val responseBin = Bin.fromName(userResponse)
        println("responseBin: $responseBin")
        if (responseBin == question.bin) {
            successCounter++
            furhat.say("Richtig!")
        } else {
            var article = question.article
            if (article == "einen")
                article = "ein"
            furhat.say("Falsch. ${article.capitalize()} ${question.item} gehört in den ${question.bin}.")
        }
        val remainingQuestions = questions.drop(1)
        if (remainingQuestions.isNotEmpty()) {
            goto(askQuestion(remainingQuestions))
        } else {
            println("successCounter: $successCounter")
            val successCounterStr = if (successCounter == 1) "eine" else successCounter.toString()
            furhat.say("Das war's. Danke für's Mitmachen! " +
                    "Du hast $successCounterStr von $NUM_QUESTIONS Fragen richtig beantwortet.")

            goto(Idle)
        }
    }
}
