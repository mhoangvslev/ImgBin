# ImgBin
Résoudre le problème de Binarisation d'Image en utilisant le modèle du flot

## Prérequis
```Maven``` et ```Graphviz``` doivent être installé sur votre système
```bash
sudo apt install maven # ubuntu
```

## Utilisation
```bash
sh test.sh # Lancer l'algo minCut sur un petit graph
sh launch.sh path/to/file.txt METHOD REPORT # Lancer minCut sur file.txt
    METHOD: --method=preflow|ford_fulkerson_bfs|ford_fulkerson_sp
    REPORT: --withReport|empty
```

Les représentations graphiques se trouvent dans le dossier ```graphviz``` après l'exécution du script.

**Attention**: le test ne donne pas les graphes graphviz. Toute exécution donne un résultat graphique et textuel (console).

## Exemples
```bash
sh launch.sh examples/4x4.txt --method=preflow --withReport # Préflot, sans graphviz
sh launch_univ.sh examples/4x4.txt --method=preflow --withReport # Sous le proxy de l'université nantes
```