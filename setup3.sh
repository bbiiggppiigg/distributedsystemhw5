javac *.java;
./killregistry.sh
./register.sh
java RMIServerImpl config3.txt 1&
java RMIServerImpl config3.txt 2&
java RMIServerImpl config3.txt 3&
