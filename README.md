# ImgBin
Résoudre le problème de Binarisation d'Image en utilisant le modèle du flot

## Prérequis
Optionel: ```Graphviz``` doivent être installé sur votre système pour pouvoir utiliser ```--withReport```

## Utilisation
```bash
sh test.sh # Lancer l'algo minCut sur un petit graph
sh launch.sh path/to/file.txt METHOD REPORT # Lancer minCut sur file.txt
    METHOD: --method=preflow|ford_fulkerson
    REPORT: --withReport|empty
```

Les représentations graphiques se trouvent dans le dossier ```graphviz``` après l'exécution du script avec l'option ```--withReport```

**Attention**: le test ne donne pas les graphes graphviz. Toute exécution donne un résultat graphique et textuel (console).

## Exemples

```bash
sh launch.sh examples/4x4.txt --method=preflow --withReport # Préflot, sans graphviz
sh launch.sh examples/4x4.txt --method=ford_fulkerson # Fulkerson, sans graphviz
```

## Documentation
[Java API](https://mhoangvslev.github.io/ImgBin/)

## Illustration

Les résultats pour les graphes de test sont sous ```gif/```

### examples/4x4.txt
#### Ford-Fulkerson
![](gif/ford_fulkerson.gif)

#### Push-Relabel
![](gif/push_relabel.gif)

