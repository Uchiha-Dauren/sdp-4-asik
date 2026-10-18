#!/bin/sh
# Compile and run the demo, then the tests
rm -rf out && mkdir out
javac --release 17 -d out $(find src -name "*.java") || exit 1
java -cp out shop.Main
java -cp out shop.OrderFacadeTest
