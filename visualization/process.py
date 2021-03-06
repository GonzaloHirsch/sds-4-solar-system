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

MAX_MARS_DISTANCE = 250 * (10**6)

radius = [695700, 6371.01, 3389.92, 500]

SUN_MULTIPLICATOR = 50
PLANET_MULTIPLICATOR = 1000
SHIP_MULTIPLICATOR = 7500
visualizing_radius = [radius[0] * SUN_MULTIPLICATOR, radius[1] * PLANET_MULTIPLICATOR, radius[2] * PLANET_MULTIPLICATOR, radius[3] * SHIP_MULTIPLICATOR]

COLOR_SUN = [235/255, 192/255, 52/255]
COLOR_EARTH = [52/255, 89/255, 235/255]
COLOR_MARS = [199/255, 59/255, 44/255]
COLOR_SHIP = [138/255, 135/255, 135/255]
COLORS = [COLOR_SUN, COLOR_EARTH, COLOR_MARS, COLOR_SHIP]

def generate_system_frames(filename, outfilename):
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
            cl = COLORS[point_index]
            f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(visualizing_radius[point_index], point[0], point[1], cl[0], cl[1], cl[2]))
            point_index += 1

        # Adding dummy particles
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, MAX_MARS_DISTANCE, MAX_MARS_DISTANCE, 0, 0, 0))
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, -MAX_MARS_DISTANCE, MAX_MARS_DISTANCE, 0, 0, 0))
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, MAX_MARS_DISTANCE, -MAX_MARS_DISTANCE, 0, 0, 0))
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, -MAX_MARS_DISTANCE, -MAX_MARS_DISTANCE, 0, 0, 0))

    f.close()

# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Post processing to generate animation frames")

    # add arguments
    # parser.add_argument('-t', dest='process_type', required=True)
    args = parser.parse_args()

    generate_system_frames(INPUT_FILE, OUTPUT_FILE)

# call main
if __name__ == '__main__':
    main()