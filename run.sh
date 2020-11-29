#!/usr/bin/env bash

HOME=$(PWD)

echo "Building Java 15 version"
cd "$HOME/java-15" || exit
mvn clean verify

echo ""
echo "Building Java 8 version"
cd "$HOME/java-8" || exit
mvn clean verify

clear

echo "Java 15 version"
java --enable-preview -cp "$HOME/java-15/target/classes" examples.SimpleInterpreter

echo ""
echo "Java 8 version"
java --enable-preview -cp "$HOME/java-8/target/classes" examples.SimpleInterpreter
