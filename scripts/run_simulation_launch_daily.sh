#!/bin/bash

launch_base=57456000

#!/bin/bash
for i in {-14..14}
do
  launch=$(( (i*86400) + launch_base ))
  echo "Day $i, launch at $launch"
  if [ "$#" -eq 4 ]; then
      java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -tf $3 -tm $2 -dt $1 -rs $4 -bt $launch
  fi
  if [ "$#" -eq 5 ]; then
      java -jar ./target/sds-tp4-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -tf $3 -tm $2 -dt $1 -rs $4 -bt $launch -v0 $5
  fi
  python3 ./post/post_launch.py -t eld -l $launch
done
