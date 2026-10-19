#!/bin/bash
# Prueft ein CustomDungeon-Design ohne Minecraft (Stubs + Erreichbarkeit, Bloecke, Chunk-Konsistenz, Fluessigkeiten).
# Aufruf: tools/dungeoncheck/check.sh <Klasse in dim/dungeon/design> [seed...]   (braucht Java 17+ und Python 3)
# Ergebnis in tools/dungeoncheck/work_<Klasse>/ (report.txt, vol.bin); Bilder: python3 render2.py work_<Klasse> iso|top|slice y y2|section u=0
set -e
D=$(cd "$(dirname "$0")" && pwd)
CLS=$1; shift
J=$D/../../src/main/java/com/space_m/dim/dungeon
SRC=$J/design/$CLS.java
W=$D/work_$CLS
rm -rf "$W"; mkdir -p "$W/stubs/com/space_m/dim/dungeon/design" "$W/classes"
python3 "$D/gen_stubs2.py" "$W/stubs" "$J/CustomDungeon.java" "$J/DungeonOut.java" "$SRC" || { echo "STUBS/BLOECKE FEHLERHAFT"; exit 3; }
cp "$J/CustomDungeon.java" "$J/DungeonOut.java" "$W/stubs/com/space_m/dim/dungeon/"; cp "$D/DCheck.java.txt" "$W/stubs/com/space_m/dim/dungeon/DCheck.java"
cp "$SRC" "$W/stubs/com/space_m/dim/dungeon/design/"
if ! javac -nowarn -d "$W/classes" $(find "$W/stubs" -name '*.java') 2> "$W/javac.txt"; then grep -v "^Note:\|JAVA_TOOL" "$W/javac.txt" | head -40; echo "KOMPILIERFEHLER"; exit 2; fi
java -Xmx2g -cp "$W/classes" com.space_m.dim.dungeon.DCheck com.space_m.dim.dungeon.design.$CLS "$W" "$@" 2>&1 | grep -v JAVA_TOOL
