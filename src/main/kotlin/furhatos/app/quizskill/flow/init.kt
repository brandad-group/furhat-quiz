package furhatos.app.quizskill.flow

import furhatos.app.quizskill.DataDelivery
import furhatos.app.quizskill.PORT
import furhatos.app.quizskill.buttons
import furhatos.app.quizskill.flow.main.Idle
import furhatos.app.quizskill.flow.main.AIGreeting
import furhatos.app.quizskill.inputFieldData
import furhatos.app.quizskill.setting.DISTANCE_TO_ENGAGE
import furhatos.app.quizskill.setting.MAX_NUMBER_OF_USERS
import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users
import furhatos.skills.HostedGUI
import furhatos.util.Gender
import furhatos.util.Language

// Our GUI declaration
val GUI = HostedGUI("GUI", "assets/gui", PORT)
val CLICK_BUTTON = "ClickButton"

// Starting state, before our GUI has connected.
val InitGUI: State = state(null) {
    include(wizardButtons)

    println("InitGUI")
    onEvent<SenseSkillGUIConnected> {
        goto(Init)
    }
}

val Init: State = state {
    include(wizardButtons)

    init {
        /** Set our default interaction parameters */
        users.setSimpleEngagementPolicy(DISTANCE_TO_ENGAGE, MAX_NUMBER_OF_USERS)
        furhat.setVoice(Language.GERMAN, Gender.FEMALE, true)
    }
    onEntry {
        send(DataDelivery(buttons = buttons, inputFields = inputFieldData.keys.toList()))
        /** start interaction */
        when {
            furhat.isVirtual() -> goto(Idle) // Convenient to bypass the need for user when running Virtual Furhat
            users.hasAny() -> {
                furhat.attend(users.random)
                goto(AIGreeting)
            }
            else -> goto(Idle)
        }
    }

}
