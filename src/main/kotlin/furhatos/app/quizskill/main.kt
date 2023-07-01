package furhatos.app.quizskill

import furhatos.app.quizskill.flow.InitGUI
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class QuizSkill : Skill() {
    override fun start() {
        Flow().run(InitGUI)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
