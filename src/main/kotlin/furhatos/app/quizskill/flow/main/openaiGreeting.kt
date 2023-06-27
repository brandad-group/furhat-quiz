package furhatos.app.quizskill.flow.main

import com.theokanning.openai.service.OpenAiService
import furhatos.app.quizskill.flow.Parent
import furhatos.app.quizskill.flow.wizardButtons
import furhatos.app.quizskill.setting.Config
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Goodbye
import java.util.concurrent.atomic.AtomicInteger
import java.util.*

val service = OpenAiService(Config.openaiApiKey)
var counter = AtomicInteger(0)
const val numQuestions = 5
const val maxRounds = 30
const val GPT_END_TOKEN = "ENDETOKEN"

val AIGreeting: State = state(Parent) {
    include(wizardButtons)

    onEntry {
        counter.set(0)
        Furhat.dialogHistory.clear()

        furhat.ask(wannaPlayPhrases())
    }

    onResponse<Goodbye> {
        furhat.say("Tschüss")
        goto(Idle)
    }

    onResponse {
        if (counter.get() < maxRounds) {
            val gptResponse = call {
                getDialogChatCompletionMuellSpiel(service, counter, numQuestions)
            } as String?

            var gptResponseText = gptResponse ?: "Kannst Du das bitte wiederholen?"
            if (gptResponseText.contains(GPT_END_TOKEN)){
                println("game ended by gpt")
                gptResponseText = gptResponseText.removeSuffix(GPT_END_TOKEN)
                furhat.say(gptResponseText)
            } else {
                furhat.ask(gptResponseText, timeout = 10000)
            }
        } else {
            //harter Ausstieg
            furhat.say("Danke fürs spielen")
            println("game ended")
            counter.set(0)
            goto(Idle)
        }
    }

    onNoResponse {
        furhat.ask("Ich habe leider nichts gehört")
    }
}
 fun wannaPlayPhrases() = getRandomString(listOf(
    "Hast Du Lust zu spielen?",
    "Sollen wir das Mülltrennungsspiel spielen?",
    "Wie wäre es mit einer Runde Mülltrennungsspiel?"))

fun getRandomString(list: List<String>) : String {
    return list[Random().nextInt(list.size)]
}
