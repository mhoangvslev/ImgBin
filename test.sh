rm -rf graphviz
mkdir graphviz
mvn clean install
mvn test
rm -rf graphviz/*.dot