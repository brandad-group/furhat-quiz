package furhatos.app.quizskill

import furhatos.app.quizskill.flow.main.AIGreeting
import furhatos.app.quizskill.flow.main.Idle
import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.skills.HostedGUI
//
//// Our GUI declaration
//val GUI = HostedGUI("GUI", "assets/gui", PORT)
//val CLICK_BUTTON = "ClickButton"
//
//// Starting state, before our GUI has connected.
//val NoGUI: State = state(null) {
//    println("NoGUI")
//    onEvent<SenseSkillGUIConnected> {
//        goto(GUIConnected)
//    }
//
//}
//
///*
//    Here we know our GUI has connected. Since the user might close down the GUI and then reopen
//    again, we inherit our handler from the NoGUI state. An edge case might be that a second GUI
//    is opened, but this is not accounted for here.
//
// */
//val GUIConnected = state(NoGUI) {
//    onEntry {
//        println("GUIConnected")
//        // Pass data to GUI
//        send(DataDelivery(buttons = buttons, inputFields = inputFieldData.keys.toList()))
//    }
//
//    // Users clicked any of our buttons
//    onEvent(CLICK_BUTTON) {
//        val data = it.get("data")
//        println("data: $data")
//
//        // Directly respond with the value we get from the event, with a fallback
//        // furhat.say("You pressed ${data ?: "something I'm not aware of" }")
//
//        if (data.equals("Quiz")) {
//            goto(AIGreeting)
//        }
//        if (data.equals("Idle")){
//            goto(Idle)
//            //goto(NoGUI)
//            //goto(GUIConnected)
//        }
//
//        // Let the GUI know we're done speaking, to unlock buttons
//        send(SPEECH_DONE)
//    }
//
//}