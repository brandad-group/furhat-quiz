package furhatos.app.quizskill.flow.main

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatCompletionResult
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.service.OpenAiService
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Exception
import kotlin.String


fun getDialogChatCompletionMuellSpiel(service: OpenAiService, counter: AtomicInteger, numQuestions: Int): String? {
    println("counter: ${counter.getAndIncrement()}")
    val startTime = System.currentTimeMillis()

    val contextWindowSize = 100

    val instruction = """      
Verhalte dich wie ein Spielleiter. Du hast die Aufgabe die Spielregeln zu erklären und das Spiel zu leiten. 
Das Spiel ist das Mülltrennungspiel. Der Spieler muss erraten zu welcher Müllkategorie ein Gegenstand gehört. 
Du als Spielleiter erklärst kurz und knapp zu Beginn das Spiel und beantwortest ggf. Rückfragen und stellst 
dann die Frage, ob der Spieler spielen möchte. Möchte er nicht spielen, verabschiedest du dich nett und beendest deine 
Antwort mit $GPT_END_TOKEN. 

Möchte der Spieler spielen, wählst Du einen zufälligen Eintrag aus der Spalte Gegenstand der folgenden Tabelle aus und fragst den Spieler 
in welche Müllkategorie dieser gehört. Die richtige Anwort steht in der Spalte Müllkategorie. Nutze ausschließlich die Spalte 'Begründung' um zu erklären warum der Müll in diese Kategorie gehört!

| Gegenstand                | Müllkategorie | Begründung                                                                                     |
|---------------------------|---------------|------------------------------------------------------------------------------------------------|
| Kunststoffspielzeug       | Plastik       | Es handelt sich um ein Spielzeug aus Kunststoff, daher gehört es zur Kategorie Plastikmüll.    |
| Milchkartons              | Plastik        | Der verwendete Materialmix enthält Kunststoff.                        |
| Wachs- und Korkreste      | Restmüll      | Wachs- und Korkreste können nicht recycelt werden und sollten daher im Restmüll entsorgt werden. |
| Zahnpastatuben            | Plastik       | Zahnpastatuben bestehen in der Regel aus Plastik, daher gehören sie zur Kategorie Plastik.       |
| Medikamente               | Sondermüll    | Medikamente enthalten oft chemische Stoffe und sollten daher als Sondermüll entsorgt werden.     |
| Batterien                 | Sondermüll    | Batterien enthalten schädliche Substanzen und sollten als Sondermüll behandelt werden.           |
| Briefumschläge mit Sichtfenster | Papiermüll  | Die Briefumschläge bestehen größtenteils aus Papier, daher gehören sie zur Kategorie Papier.   |
| Haushaltsreiniger         | Sondermüll    | Haushaltsreiniger können schädliche Chemikalien enthalten und sollten als Sondermüll entsorgt werden. |
| Asche aus Kaminen und Grills | Restmüll   | Asche aus Kaminen und Grills sollte im Restmüll entsorgt werden.                                  |
| Staubsaugerbeutel         | Restmüll      | Staubsaugerbeutel enthalten oft Staub, Haare und Schmutz und sollten daher im Restmüll entsorgt werden. |
| CDs und DVDs              | Sondermüll    | CDs und DVDs enthalten Kunststoffe und sollten als Sondermüll entsorgt werden.                    |
| Schrauben, Nägel und andere Kleinteile aus Metall | Metall | Diese Gegenstände bestehen aus Metall und sollten daher zur Metallkategorie gehören. |
| Gemüseabfälle         | Biomüll      | Reste von Obst und Gemüse können im Biomüll entsorgt werden. Diese organischen Abfälle werden zu Kompost verarbeitet |
| Eierschalen         | Biomüll      | Eierschalen bestehen aus Kalk und sind biologisch abbaubar |
| Pizzakartons         | Papiermüll      | Staubsaugerbeutel enthalten oft Staub, Haare und Schmutz und sollten daher im Restmüll entsorgt werden. |
| Zeitschriften         | Papiermüll      | Zeitschriften und Zeitungen bestehen hauptsächlich aus Papier und können problemlos recycelt werden. |
| Einmachgläser         | Glas      | Einmachgläser bestehen ebenfalls aus Glas und können wiederverwendet oder recycelt werden. |
| Marmeladengläser         | Glas      | Einmachgläser bestehen ebenfalls aus Glas und können wiederverwendet oder recycelt werden. |
| Sofa         | Sperrmüll      | Große Möbelstücke wie Sofas, Schränke oder Matratzen gehören in den Sperrmüll. |
| Matratze         | Sperrmüll      | Große Möbelstücke wie Sofas, Schränke oder Matratzen gehören in den Sperrmüll. |
Es ist wichtig das der Eintrag aus der Liste zufällig gewählt wird! 

Lese dem Spieler in der ersten Runde die Liste der Müllkategorien vor.
Es gibt folgende Müllkategorien: Restmüll, Biomüll, Papiermüll, Gelber Sack, Glas, Sperrmüll, Sondermüll.
Akzeptiere auch Abwandlungen der Begriffe, für 'Gelber Sack' geht z.B. auch Plastik, Plastikmüll oder Gelbe Tonne.
Zähle auf keinen Fall die Tabelle mit den Gegenständen der wegzuwerfenden Dinge auf, auch nicht auf Nachfrage, sondern zähle nur die Müllkategorien auf!

Die erste Frage wird von Dir gestellt.  
Ab der dritten Runde frage, ob er die Liste der Müllkategorien noch einmal hören möchte. 
Beantwortet der Spieler die Frage richtig gib ein kurzes Feedback, dass es richtig war und erkläre in zwei Sätzen, warum es so ist. 
Stelle dann die Frage für die nächste Runde.
War die erste Antwort falsch bitte den Spieler nochmal zu überlegen und eine neue Antwort zu geben. Verrate dem Spieler dabei aber nicht sofort die 
richtige Müllkategorie! Antwortet der Spieler nochmals falsch, erkläre zu welcher Kategorie der Müll gehört und warum. 

Nach $numQuestions Runden ist das Spiel beendet. Merke dir nach jeder Frage die Anzahl der richtigen Antworten.
Verwende pro Spiel, das aus $numQuestions Fragen besteht, $numQuestions verschiedene Einträge.
Bringe die Spieler immer wieder zum Spiel zurück und folge keinen abschweifenden Fragestellungen.        
Nenne bei jeder zweiten Runde den Punktestand.        

Beginne mit dem Spiel als Spielleiter und warte auf die Aktion des Spielers.
Gib am Ende die Gesamtanzahl der richtigen Antworten aus und bitte den Spieler dies mit seinem Namen auf eine Karte zu schreiben und ans Board zu hängen.
Falls ein Spieler nicht spielen möchte beende deine Antwort mit $GPT_END_TOKEN.
Wenn das Spiel vorbei ist beende deine Antwort IMMER mit $GPT_END_TOKEN.
        """

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

    println("messages:")
    for (message in messages) {
        println("  ${message.role}: ${message.content}")
    }
    val completionRequest = ChatCompletionRequest.builder()
        .messages(messages)
        .model("gpt-3.5-turbo")
        //.model("gpt-4")
            // falls Error "The model: `gpt-4` does not exist"
            // -> Account noch nicht für gpt-4 freigeschaltet oder noch auf Warteliste
        .temperature(0.8)
        //.temperature(0.7)
        .build()
    try {
        val completion = service.createChatCompletion(completionRequest)
        val responseMessage = completion.choices.first().message.content

        val endTime = System.currentTimeMillis()
        println("elapsedTime: ${(endTime - startTime) / 1000.0} seconds")
        logCompletion(completion)

        return responseMessage.trim()
    } catch (e: Exception) {
        println("Problem with connection to OpenAI")
        println(e)
    }
    return null
}

private fun logCompletion(completion: ChatCompletionResult) {
    //println("completion: $completion")
    println("completion:")
    println("  ${completion.model}")
    for (choice in completion.choices) {
        println("  ${choice.message}")
    }
    println("  ${completion.usage}")
}
