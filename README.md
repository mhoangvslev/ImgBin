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
    METHOD: --method=preflow|ford_fulkerson
    REPORT: --withReport|empty
```

Les représentations graphiques se trouvent dans le dossier ```graphviz``` après l'exécution du script.

**Attention**: le test ne donne pas les graphes graphviz. Toute exécution donne un résultat graphique et textuel (console).

## Exemples

Pour l'utlisation sous les machines à l'Université, il est peut etre util d'ajouter dans ```~/.m2/settings.xml```

```xml
<settings>
 <proxies>
 <proxy>
      <active>true</active>
      <protocol>https</protocol>
      <host>proxy.ensinfo.sciences.univ-nantes.prive</host>
      <port>3128</port>
    </proxy>
  </proxies>
</settings>
```

```bash
sh launch.sh examples/4x4.txt --method=preflow --withReport # Préflot, sans graphviz
sh launch_univ.sh examples/4x4.txt --method=preflow --withReport # Sous le proxy de l'université nantes
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

