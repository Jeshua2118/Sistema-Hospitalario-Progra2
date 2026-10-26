#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes
find src/main/java -name '*.java' > build/fuentes.txt
javac -encoding UTF-8 -d build/classes @build/fuentes.txt
java -cp build/classes com.mycompany.sistemahospitalario.SistemaHospitalario
