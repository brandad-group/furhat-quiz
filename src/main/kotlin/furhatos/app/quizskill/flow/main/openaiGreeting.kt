package furhatos.app.quizskill.flow.main

import com.theokanning.openai.service.OpenAiService
import furhatos.app.quizskill.DataDelivery
import furhatos.app.quizskill.SPEECH_DONE
import furhatos.app.quizskill.buttons
import furhatos.app.quizskill.flow.CLICK_BUTTON
import furhatos.app.quizskill.flow.Parent
import furhatos.app.quizskill.flow.wizardButtons
import furhatos.app.quizskill.inputFieldData
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
                furhat.ask(gptResponseText,
                    //interruptable=true,  //recht empfindlich, ggf. mit Mikro eher sinnvoll
                    timeout = 10000 //Wartezeit auf User Antwort
                )

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

    onEvent(CLICK_BUTTON) {
        val data = it.get("data")
        println("data: $data")

        if (data.equals("Stop")){
            goto(Idle)
        }
    }
}
 fun wannaPlayPhrases() = getRandomString(listOf(
    "Hast Du Lust zu spielen?",
    "Sollen wir das Mülltrennungsspiel spielen?",
    "Wie wäre es mit einer Runde Mülltrennungsspiel?"))

fun getRandomString(list: List<String>) : String {
    return list[Random().nextInt(list.size)]
}
