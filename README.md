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
