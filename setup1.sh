javac *.java;
./killregistry.sh
./register.sh
java RMIServerImpl config1.txt 1&
