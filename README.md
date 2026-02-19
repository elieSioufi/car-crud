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
        RT[RequestTimer<br><i>@RequestScope</i>]
        AV[AppVisitCounter<br><i>@ApplicationScope</i>]
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
        CS[CarService<br><i>@Singleton</i>]
        PS[PurchaseService]
        RS[RentalService]
        PMS[PaymentRecordService]
        CRB[CarReportBuilder<br><i>@Prototype</i>]
    end

    subgraph "Session Beans"
        SC[SessionCart<br><i>@SessionScope</i>]
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
