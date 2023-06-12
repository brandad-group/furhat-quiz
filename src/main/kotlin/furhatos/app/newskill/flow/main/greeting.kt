package furhatos.app.newskill.flow.main

import furhatos.app.newskill.flow.Parent
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes

val Greeting: State = state(Parent) {
    onEntry {
        furhat.ask("Should I say Hello World?")
    }

    onResponse<Yes> {
        furhat.say("Hello World! ")
    }

    onResponse<No> {
        furhat.say("Ok.")
    }

}

