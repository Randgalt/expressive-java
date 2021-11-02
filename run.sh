#!/usr/bin/env zsh

jenv --version

HOME=$(PWD)

echo "Building Java 15 version"
cd "$HOME/java-15" || exit
jenv shell 15
export JAVA_HOME=`jenv javahome`
mvn clean verify

echo ""
echo "Building Java 8 version"
cd "$HOME/java-8" || exit
jenv shell 1.8
export JAVA_HOME=`jenv javahome`
mvn clean verify

# clear

echo "Java 15 version"
cd "$HOME/java-15" || exit
java --enable-preview -cp "$HOME/java-15/target/classes" examples.SimpleInterpreter

echo ""
echo "Java 8 version"
java -cp "$HOME/java-8/target/classes" examples.SimpleInterpreter
