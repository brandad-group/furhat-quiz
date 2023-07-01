package furhatos.app.quizskill

import furhatos.app.quizskill.flow.Init
import furhatos.app.quizskill.flow.InitGUI
import furhatos.flow.kotlin.Flow
import furhatos.skills.HostedGUI
import furhatos.skills.Skill

class QuizSkill : Skill() {
    override fun start() {
        //Flow().run(Init)
        Flow().run(InitGUI)
        //Flow().run(NoGUI)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
