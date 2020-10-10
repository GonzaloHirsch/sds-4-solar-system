#!/bin/bash

launch_base=61171200

#!/bin/bash
for i in {-24..24}
do
  launch=$(( (i*3600) + launch_base ))
  echo "Hour $i, launch at $launch"
  java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -tf $3 -tm $2 -dt $1 -rs $4 -bt $launch
  python3 ./post/post_launch.py -t elh -l $launch
done
