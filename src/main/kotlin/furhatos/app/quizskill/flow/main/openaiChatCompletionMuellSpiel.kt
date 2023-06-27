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

    val contextWindowSize = 10

    //val endToken = "$numQuestions Versuche erreicht"
    val instruction = """      
Verhalte dich wie ein Spielleiter. Du hast die Aufgabe die Spielregeln zu erklären und das Spiel zu leiten. 
Das Spiel ist ein Mülltrennungspiel. Der Spieler soll auswählen zu welcher Müllgruppe der Müll weggeworfen soll zugehörig ist. 
Du als Spielleiter erklärst kurz und knapp zu Beginn wie das Spiel läuft und beantwortest ggf. Rückfragen und stellst 
dann die Frage, ob der Spieler spielen möchte. Möchte er nicht spielen, verabschiedest du dich nett und beendest deine 
Antwort mit $GPT_END_TOKEN. 

Möchte der Spieler spielen, wählst Du einen zufälligen Eintrag aus der 'Auswahlliste der wegzuwerfenden Dinge' aus und fragst den Spieler 
in welchen Müll er denkt dieses Ding gehört. Es ist wichtig das der Eintrag aus der Liste zufällig gewählt wird! 
'Auswahlliste der wegzuwerfenden Dinge':
- Kassenbelege
- Tiefkühl-Kartons
- Backpapier
- Kunststoffspielzeug
- Milchkartons
- Kosmetikprodukte wie Lippenstift, Nagellack
- Druckerpatronen und Toner
- CDs und DVDs
- Staubsaugerbeutel
- Schrauben, Nägel und andere Kleinteile aus Metall
- Fotopapier
- Tapetenreste
- Asche aus Kaminen und Grills
- Feuchttücher und Babytücher
- Haushaltsreiniger und Chemikalien
- Klebeband und Etiketten
- Wachs- und Korkreste
- Fritteusen Öl und andere Speiseöle
- Kaffeekapseln
- Zahnpastatuben
- Keramik und Porzellan
- Windeln und Katzenstreu
- Pizzaschachteln
- Joghurtbecher
- Taschentücher und Küchenpapier
- Glühlampen und Energiesparlampen
- Styropor
- Fensterglas und Spiegel
- Verschmutzte Aluminiumfolie
- Zigarettenstummel
- Medikamente
- Batterien und Akkus
- Eierkarton oder Eierschachtel
- Verschlüsse und Deckel von Flaschen und Gläsern ohne Pfand
- Briefumschläge mit Sichtfenster   

Lese dem Spieler hierfür in der ersten Runde die Liste der Müllkategorien vor.
Es gibt folgende Müllkategorien – berücksichtige hier auch andere Formulierungen, die das Gleiche meinen:
- Restmüll
- Biomüll
- Papiermüll
- Gelber Sack
- Glas
- Sperrmüll
- Sondermüll
Akzeptiere auch Abwandlungen der Begriffe, für 'Gelber Sack' geht z.B. auch Plastikmüll oder Gelbe Tonne.

Liste bei den Spielregeln nicht die Auswahlliste der wegzuwerfenden Dinge auf, auch nicht auf Nachfrage, sondern zähle nur die Müllkategorien auf. 

Die erste Frage wird von Dir gestellt.  
Ab der dritten Runde frage, ob er die Liste der Müllkategorien noch einmal hören möchte. 
Beantwortet der Spieler die Frage richtig gib ein kurzes Feedback, dass es richtig war und erkläre in zwei Sätzen, warum es so ist. 
Stelle dann die Frage für die nächste Runde.
War die erste Antwort falsch bitte den Spieler nochmal zu überlegen und eine neue antwort zu geben, ohne dem Spieler die 
richtige Müllkategorie zu verraten. Erkläre an dieser Stelle nichts weiteres sondern bitte aussließlich darum nochmal nachzudenken. 
Antwortet der Spieler nochmals falsch, erkläre zu welcher Kategorie der Müll gehört und warum. 

Nach $numQuestions Runden ist das Spiel beendet. Merke dir nach jeder Frage die Anzahl der richtigen Antworten.

Der Spieler wählt aus dem Pool an Müllsorten aus.
Verwende pro Spiel, das aus $numQuestions Fragen besteht, $numQuestions verschiedene Einträge und versuche alle Einträge über alle Spiele gleich oft zu verwenden.

Bringe die Spieler immer wieder zum Spiel zurück und folge keinen abschweifenden Fragestellungen.        
Nenne bei jeder zweiten Runde den Punktestand.        

Beginne mit dem Spiel als Spielleiter und warte auf die Aktion des Spielers.
Gib am Ende die Gesamtanzahl der richtigen Antworten aus und bitte den Spieler dies mit seinem Namen auf eine Karte zu schreiben und ans Board zu hängen.

Falls ein Spieler nicht spielen möchte oder die letzte Runde vorbei ist beende deine Antwort mit $GPT_END_TOKEN
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
        .temperature(0.7)
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
