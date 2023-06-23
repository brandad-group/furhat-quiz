package furhatos.app.quizskill.flow.main

import com.theokanning.openai.service.OpenAiService
import furhatos.app.quizskill.flow.Parent
import furhatos.app.quizskill.flow.wizardButtons
import furhatos.app.quizskill.setting.Config
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Goodbye
import java.util.concurrent.atomic.AtomicInteger

val service = OpenAiService(Config.openaiApiKey)
var counter = AtomicInteger(0)

val AIGreeting: State = state(Parent) {
    include(wizardButtons)

    onEntry {
        furhat.ask("Hast Du Lust zu spielen?")
    }

    onResponse<Goodbye> {
        furhat.say("Tschüss")
        goto(Idle)
    }

    onResponse {
        if (counter.get() < 20) {
            val robotResponse = call {
                //getDialogChatCompletion20Questions(service, counter)
                getDialogChatCompletionMuellSpiel(service, counter)
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