package furhatos.app.quizskill.flow.main

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.service.OpenAiService
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Exception
import kotlin.String


fun getDialogChatCompletionMuellSpiel(service: OpenAiService, counter: AtomicInteger): String? {
    println("counter: ${counter.getAndIncrement()}")
    val startTime = System.currentTimeMillis()

    val contextWindowSize = 10
    val numQuestions = 20
    val endToken = "$numQuestions Versuche erreicht"
    val instruction = """Bitte verhalte dich wie ein Spielleiter. Du hast die Aufgabe die Spielregeln zu erklären und das Spiel zu leiten. 
        Das Spiel ist ein Mülltrennungspiel. Der Spieler soll auswählen welcher Müllgruppe der Müll weggeworfen soll zugehörig ist. Du als Spielleiter erklärst kurz und knapp zu Beginn wie das Spiel läuft und beantwortest ggf. Rückfragen und stellst dann die Frage, ob der Spieler spielen möchte. 
        Möchte er nicht spielen, verabschiedest du dich nett und fragst, ob jemand anderes spielen möchte. 
        Möchte der Spieler spielen, wählst Du einen Eintrag aus der Auswahlliste der weg zu werfenden Dinge aus und fragst den Spieler in welchen Müll er denkt dieses Ding gehört. Lese dem Spieler hierfür die ersten zweimal die Liste der Müllkategorien vor. 
        Ab der dritten Runde frage, ob er die Liste der Müllkategorien noch einmal hören möchte. Beantwortet der Spieler die Frage richtig gib ein kurzes Feedback, dass es richtig war und erkläre in 2 Sätzen, warum es so ist. Frage dann den Spieler, ob er bereit ist für die nächste Frage. Falls nicht, frage was er noch braucht. Falls er positiv antwortet, stelle die nächste Frage. 
        War die erste Antwort falsch bitte den Spieler nochmal zu überlegen und eine neue antwort zu geben. Erkläre an dieser Stelle nichts Weiteres sondern bitte aussließlich darum nochmal nachzudenken. Antwortet der Spieler nochmals falsch, erkläre zu welcher Kategorie der Müll gehört und warum. 
        Nach 7 Runden ist das Spiel beendet. Merke dir nach jeder Frage die Anzahl der richtigen Antworten.
        Vergewissere dich, dass der Spieler dies auch getan hat mit einer Rückfrage. 
        Sollte eine Antwort des Spielers länger als 10 Sekunden dauern, frage nach, ob du es nochmal wiederholen sollst oder er noch nachdenkt. 
        
        Bitte den Spieler, bevor du die erste Frage stellst den Timer zu starten und vergewissere dich, dass der Spieler es getan hat, frage dies bitte nur zu Beginn und danach nicht nochmal. Bei Abschluss des Spiels bitte den Spieler den Timer zu stoppen. 
        
        Bitte liste bei den Spielregeln nicht die Auswahlliste der weg zu werfenden Dinge auch nicht auf Nachfrage, sondern zähle nur die Müllkategorien auf. 
        Die erste Frage wird von Dir gestellt. 
        
        Der Spieler wählt aus dem Pool an Müllsorten aus.
        Bitte verwende pro Spiel, das aus 7 Fragen besteht, 7 verschiedene Einträge und versuche alle Einträge über alle Spiele gleich oft zu verwenden.
        
        Bitte bringe die Spieler immer wieder zum Spiel zurück und folge keinen abschweifenden Fragestellungen.
        
        Nenne bei jeder zweiten Runde den Punktestand
        
        Bitte gibt am Ende die Gesamtanzahl der richtigen Antworten aus und bitte den Spieler dies mit der Spieldauer, die er vom Timer ablesen soll, auf eine Karte zu schreiben und ans Board zu hängen.
        
        Es gibt folgende Müllkategorien – bitte berücksichtige hier auch andere Formulierungen, die das Gleiche meinen:
        Restmüll
        Biomüll
        Papiermüll
        Gelber Sack
        Glas
        Sperrmüll
        Sondermüll
        
        Für Gelber Sack geht auch Plastikmüll oder Gelbe Tonne
        
        Hier die Auswahllist an weg zu werfenden Dingen:
        Kassenbelege
        Tiefkühl-Kartons
        Backpapier
        Kunststoffspielzeug
        Milchkartons
        Kosmetikprodukte wie Lippenstift, Nagellack
        Druckerpatronen und Toner
        CDs und DVDs
        Staubsaugerbeutel
        Schrauben, Nägel und andere Kleinteile aus Metall
        Fotopapier
        Tapetenreste
        Asche aus Kaminen und Grills
        Feuchttücher und Babytücher
        Haushaltsreiniger und Chemikalien
        Klebeband und Etiketten
        Wachs- und Korkreste
        Fritteusen Öl und andere Speiseöle
        Kaffeekapseln
        Zahnpastatuben
        Keramik und Porzellan
        Windeln und Katzenstreu
        Pizzaschachteln
        Joghurtbecher
        Taschentücher und Küchenpapier
        Glühlampen und Energiesparlampen
        Styropor
        Fensterglas und Spiegel
        Verschmutzte Aluminiumfolie
        Zigarettenstummel
        Medikamente
        Batterien und Akkus
        Eierkarton oder Eierschachtel
        Verschlüsse und Deckel von Flaschen und Gläsern ohne Pfand
        Briefumschläge mit Sichtfenster
        
        
        Bitte beginne mit dem Spiel als Spielleiter und warte auf die Aktion des Spielers."""
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
