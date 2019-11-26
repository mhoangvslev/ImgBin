rm -rf graphviz
mkdir graphviz
mvn clean install
mvn exec:java -Dexec.mainClass="com.tp01.imgbin.Main" -Dexec.args="$1 $2 $3" 
rm -rf graphviz/*.dot