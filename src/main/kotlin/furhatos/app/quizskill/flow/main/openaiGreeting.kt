package furhatos.app.quizskill.flow.main

import com.theokanning.openai.service.OpenAiService
import furhatos.app.quizskill.flow.Parent
import furhatos.app.quizskill.flow.wizardButtons
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Goodbye
import java.util.concurrent.atomic.AtomicInteger

val apikey = System.getenv("OPENAI_API_KEY" )
val service = OpenAiService(apikey)
var counter = AtomicInteger(0)

val AIGreeting: State = state(Parent) {
    include(wizardButtons)

    onEntry {
        furhat.ask("Wir spielen das Spiel '20 Fragen'. Du denkst an etwas und ich muss es erraten")
    }

    onResponse<Goodbye> {
        furhat.say("Tschüss")
        goto(Idle)
    }

    onResponse {
        if (counter.get() < 20) {
            val robotResponse = call {
                getDialogChatCompletion(service, counter)
            } as String?
            furhat.ask(robotResponse?:"Kannst Du das bitte wiederholen?")
        } else {
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