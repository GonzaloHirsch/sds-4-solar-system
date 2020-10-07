import numpy as np
import math
import argparse
import random as rnd
import statistics
import matplotlib.pyplot as plt
import matplotlib.ticker as mtick
from matplotlib.ticker import (MultipleLocator, FormatStrFormatter, AutoMinorLocator)

# Files with the values
INPUT_FILE = "./parsable_files/output.txt"
OUTPUT_FILE = "./parsable_files/animation.xyz"

PROCESS_NO_SHIP = "ns"
PROCESS_WITH_SHIP = "ws"

MAX_MARS_DISTANCE = 250 * (10**6)

radius = [695700, 6371.01, 3389.92, 500]

SUN_MULTIPLICATOR = 50
PLANET_MULTIPLICATOR = 800
SHIP_MULTIPLICATOR = 10000
visualizing_radius = [radius[0] * SUN_MULTIPLICATOR, radius[1] * PLANET_MULTIPLICATOR, radius[2] * PLANET_MULTIPLICATOR, radius[3] * SHIP_MULTIPLICATOR]

def generate_system_without_ship_frames(filename, outfilename):
    f = open(filename, 'r')

    # Extract data

    processed_data = {}
    times = []

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            time = float(data[0])
            processed_data[time] = []
            times.append(time)
        else:
            point = [float(x) for x in data]
            processed_data[time].append(point)

    # Generate animation file

    f = open(outfilename, 'w')

    n = len(processed_data[times[0]]) + 4

    for time in times:
        f.write('{}\n'.format(n))
        f.write('\n')
        point_index = 0

        data_for_time = processed_data[time]

        # Adding the particles
        for point in data_for_time:
            f.write('{}\t{}\t{}\n'.format(visualizing_radius[point_index], point[0], point[1], point[2], point[3]))
            point_index += 1

        # Adding dummy particles
        f.write('{}\t{}\t{}\n'.format(0.00001, MAX_MARS_DISTANCE, MAX_MARS_DISTANCE))
        f.write('{}\t{}\t{}\n'.format(0.00001, -MAX_MARS_DISTANCE, MAX_MARS_DISTANCE))
        f.write('{}\t{}\t{}\n'.format(0.00001, MAX_MARS_DISTANCE, -MAX_MARS_DISTANCE))
        f.write('{}\t{}\t{}\n'.format(0.00001, -MAX_MARS_DISTANCE, -MAX_MARS_DISTANCE))

    f.close()

# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Post processing to generate animation frames")

    # add arguments
    parser.add_argument('-t', dest='process_type', required=True)
    args = parser.parse_args()

    if args.process_type == PROCESS_NO_SHIP:
        generate_system_without_ship_frames(INPUT_FILE, OUTPUT_FILE)
    elif args.process_type == PROCESS_WITH_SHIP:
        generate_system_without_ship_frames(INPUT_FILE, OUTPUT_FILE)

# call main
if __name__ == '__main__':
    main()