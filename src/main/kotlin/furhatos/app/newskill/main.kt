package furhatos.app.newskill

import furhatos.app.newskill.flow.Init
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class NewskillSkill : Skill() {
    override fun start() {
        Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
