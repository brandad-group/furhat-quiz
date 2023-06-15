package furhatos.app.quizskill.flow.main

import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.service.OpenAiService
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat


fun getDialogCompletion(service: OpenAiService, counter: Int): String? {
    //println("counter: ${furhatos.app.quizskill.flow.main.counter++}")
    val agentName = "Robot"
    //val description = "The following is a conversation between a $agentName and a Human. The $agentName is friendly and helpful."
    val description = "Das 20-Fragen-Spiel ist ein einfaches, aber lustiges Ratespiel. In diesem Spiel denke ich an etwas " +
            "und du stellst bis zu 20 Ja/Nein-Fragen, um herauszufinden, woran ich denke. Versuche bei deinen Antworten lustig " +
            "und abwechslungsreich zu sein. Sage ab und zu etwas wie 'Oh, da muss ich mal nachdenken' oder 'Hmm, ich bin mir nicht sicher'. " +
            "Wenn du denkst, dass du weißt, woran ich denke, sagst du 'Ich denke, es ist ein ...'.  Wenn ich es dann bestätige ist das Spiel zu Ende." +
            "Dann sagst Du 'Ich habe gewonnen'. "

    val contextWindowSize = 10
    val history = Furhat.dialogHistory.all.takeLast(contextWindowSize).mapNotNull {
        when (it) {
            is DialogHistory.ResponseItem -> {
                "  Human: ${it.response.text}"
            }
            is DialogHistory.UtteranceItem -> {
                "  $agentName: ${it.toText()}"
            }
            else -> null
        }
    }.joinToString(separator = "\n")
    val prompt = "$description\n\n$history\n$agentName:"
    println("\nprompt: $prompt")

    val completionRequest = CompletionRequest.builder()
        .temperature(0.9)
        .maxTokens(50)
        .topP(1.0)
        .frequencyPenalty(0.0)
        .presencePenalty(0.6)
        .prompt(prompt)
        .stop(listOf("$agentName:", "Human:"))
        .echo(false)
        .model("text-davinci-003")
        .build()
    try {
        val completion = service.createCompletion(completionRequest).choices.first().text
        println("completion: $completion")
        return completion.trim()
    } catch (e: Exception) {
        println("Problem with connection to OpenAI")
    }
    return null
}