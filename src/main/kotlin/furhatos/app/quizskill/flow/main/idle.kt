package furhatos.app.quizskill.flow.main

import furhatos.app.quizskill.DataDelivery
import furhatos.app.quizskill.SPEECH_DONE
import furhatos.app.quizskill.buttons
import furhatos.app.quizskill.flow.CLICK_BUTTON
import furhatos.app.quizskill.flow.main.other.Quiz
import furhatos.app.quizskill.flow.wizardButtons
import furhatos.app.quizskill.inputFieldData
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onUserEnter
import furhatos.flow.kotlin.state

val Idle: State = state {
    include(wizardButtons)

    onEntry {
        println("entering idle state")
        //furhat.attendNobody()
        //goto(NoGUI)
        send(DataDelivery(buttons = buttons, inputFields = inputFieldData.keys.toList()))
    }

    onUserEnter {
        furhat.attend(it)
        goto(Quiz)
    }

    //    // Users clicked any of our buttons
    onEvent(CLICK_BUTTON) {
        val data = it.get("data")
        println("data: $data")

        // Directly respond with the value we get from the event, with a fallback
        // furhat.say("You pressed ${data ?: "something I'm not aware of" }")

        if (data.equals("Quiz")) {
            goto(AIGreeting)
        }

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
    }

}
