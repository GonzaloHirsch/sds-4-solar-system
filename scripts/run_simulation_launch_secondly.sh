#!/bin/bash

launch_base=61148580

#!/bin/bash
for i in {-120..0}
do
  launch=$(( i + launch_base ))
  echo "Second $i, launch at $launch"
  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -tf $3 -tm $2 -dt $1 -rs $4 -bt $launch
  python3 ./post/post_launch.py -t els -l $launch
done