#!/bin/bash

> ./parsable_files/analytic.txt
> ./parsable_files/beeman.txt
> ./parsable_files/gear.txt
> ./parsable_files/verlet.txt

java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm 50 -dt 0.0001 -ra
java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm 50 -dt 0.0001 -rn b
java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm 50 -dt 0.0001 -rn g
java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -tf 5 -tm 50 -dt 0.0001 -rn v

python3 ./post/post_oscillator.py
