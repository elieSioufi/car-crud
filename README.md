    # Car CRUD - Plateforme de Location et Achat de Voitures

Application web de gestion de voitures avec location et achat, construite avec Spring Boot, Thymeleaf et H2.

**Objectif principal** : Demonstrer les 5 portees (scopes) des beans Spring dans un contexte reel.


## Identifiants de connexion

| Utilisateur | Mot de passe  | Role  | Prenom |
|-------------|---------------|-------|--------|
| admin       | adminpass     | ADMIN | Admin  |
| elie        | eliepass      | USER  | Elie   |

## Fonctionnalites

### Administrateur (ADMIN)
- Voir, creer, modifier et supprimer des voitures
- Voir toutes les locations (approuver / rejeter)
- Voir tous les achats (acheteur, voiture, prix, date)
- Gerer les enregistrements de paiement (CRUD complet)
- Generer un rapport d'inventaire des voitures

### Utilisateur (USER)
- Louer une voiture (avec validation des dates)
- Acheter une voiture (la voiture est marquee comme vendue + un paiement est cree automatiquement)
- Panier d'achat (ajouter, retirer, acheter tout)
- Page d'accueil avec liens rapides

### Liaison Achats-Paiements
Quand un utilisateur achete une voiture (directement ou via le panier), le systeme :
1. Cree un enregistrement `Purchase` (acheteur, voiture, prix, date)
2. Marque la voiture comme vendue
3. Cree automatiquement un `PaymentRecord` lie a l'achat (montant = prix de la voiture)

## Les 5 Scopes des Beans Spring

Le projet implemente les 5 portees de beans Spring :

### 1. Singleton — `CarService`
- **Annotation** : `@Scope("singleton")`
- **Fichier** : `service/CarService.java`
- **Comportement** : Une seule instance partagee par tout le conteneur Spring. Tous les controleurs utilisent la meme instance.
- **Tester** : C'est le comportement par defaut. L'annotation est explicite dans le code.

### 2. Session — `SessionCart`
- **Annotation** : `@SessionScope`
- **Fichier** : `model/SessionCart.java`
- **Comportement** : Une instance par session HTTP. Le panier persiste entre les pages et se vide a la deconnexion.
- **Tester** :
  1. Se connecter en tant que `elie`
  2. Aller sur "Buy a car" et cliquer "Add to Cart" sur 2 voitures
  3. Le badge "Cart (2)" apparait dans la barre laterale
  4. Naviguer sur d'autres pages — le panier persiste
  5. Se deconnecter et se reconnecter — le panier est vide (nouvelle session)
  6. Ouvrir un **2eme navigateur** avec `john` — son panier est independant

### 3. Request — `RequestTimer`
- **Annotation** : `@RequestScope`
- **Fichier** : `model/RequestTimer.java`
- **Comportement** : Une nouvelle instance creee a chaque requete HTTP, detruite a la fin de la requete.
- **Tester** :
  1. Sur n'importe quelle page, regarder le pied de page : "Page generated in Xms"
  2. Rafraichir la page — la valeur change a chaque fois (nouvelle instance par requete)

### 4. Prototype — `CarReportBuilder`
- **Annotation** : `@Scope("prototype")`
- **Fichier** : `service/CarReportBuilder.java`
- **Comportement** : Une nouvelle instance independante a chaque demande au conteneur. Pas de partage, pas de cache.
- **Tester** :
  1. Se connecter en tant que `admin`
  2. Cliquer "Car Report" dans la barre laterale
  3. Voir les statistiques (total, disponibles, vendues, prix moyen, repartition par type)
  4. Ajouter ou supprimer une voiture, puis revisiter le rapport — les stats sont mises a jour (nouvelle instance)

### 5. Application — `AppVisitCounter`
- **Annotation** : `@ApplicationScope`
- **Fichier** : `model/AppVisitCounter.java`
- **Comportement** : Une seule instance partagee entre TOUTES les sessions et TOUS les utilisateurs. Se reinitialise uniquement au redemarrage de l'application.
- **Tester** :
  1. Voir le compteur de visites dans la barre laterale et sur la page de connexion
  2. Rafraichir — le compteur augmente
  3. Ouvrir un 2eme navigateur avec un autre utilisateur — le compteur est le meme (partage global)

### Resume des Scopes

