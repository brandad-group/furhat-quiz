package furhatos.app.quizskill.flow

import furhatos.app.quizskill.flow.main.AIGreeting
import furhatos.app.quizskill.flow.main.Idle
import furhatos.app.quizskill.flow.main.other.Quiz
import furhatos.flow.kotlin.Color
import furhatos.flow.kotlin.Section
import furhatos.flow.kotlin.partialState

val wizardButtons = partialState {

    onButton("Local recycling quiz", section = Section.LEFT, color = Color.Green) {
        goto(Quiz)
    }

    onButton("GPT Quiz", section = Section.LEFT, color = Color.Yellow) {
        goto(AIGreeting)
    }

    onButton("Idle", section = Section.LEFT, color = Color.Red) {
        goto(Idle)
    }
}