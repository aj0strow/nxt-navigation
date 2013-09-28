source clean.sh;
echo -n "compiling project... ";
find src -type f -name *.java | xargs nxjc -d classes;
cd classes && nxjlink -o ../Lab3.nxj Lab3;
cd .. && echo "complete!"