| Scope       | Bean               | Duree de vie                  | Exemple dans le projet           |
|-------------|--------------------|-------------------------------|----------------------------------|
| Singleton   | `CarService`       | Toute la duree de l'app       | Service partage partout          |
| Session     | `SessionCart`      | Une session utilisateur       | Panier d'achat                   |
| Request     | `RequestTimer`     | Une requete HTTP              | Chronometre de page              |
| Prototype   | `CarReportBuilder` | Ponctuel (nouvelle instance)  | Generateur de rapport            |
| Application | `AppVisitCounter`  | Toute l'app, tous les users   | Compteur global de visites       |

## JPA / ORM — Entites et Relations

Le projet utilise **Spring Data JPA** avec **Hibernate** comme ORM (Object-Relational Mapping). Les classes Java sont mappees automatiquement vers des tables SQL dans la base H2.

### Annotations JPA utilisees

| Annotation | Role | Exemple |
|---|---|---|
| `@Entity` | Declare une classe comme entite JPA (mappee a une table) | Toutes les entites |
| `@Table(name = "...")` | Definit le nom de la table SQL | `@Table(name = "app_user")` |
| `@Id` | Cle primaire de la table | `private Long id` |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Auto-increment de l'ID | Toutes les entites |
| `@Column(nullable = false)` | Colonne obligatoire (NOT NULL) | `username`, `brand`, `model` |
| `@Column(unique = true)` | Valeur unique en base | `AppUser.username` |
| `@Column(precision = 10, scale = 2)` | Precision decimale pour les prix | `BigDecimal price` |
| `@Enumerated(EnumType.STRING)` | Stocke un enum en tant que texte (pas un entier) | `CarType`, `RentalStatus` |
| `@ManyToOne(fetch = FetchType.EAGER)` | Relation plusieurs-a-un (charge l'objet lie immediatement) | `Purchase.user`, `Rental.car` |
| `@JoinColumn(name = "...")` | Definit la colonne de cle etrangere | `@JoinColumn(name = "user_id")` |
| `@PrePersist` | Methode executee avant l'insertion en base | Auto-set `purchaseDate`, `createdAt` |

### Entites et leurs relations

```mermaid
erDiagram
    APP_USER {
        Long id PK
        String username UK
        String password
        String role
        String firstName
        String lastName
    }

    CAR {
        Long id PK
        CarType type
        String brand
        String model
        LocalDate purchaseDate
        int maxSpeed
        int passengers
        boolean automaticTransmission
        boolean sold
        BigDecimal price
    }

    PURCHASE {
        Long id PK
        Long user_id FK
        Long car_id FK
        LocalDateTime purchaseDate
        BigDecimal price
    }

    RENTAL {
        Long id PK
        Long user_id FK
        Long car_id FK
        LocalDate startDate
        LocalDate endDate
        int days
        String comment
        RentalStatus status
        LocalDateTime createdAt
    }

    PAYMENT_RECORD {
        Long id PK
        String currency
        String iban
        String ownerName
        BigDecimal amount
    }

    APP_USER ||--o{ PURCHASE : "achete"
    APP_USER ||--o{ RENTAL : "loue"
    CAR ||--o{ PURCHASE : "est achete"
    CAR ||--o{ RENTAL : "est loue"
    PURCHASE ||--o| PAYMENT_RECORD : "genere"
```

### Detail des relations

**`Purchase` → `AppUser`** : `@ManyToOne` — Un utilisateur peut faire plusieurs achats, mais chaque achat appartient a un seul utilisateur.
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id", nullable = false)
private AppUser user;
```

**`Purchase` → `Car`** : `@ManyToOne` — Chaque achat concerne une voiture. La colonne `car_id` est la cle etrangere.
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "car_id", nullable = false)
private Car car;
```

**`Rental` → `AppUser`** : `@ManyToOne` — Un utilisateur peut faire plusieurs locations.
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id", nullable = false)
private AppUser user;
```

**`Rental` → `Car`** : `@ManyToOne` — Chaque location concerne une voiture.
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "car_id", nullable = false)
private Car car;
```

**`Purchase` → `PaymentRecord`** : Liaison logique (pas de `@OneToOne` en base). Lors d'un achat, un `PaymentRecord` est cree automatiquement dans `PurchaseService` avec l'IBAN `"PURCHASE-{id}"`.

### Repositories (Spring Data JPA)

Les repositories heritent de `JpaRepository<Entity, ID>` qui fournit automatiquement les methodes CRUD (`findAll()`, `findById()`, `save()`, `deleteById()`, etc.) sans ecrire de SQL.

| Repository | Entite | Methodes personnalisees |
|---|---|---|
| `CarRepository` | `Car` | `findBySoldFalse()` — voitures disponibles |
| `AppUserRepository` | `AppUser` | `findByUsername(String)` — recherche par login |
| `PurchaseRepository` | `Purchase` | — (CRUD de base) |
| `RentalRepository` | `Rental` | — (CRUD de base) |
| `PaymentRecordRepository` | `PaymentRecord` | — (CRUD de base) |

> Spring Data JPA genere automatiquement les requetes SQL a partir du nom de la methode. Par exemple, `findBySoldFalse()` genere : `SELECT * FROM car WHERE sold = false`.

## Architecture globale

```mermaid
graph TB
    subgraph Client
        B[Navigateur]
    end

    subgraph Spring Security
        SF[Security Filter Chain]
    end

    subgraph Intercepteur Web
        RT["RequestTimer - @RequestScope"]
        AV["AppVisitCounter - @ApplicationScope"]
    end

    subgraph Controllers
        LC[LoginController]
        UC[UserController]
        AC[AdminCarController]
        APC[AdminPurchaseController]
        ARC[AdminRentalController]
        APMC[AdminPaymentController]
    end

    subgraph Services
        CS["CarService - @Singleton"]
        PS[PurchaseService]
        RS[RentalService]
        PMS[PaymentRecordService]
        CRB["CarReportBuilder - @Prototype"]
    end

    subgraph Session Beans
        SC["SessionCart - @SessionScope"]
    end

    subgraph Repositories
        CR[CarRepository]
        PR[PurchaseRepository]
        RR[RentalRepository]
        PMR[PaymentRecordRepository]
        UR[AppUserRepository]
    end

    subgraph "Base de donnees"
        H2[(H2 Database)]
    end

    B --> SF
    SF --> LC
    SF --> UC
    SF --> AC
    SF --> APC
    SF --> ARC
    SF --> APMC

    UC --> SC
    UC --> CS
    UC --> PS
    AC --> CS
    AC --> CRB
    APC --> PS
    ARC --> RS
    APMC --> PMS

    CS --> CR
    PS --> PR
    PS --> PMR
    RS --> RR
    PMS --> PMR

    CR --> H2
    PR --> H2
    RR --> H2
    PMR --> H2
    UR --> H2
```

## RabbitMQ — Communication Asynchrone avec le Service Bancaire

Le projet utilise **RabbitMQ** comme broker de messages pour communiquer de maniere asynchrone avec un microservice bancaire (`bank-service`). Chaque operation d'achat ou de location passe par une verification de credit aupres de la banque avant d'etre validee.

### Principe general

```mermaid
sequenceDiagram
    participant U as Utilisateur
    participant CC as car-crud (port 8080)
    participant RMQ as RabbitMQ (port 5672)
    participant BS as bank-service (port 8081)

    U->>CC: Acheter / Louer une voiture
    CC->>CC: Valider la requete
    CC->>RMQ: Envoyer CreditRequest<br/>(credit.request.queue)
    RMQ->>BS: Distribuer le message
    BS->>BS: Verifier le credit disponible<br/>(limite: 10 000 EUR/utilisateur)
    BS->>RMQ: Envoyer CreditResponse<br/>(credit.response.queue)
    RMQ->>CC: Distribuer la reponse
    alt Credit approuve
        CC->>CC: Creer l'achat/location + log
        CC->>U: Succes !
    else Credit refuse
        CC->>CC: Logger le refus
        CC->>U: Erreur : credit insuffisant
    end
```

### Architecture RabbitMQ

```mermaid
graph LR
    subgraph "car-crud (port 8080)"
        CRP[CreditRequestPublisher]
        CRL[CreditResponseListener]
    end

    subgraph "RabbitMQ (port 5672)"
        Q1[credit.request.queue]
        Q2[credit.response.queue]
    end

    subgraph "bank-service (port 8081)"
        BS[BankService]
        BC[BankController]
    end

    CRP -->|"convertAndSend()"| Q1
    Q1 -->|"@RabbitListener"| BS
    BS -->|"convertAndSend()"| Q2
    Q2 -->|"@RabbitListener"| CRL
```

### Diagramme de classes — RabbitMQ

```mermaid
classDiagram
    class CreditRequest {
        -String correlationId
        -String username
        -BigDecimal amount
        -String operationType
    }

    class CreditResponse {
        -String correlationId
        -String username
        -boolean approved
        -String message
    }

    class CreditRequestPublisher {
        -RabbitTemplate rabbitTemplate
        -ConcurrentHashMap~String, CompletableFuture~ pendingRequests
        +verifyCreditAndWait(username, amount, operationType) CreditResponse
        +completeRequest(response) void
    }

    class CreditResponseListener {
        -CreditRequestPublisher creditRequestPublisher
        +handleBankResponse(response) void
    }

    class RabbitMQConfig {
        +CREDIT_REQUEST_QUEUE$ String
        +CREDIT_RESPONSE_QUEUE$ String
        +creditRequestQueue() Queue
        +creditResponseQueue() Queue
        +jsonMessageConverter() MessageConverter
    }

    class BankService {
        -BigDecimal DEFAULT_CREDIT_LIMIT$
        -Map~String, BigDecimal~ usedCredit
        -RabbitTemplate rabbitTemplate
        +handleCreditRequest(request) void
        +getCreditLimit() BigDecimal
        +getUsedCredit(username) BigDecimal
        +getRemainingCredit(username) BigDecimal
    }

    class BankController {
        -BankService bankService
        +getCreditInfo(username) ResponseEntity
        +getAllCredits() ResponseEntity
    }

    CreditRequestPublisher --> CreditRequest : envoie
    CreditResponseListener --> CreditResponse : recoit
    CreditRequestPublisher <-- CreditResponseListener : completeRequest()
    BankService --> CreditRequest : recoit
    BankService --> CreditResponse : envoie
    BankController --> BankService : utilise

    RentalService --> CreditRequestPublisher : verifyCreditAndWait()
    PurchaseService --> CreditRequestPublisher : verifyCreditAndWait()
```

### Les deux files (queues) RabbitMQ

| Queue | Direction | Producteur | Consommateur | Contenu |
|-------|-----------|------------|--------------|---------|
| `credit.request.queue` | car-crud → bank-service | `CreditRequestPublisher` | `BankService` | Demande de verification de credit |
| `credit.response.queue` | bank-service → car-crud | `BankService` | `CreditResponseListener` | Decision de la banque (approuve/refuse) |

Les deux queues sont **durables** (`new Queue(name, true)`) — elles survivent a un redemarrage de RabbitMQ.

### Les DTOs echanges

**`CreditRequest`** — Envoye par car-crud vers la banque :
```java
public class CreditRequest implements Serializable {
    private String correlationId;   // UUID unique pour matcher requete/reponse
    private String username;        // Utilisateur qui demande le credit
    private BigDecimal amount;      // Montant demande (prix voiture ou cout location)
    private String operationType;   // "RENT" ou "BUY"
}
```

**`CreditResponse`** — Retourne par la banque :
```java
public class CreditResponse implements Serializable {
    private String correlationId;   // Meme UUID que la requete
    private String username;        // Utilisateur concerne
    private boolean approved;       // true = approuve, false = refuse
    private String message;         // Details ("Remaining credit: 5000 EUR" ou "Insufficient credit...")
}
```

### Mecanisme de correlation (Request-Reply Pattern)

Le defi principal est que RabbitMQ est **asynchrone** mais le controleur web a besoin d'une **reponse synchrone** pour afficher le resultat. Voici comment le pattern Request-Reply est implemente :

```mermaid
sequenceDiagram
    participant PS as PurchaseService
    participant CRP as CreditRequestPublisher
    participant RMQ as RabbitMQ
    participant BS as BankService
    participant CRL as CreditResponseListener

    PS->>CRP: verifyCreditAndWait("elie", 10000, "BUY")
    CRP->>CRP: Generer UUID (correlationId)
    CRP->>CRP: Creer CompletableFuture<br/>pendingRequests.put(correlationId, future)
    CRP->>RMQ: convertAndSend(credit.request.queue, request)
    CRP->>CRP: future.get(10, SECONDS) — BLOQUE

    RMQ->>BS: handleCreditRequest(request)
    BS->>BS: Verifier credit restant
    BS->>RMQ: convertAndSend(credit.response.queue, response)

    RMQ->>CRL: handleBankResponse(response)
    CRL->>CRP: completeRequest(response)
    CRP->>CRP: pendingRequests.get(correlationId).complete(response)
    CRP-->>PS: Retourner CreditResponse (debloque)
```

**Etapes cles :**
1. `CreditRequestPublisher` genere un **UUID** comme `correlationId`
2. Il cree un `CompletableFuture` et le stocke dans une `ConcurrentHashMap<correlationId, Future>`
3. Il envoie le message a RabbitMQ puis **bloque** avec `future.get(10, SECONDS)`
4. La banque traite la requete et renvoie une reponse avec le **meme correlationId**
5. `CreditResponseListener` recoit la reponse et appelle `completeRequest(response)`
6. Le `CompletableFuture` est complete, ce qui **debloque** `verifyCreditAndWait()`

**Timeout** : Si la banque ne repond pas dans les 10 secondes, une `RuntimeException` est levee.

### Logique de la banque (`bank-service`)

La banque maintient un **compteur de credit utilise par utilisateur** en memoire (`ConcurrentHashMap`).

```
Limite de credit par defaut : 10 000 EUR / utilisateur

Si montant_demande <= credit_restant :
    → APPROUVE (credit deduit)
Sinon :
    → REFUSE (pas de deduction)
```

**Exemple :**
1. Elie achete une voiture a 8 000 EUR → **Approuve** (reste 2 000 EUR)
2. Elie loue une voiture pour 300 EUR → **Approuve** (reste 1 700 EUR)
3. Elie achete une voiture a 5 000 EUR → **Refuse** (seulement 1 700 EUR disponibles)

> **Note** : Le credit est stocke en memoire — il se reinitialise au redemarrage de `bank-service`.

### API REST de la banque

Le `bank-service` expose aussi une API REST pour consulter l'etat du credit :

| Endpoint | Methode | Description |
|----------|---------|-------------|
| `/api/bank/credit/{username}` | GET | Credit d'un utilisateur (limite, utilise, restant) |
| `/api/bank/credits` | GET | Credit de tous les utilisateurs |

**Exemple de reponse** `GET /api/bank/credit/elie` :
```json
{
    "username": "elie",
    "creditLimit": 10000,
    "usedCredit": 8000,
    "remainingCredit": 2000
}
```

### Journal des operations (`OperationLog`)

Chaque demande de credit (approuvee ou refusee) est enregistree dans la table `operation_log` :

```mermaid
erDiagram
    OPERATION_LOG {
        Long id PK
        String username
        Long carId
        String carDescription
        String operationType
        BigDecimal amount
        boolean approved
        String bankMessage
        LocalDateTime timestamp
    }
```

Consultable via l'API REST : `GET /api/operations`

### Configuration RabbitMQ

Les deux microservices utilisent la **meme configuration** de connexion :

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

Les messages sont serialises en **JSON** via `Jackson2JsonMessageConverter`.

### Demarrer l'ensemble

```bash
# 1. Demarrer RabbitMQ (Docker)
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management

# 2. Demarrer le service bancaire (port 8081)
cd bank-service
mvn spring-boot:run

# 3. Demarrer l'application principale (port 8080)
cd car-crud
mvn spring-boot:run
```

Console RabbitMQ : **http://localhost:15672** (guest / guest)

## Base de donnees

L'application utilise **H2 en mode fichier** (`./data/cardb`). Les donnees persistent entre les redemarrages.

Console H2 disponible sur : **http://localhost:8080/h2-console**
- JDBC URL : `jdbc:h2:file:./data/cardb`
- Utilisateur : `sa`
- Mot de passe : *(vide)*

## Structure du projet

```
src/main/java/com/examples/carcrud/
├── config/          # Configuration securite, intercepteur web, seeder
├── controller/      # Controleurs MVC (Admin, User, Login)
├── dto/             # Objets formulaire + CarReport
├── model/           # Entites JPA + beans scopes (SessionCart, RequestTimer, AppVisitCounter)
├── repository/      # Interfaces Spring Data JPA
└── service/         # Logique metier + CarReportBuilder (prototype)

src/main/resources/
├── static/css/      # Feuille de style
├── templates/       # Templates Thymeleaf
│   ├── fragments/   # Barres laterales (admin/user)
│   ├── admin/       # Pages admin (listes, formulaires, details, rapport)
│   └── user/        # Pages utilisateur (accueil, location, achat, panier)
└── application.properties
```

## Captures d'ecran

### Page de connexion
![Login](screenshots/img_6.png)

### Espace Utilisateur

| Page d'accueil | Location | Achat | Panier |
|:-:|:-:|:-:|:-:|
| ![Main](screenshots/img_7.png) | ![Rent](screenshots/img_8.png) | ![Buy](screenshots/img_9.png) | ![Cart](screenshots/img_10.png) |

### Espace Administrateur

| Liste des voitures | Creer une voiture |
|:-:|:-:|
| ![Car List](screenshots/img.png) | ![Create Car](screenshots/img_1.png) |

| Rapport d'inventaire | Liste des locations |
|:-:|:-:|
| ![Report](screenshots/img_2.png) | ![Rentals](screenshots/img_3.png) |

| Liste des achats | Liste des paiements |
|:-:|:-:|
| ![Purchases](screenshots/img_4.png) | ![Payments](screenshots/img_5.png) |
