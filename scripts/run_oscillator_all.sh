#!/bin/bash

for t in 0.01 0.001 0.0001 0.00001 0.000001 0.0000001 0.00000001
do
  echo "Running with dt $t"

  > ./parsable_files/analytic.txt
  > ./parsable_files/beeman.txt
  > ./parsable_files/gear.txt
  > ./parsable_files/verlet.txt

  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm $1 -dt $t -ra
  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm $1 -dt $t -rn b
  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm $1 -dt $t -rn g
  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm $1 -dt $t -rn v

  python3 ./post/post_oscillator.py -t ee -dt $t
done

python3 ./post/post_oscillator.py -t pe