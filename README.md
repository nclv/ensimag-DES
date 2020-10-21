# Simulation d'une équipe de robots pompiers.

- `src/`: contient les classes fournies par les enseignants
  - `LecteurDonnees.java` : lit tous les elements d'un fichier de description de donnees (cases, incendies et robots) et les affiche.
    
    _A vous de MODIFIER cette classe (ou en ecrire une nouvelle) pour creer les objets correspondants à vos propres classes_
  - `TestLecteurDonnees.java` : lit un fichier de donnees et affiche son contenu
  - `TestInvader.java` : cree un simulateur "mini Invaders" dans une fenetre graphique

- `cartes/`: quelques exemples de fichiers de donnees

- `bin/gui.jar`: archive Java contenant les classes de l'interface graphique. Voir un exemple d'utilisation dans `TestInvader.java`

- `doc`: la documentation (API) des classes de l'interface graphique contenues dans `gui.jar`. 
    
    Point d'entrée: `index.html`

- `Makefile`: quelques explications sur la compilation en ligne, notamment la notion de classpath et l'utilisation de `gui.jar`

## Commandes

```bash
cd bin/
java TestLecteurDonnees ../cartes/desertOfDeath-20x20.map
```

## KISS

On va analyser nos entités et ce qu'elles doivent savoir.

 - les cases, quel est mon type ?
 - les incendies, quelle est mon intensité ?
 - les robots, quel est mon type ?
 - la carte, où sont les incendies/robots/cases ?
   - quels incendies peuvent être éteint par le robot placé sur telle position particulière ?
   - est-ce que le mouvement proposé est autorisé ?
   - comment représenter les voisins ?
 - l'état de l'ensemble des entités, à quoi ressemble la carte en ce moment ?
   - quel est l'historique des déplacements ? (ex: R1 éteint I2) qui a éteint quel(s) incendie(s) ?
 - UI
   - où est-ce que l'on doit afficher ?
   - quand est-ce que l'on doit afficher ?

---

Les incendies/robots doivent savoir où ils sont sur la carte. Ils contiennent donc leur position (x, y). Mais on veut aussi savoir ce qui se trouve sur la case spécifique (x, y). Faire une boucle sur les incendies/robots pour trouver si leurs coordonnées correspondent devient vite long.

On ne stocke pas (x, y) soit dans la carte, ou l'entité, ou dans les deux. On peut faire une classe `Relation` qui a deux tables de hashage de l'entité vers sa position et d'une position vers une liste d'entités. Il y a toujours un problème de synchronisation mais tout est dans le même objet.

---

Actuellement les robots et incendies sont couplés à la classe Case. On ne peut pas comprendre ces classes sans comprendre la classe Case. Si on arrive à les découpler on pourra raisonner sur chacune d'elles indépendamment de la classe Case.

---

Utiliser le design pattern [type-object](https://gameprogrammingpatterns.com/type-object.html) pour les robots.

---

Comment organiser le projet en packages ?

 - `robots/` contient toutes les classes des robots.

---

Utiliser un logger.

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// attribute in the class
private static final Logger LOGGER = LoggerFactory.getLogger(<myclass>.class);
// in a method
LOGGER.info("blabla")
```

---

TODO:
 - Revoir les dépendances de LecteurDonnees.java et les éliminer.