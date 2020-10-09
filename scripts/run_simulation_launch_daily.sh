#!/bin/bash

#!/bin/bash
for i in {0..365}
do
  launch=$(( i*86400 ))
  echo "Day $i, launch at $launch"
  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -tf $3 -tm $2 -dt $1 -rs $4 -bt $launch
  python3 ./post/post_launch.py -t el -l $launch
done