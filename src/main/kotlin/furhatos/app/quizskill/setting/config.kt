package furhatos.app.quizskill.setting

import java.util.Properties
import java.io.FileReader

object Config {
    val openaiApiKey: String

    init {
        val props = Properties()
        val reader = FileReader("src/main/resources/secrets.properties")

        props.load(reader)

        openaiApiKey = props.getProperty("OPENAI_API_KEY")
    }
}
