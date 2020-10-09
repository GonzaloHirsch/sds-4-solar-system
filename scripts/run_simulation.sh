#!/bin/bash

java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -tf $3 -tm $2 -dt $1 -rs $4 -bt $5

python3 ./visualization/process.py

python3 ./post/post_launch.py -t el -l $5