import numpy as np
import math
import argparse
import random as rnd
import statistics
import matplotlib.pyplot as plt
import matplotlib.ticker as mtick
from matplotlib.ticker import (MultipleLocator, FormatStrFormatter, AutoMinorLocator)
import datetime

# Files
INPUT_FILE = "./parsable_files/output.txt"

# Variables
INITIAL_DATE = datetime.datetime(2020, 9, 28)

# Type variables
EXTRACT_LAUNCH = "el"
PLOT_LAUNCH = "pl"

# Given the data of the simulation
def extract_launch(filename, launch, outfilename):
    # Extracting the positions and velocities

    f = open(filename, 'r')
    positions = {}
    velocities = {}
    times = []

    particle_index = 0

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            times.append(float(data[0]))
            particle_index = 0
        else:
            data = [float(x) for x in data]

            # Checking if the indexes exist
            if not particle_index in positions:
                positions[particle_index] = []
            if not particle_index in velocities:
                velocities[particle_index] = []

            positions[particle_index].append([data[0], data[1]])
            velocities[particle_index].append([data[2], data[3]])
            particle_index += 1

    f.close()

    # Processing to determine minimum distance to mars within launch

    mars_pos = np.array(positions[2])
    ship_pos = np.array(positions[3])
    dist_partial = (mars_pos - ship_pos)**2
    distances = np.sqrt(dist_partial[:, 0] + dist_partial[:, 1])

    # Obtaining the minimum distance within launch
    index = 0
    for time in times:
        # If in another launch, take that launch
        if time >= launch:
            sub_distances = distances[index:]
            sub_times = times[index:]
            min_index = np.argmin(sub_distances)
            min_distance = sub_distances[min_index]
            time_to_min_distance = sub_times[min_index]
            break
        index += 1

    wf = open(outfilename, 'a')
    wf.write('{} {} {}\n'.format(INITIAL_DATE + datetime.timedelta(seconds=launch), time_to_min_distance, min_distance))
    wf.close()
    
# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Post processing for the points data to generate data statistics")

    # add arguments
    parser.add_argument('-l', dest='launch_delta', required=True)
    parser.add_argument('-t', dest='process_type', required=True)
    args = parser.parse_args()

    launch_delta = float(args.launch_delta)

    if args.process_type == EXTRACT_LAUNCH:
        extract_launch(INPUT_FILE, launch_delta)
    elif args.process_type == PLOT_LAUNCH:
        extract_launch(INPUT_FILE, launch_delta)

# call main
if __name__ == '__main__':
    main()