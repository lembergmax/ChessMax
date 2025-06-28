# ♟ ChessMax – Das digitale Schachspiel

ChessMax ist eine regelkonforme und benutzerfreundliche Schachanwendung für den lokalen Einzelspieler- oder Mehrspielermodus. Die Anwendung wurde in Java mit Swing entwickelt und stellt das klassische Schachspiel in einer intuitiven grafischen Oberfläche dar.

## 🎮 Hauptfunktionen

- Klassisches Schachspiel für Einzelspieler
- Grafisches Schachbrett mit Swing-Komponenten
- Unterstützte Spielregeln:
  - Schach, Schachmatt, Patt
  - Rochade, en passant, Figurenumwandlung
- Zugliste (chronologische Darstellung der Spielzüge)
- Schritt-für-Schritt Navigation durch den Spielverlauf
- Brettdrehung (180°) zur Perspektivenanpassung
- Bedienung über Maus: Klick oder Drag & Drop

## 🖼️ Benutzeroberfläche

![image](https://github.com/user-attachments/assets/16936b01-fc5f-4885-9cfc-61eb092322a7)

![image](https://github.com/user-attachments/assets/8529ede7-7616-43db-8757-3b4f1d75f1fe)

## ⚙️ Technologiestack

| Komponente         | Technologie   |
| ------------------ | ------------- |
| Programmiersprache | Java          |
| GUI                | Java Swing    |
| Build-Tool         | Maven         |

## 🧱 Architektur

Das Projekt folgt einem modularen Aufbau nach dem MVC-Prinzip:

* **Model**: Spiellogik, Figurenverhalten, Regelvalidierung
* **View**: Swing-basierte Benutzeroberfläche (Spielbrett, Buttons)
* **Controller**: Steuerung der Benutzerinteraktionen und Spielfluss

## 🔧 Bedienung

* Starte die Anwendung über die `main`-Methode
* Spiele lokal gegen dich selbst (oder andere)
* Navigation über die Buttons unterhalb des Bretts
* Drehe das Brett bei Bedarf um 180°, um die Perspektive zu wechseln
