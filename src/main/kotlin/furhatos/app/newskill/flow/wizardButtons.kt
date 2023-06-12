package furhatos.app.newskill.flow

import furhatos.app.newskill.flow.main.Start
import furhatos.flow.kotlin.Color
import furhatos.flow.kotlin.Section
import furhatos.flow.kotlin.partialState

val wizardButtons = partialState {
    onButton("Go to quiz", section = Section.LEFT, color = Color.Green) {
        goto(Start)
    }

//    onButton("Play quiz", section = Section.RIGHT, color = Color.Yellow) {
//        goto(RequireUsers(Idle))
//    }
}