package furhatos.app.quizskill.flow.main

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.service.OpenAiService
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Exception
import kotlin.String


fun getDialogChatCompletion20Questions(service: OpenAiService, counter: AtomicInteger): String? {
    println("counter: ${counter.getAndIncrement()}")
    val startTime = System.currentTimeMillis()

    val contextWindowSize = 10
    val numQuestions = 20
    val endToken = "$numQuestions Versuche erreicht"
    val instruction = "Du bist ein freundlicher Roboter. Dein Name ist Furhat. " +
            "Wir spielen das 20-Fragen-Spiel, ein einfaches, aber lustiges Ratespiel. " +
            "In diesem Spiel stellst du bis zu 20 Ja/Nein-Fragen, um herauszufinden, woran der Nutzer denkt. " +
            "Versuche kurze Antworten zu geben, die aber lustig und abwechslungsreich sind. " +
            "Sage ab und zu etwas wie 'Da muss ich mal nachdenken...' oder 'Ich bin mir nicht sicher, aber...'. " +
            "Wenn du weißt woran der Nutzer denkt, sagst du 'Ich denke, es ist ein ...'.  Wenn der Nutzer es bestätigt ist das Spiel zu Ende." +
            "Dann sagst Du 'Ich habe gewonnen'. " +
            "Wenn der Nutzer sagt '20 Versuche erreicht' ist das Spiel auch zu Ende und der Nutzer hat gewonnen."
    val messages = mutableListOf(ChatMessage().apply { role = "system"; content = instruction })

    Furhat.dialogHistory.all.takeLast(contextWindowSize).forEach {
        when (it) {
            is DialogHistory.ResponseItem -> {
                messages.add(ChatMessage().apply { role = "user"; content = it.response.text })
            }
            is DialogHistory.UtteranceItem -> {
                messages.add(ChatMessage().apply { role = "assistant"; content = it.toText() })
            }
            else -> null
        }
    }

    if (counter.get() > numQuestions) {
        messages.removeAt(messages.size-1)
        messages.add(ChatMessage().apply { role = "user"; content = endToken })
    }

    println("messages:")
    for (message in messages) {
        println("  ${message.role}: ${message.content}")
    }
    val completionRequest = ChatCompletionRequest.builder()
        .messages(messages)
        .model("gpt-3.5-turbo")
        .temperature(0.7)
        .build()
    try {
        val completion = service.createChatCompletion(completionRequest)
        val responseMessage = completion.choices.first().message.content
        //Alternativ evtl. streamen der Antwort
        //service.streamChatCompletion(chatCompletionRequest)
        //    .doOnError(Throwable::printStackTrace)
        //    .blockingForEach(System.out::println);

        val endTime = System.currentTimeMillis()
        println("elapsedTime: ${(endTime - startTime) / 1000.0} seconds")
        println("completion: $completion")
        return responseMessage.trim()
    } catch (e: Exception) {
        println("Problem with connection to OpenAI")
    }
    return null
}
