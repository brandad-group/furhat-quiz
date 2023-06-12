package furhatos.app.quizskill.flow.main

import furhatos.app.quizskill.flow.wizardButtons
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

}
