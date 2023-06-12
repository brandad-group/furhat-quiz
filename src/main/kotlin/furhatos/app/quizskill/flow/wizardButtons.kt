package furhatos.app.quizskill.flow

import furhatos.app.quizskill.flow.main.Quiz
import furhatos.flow.kotlin.Color
import furhatos.flow.kotlin.Section
import furhatos.flow.kotlin.partialState

val wizardButtons = partialState {

    onButton("Go to quiz", section = Section.LEFT, color = Color.Green) {
        goto(Quiz)
    }
}