# Compilation
rm -rf bin/
mkdir -p bin
javac -source 1.7 -target 1.7 -d bin/ src/*.java
cd bin/
jar cfv ImgBin.jar *.class

# Execution
cd ../
mkdir -p graphviz
java -cp bin/ImgBin.jar Main $1 $2 $3