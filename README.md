# Spese Personali

## Overview

Sistema per gestire entrate e spese personali. Permette di aggiungere transazioni, categorizzarle, visualizzarle e calcolare statistiche. I dati vengono salvati in formato CSV. Implementa i pattern di design richiesti: Factory, Iterator, Strategy, Composite ed Exception Shielding.

Funzionalita principali:
- Aggiunta spese e entrate con categorie predefinite
- Visualizzazione transazioni ordinate per data
- Calcolo statistiche (totali e per categoria)
- Gestione categorie personalizzate
- Persistenza dati in CSV

## Design Decisions & Rationale

### Architettura
Il progetto segue un'architettura a layer con separazione delle responsabilita:
- Model: contiene Transaction, Category, TransactionType
- Factory: crea transazioni validate tramite TransactionFactory
- Repository: gestisce persistenza CSV tramite TransactionRepository
- Service: contiene business logic tramite ExpenseService
- Iterator: naviga collezioni tramite TransactionByDateIterator
- Strategy: calcoli diversi tramite CalculationStrategy
- Exception: gestisce errori tramite custom exceptions

### Scelte implementative

Factory Pattern: scelto per centralizzare creazione transazioni con validazione. Evita duplicazione codice e garantisce consistenza dati.
Iterator Pattern: implementato per navigare transazioni mantenendo ordine cronologico. Nasconde implementazione interna della collezione.
Strategy Pattern: permette calcoli diversi (totali, categorie) senza modificare codice esistente. Facilita aggiunta nuove statistiche.
Composite Pattern: implementato per gestire categorie di spese in modo gerarchico, permettendo di raggruppare e calcolare totali per gruppi di categorie
Exception Shielding: nasconde errori tecnici mostrando messaggi senza dare informazioni sul codice all'esterno.
Repository Pattern: separa business logic dalla persistenza. Facilita cambio formato dati futuro.


## Diagramma UML

Immagine in: "spesepersonali\logs\uml\UML_Diagram.png"



## Limiti noti

### Limitazioni attuali
- Un solo file CSV per tutti i dati (non scalabile per molti utenti)
- Nessuna autenticazione o gestione utenti multipli
- Statistiche limitate a totali e categorie
- Interface console-based (no GUI)


### Possibili problemi
- File CSV potrebbe corrompersi con edit esterni
- Prestazioni degradano con molte transazioni
- Nessuna crittografia dati sensibili

## Future Work

### Miglioramenti pianificati
- Interface web o mobile
- Grafici e statistiche avanzate
- Sincronizzazione cloud
- Gestione budget e obiettivi risparmio
- Notifiche spese eccessive


## Comandi

Installazione ed avvio pulito e test: mvn clean install
Per testare: mvn test  
Per avviare: java -cp target/classes com.epicode.App

Pattern implementati: Factory, Iterator, Strategy, Composite, Exception Shielding
Tecnologie: Collections, Generics, I/O, Logging, JUnit


