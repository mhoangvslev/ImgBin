rm -rf graphviz
mkdir graphviz
mvn clean install -Dhttp.proxyHost=proxy.ensinfo.sciences.univ-nantes.prive -Dhttp.proxyPort=3128
mvn exec:java -Dexec.mainClass="com.tp01.imgbin.Main" -Dexec.args="$1 $2 $3" 
rm -rf graphviz/*.dot