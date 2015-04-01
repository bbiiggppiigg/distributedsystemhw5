javac *.java;
./killregistry.sh
./register.sh
java RMIServerImpl config5.txt 1&
java RMIServerImpl config5.txt 2&
java RMIServerImpl config5.txt 3&
java RMIServerImpl config5.txt 4&
java RMIServerImpl config5.txt 5&
