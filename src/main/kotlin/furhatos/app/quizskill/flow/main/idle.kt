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
        furhat.attendNobody()
    }

    onUserEnter {
        furhat.attend(it)
        goto(Quiz)
    }

    onEvent(CLICK_BUTTON) {
        val data = it.get("data")
        if (data.equals("Quiz")) {
            goto(AIGreeting)
        }
    }

}
