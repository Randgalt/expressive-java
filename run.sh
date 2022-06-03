#!/usr/bin/env zsh

HOME=$(PWD)

buildVersion() {
  echo "Building Java $1 version"
  cd "$HOME/java-$1" || exit
  export JENV_VERSION=$2
  export JAVA_HOME=$(jenv javahome)
  echo $(java -version)
  mvn clean verify
  echo ""
}

runVersion() {
  echo "========================="
  echo "Running Java $1 version"
  echo "========================="
  cd "$HOME/java-$1" || exit
  export JENV_VERSION=$2
  export JAVA_HOME=$(jenv javahome)
  if [ $1 -eq '8' ]; then
    java -cp "./target/classes" examples.SimpleInterpreter
  else
    java --enable-preview -cp "./target/classes" examples.SimpleInterpreter
  fi
  echo ""
}

buildVersion 8 1.8
buildVersion 15 15.0.2
buildVersion 17 17
buildVersion 19 19-ea

runVersion 8 1.8
runVersion 15 15.0.2
runVersion 17 17
runVersion 19 19-ea
