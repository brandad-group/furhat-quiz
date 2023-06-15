package furhatos.app.quizskill.flow

import furhatos.app.quizskill.flow.main.Idle
import furhatos.app.quizskill.flow.main.Quiz
import furhatos.app.quizskill.setting.DISTANCE_TO_ENGAGE
import furhatos.app.quizskill.setting.MAX_NUMBER_OF_USERS
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users
import furhatos.util.Gender
import furhatos.util.Language

val Init: State = state {
    init {
        /** Set our default interaction parameters */
        users.setSimpleEngagementPolicy(DISTANCE_TO_ENGAGE, MAX_NUMBER_OF_USERS)
        furhat.setVoice(Language.GERMAN, Gender.FEMALE, true)
    }
    onEntry {
        /** start interaction */
        when {
            furhat.isVirtual() -> goto(Idle) // Convenient to bypass the need for user when running Virtual Furhat
            users.hasAny() -> {
                furhat.attend(users.random)
                goto(Quiz)
            }
            else -> goto(Idle)
        }
    }

}
