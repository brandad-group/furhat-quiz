package furhatos.app.newskill.flow

import furhatos.app.newskill.flow.main.Idle
import furhatos.app.newskill.flow.main.Start
import furhatos.app.newskill.setting.DISTANCE_TO_ENGAGE
import furhatos.app.newskill.setting.MAX_NUMBER_OF_USERS
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
        furhat.setVoice(Language.GERMAN, Gender.FEMALE, true) // The last parameter tells the system to not also set the input language
        //furhat.setInputLanguage(Language.GERMAN)

    }
    onEntry {
        /** start interaction */
        when {
            furhat.isVirtual() -> goto(Start) // Convenient to bypass the need for user when running Virtual Furhat
            users.hasAny() -> {
                furhat.attend(users.random)
                goto(Start)
            }
            else -> goto(Idle)
        }
    }

}
