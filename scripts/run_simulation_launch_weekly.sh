#!/bin/bash

echo "Runs bi-weekly launches for 4 years"

for i in {0..209}
do
  launch=$(( i*(86400*7) ))
  echo "Week $i, launch at $launch"
  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -tf $3 -tm $2 -dt $1 -rs $4 -bt $launch
  python3 ./post/post_launch.py -t elw -l $launch
done