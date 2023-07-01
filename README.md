# Skill
Furhat Skills:
 - 20 questions
 - Mülltrennungsspiel (lokal und GPT Variante)

[Allgemeine Furhat Skill Doku](https://docs.furhat.io/skills/#the-contents-of-a-skill) 

## Skill ausführen auf virtuellem Furhat
- registrieren https://furhatrobotics.com/requestsdk/
- SDK laden, installieren und Furhat API Key eingeben
- SDK ausführen (furhat-sdk-desktop-launcher)
- Skill auswählen oder per IDE starten (siehe Dev Einrichtung)
- über "Open web interface" kann die manuelle Steuerung von furhat gestartet werden

## Dev Einrichtung 
Zum anpassen oder deployen neuer Skills (in Kotlin):
- Repo clonen
- in IntelliJ importieren:
  - geht auch mit Community Edition
  - wichtig: braucht Java8
- Furhat SDK muss laufen (siehe Schritte oben)
- über IntelliJ ein Skill Projekt ausführen (navigate to the main.kt file of the skill and press the green play button )
- Custom Gui bauen/starten (in assets/gui):
  - npm install
  - npm run build
- für OpenAi Spiel: API Key hinterlegen in secrets.properties