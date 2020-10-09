import numpy as np
import math
import argparse
import random as rnd
import statistics
import matplotlib.pyplot as plt
import matplotlib.ticker as mtick
# https://matplotlib.org/3.1.1/gallery/text_labels_and_annotations/date.html
import matplotlib.dates as mdates
from matplotlib.ticker import (MultipleLocator, FormatStrFormatter, AutoMinorLocator)
import datetime

# Files
INPUT_FILE = "./parsable_files/output.txt"
DISTANCES_FILE = "./parsable_files/launch_distances.txt"

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
    # Contents are:
    # - Date of launch
    # - Date of launch in seconds
    # - Time of flight
    # - Minimum distance to mars
    wf.write('{} {} {} {}\n'.format((INITIAL_DATE + datetime.timedelta(seconds=launch)).strftime('%Y-%m-%d'), launch, time_to_min_distance, min_distance))
    wf.close()

# Plots the distances to mars for each launch
# https://www.kite.com/python/answers/how-to-plot-dates-on-the-x-axis-of-a-matplotlib-plot-in-python
# https://stackoverflow.com/questions/21423158/how-do-i-change-the-range-of-the-x-axis-with-datetimes-in-matplotlib
def plot_launches(filename):
    f = open(filename, 'r')
    dates = []
    times = []
    launches = []
    distances = []

    for line in f:
        data = line.rstrip("\n").split(" ")
        dates.append(data[0])
        launches.append(float(data[1]))
        times.append(float(data[2]))
        distances.append(float(data[3]))

    f.close()

    min_index = np.argmin(distances)
    print("Minimum distance to Mars achieved:")
    print("\tLaunch Day:", dates[min_index], "(" + str(launches[min_index]) + "[s])")
    print("\tArrival day:", (INITIAL_DATE + datetime.timedelta(seconds=times[min_index])).strftime('%Y-%m-%d') + "[s]")
    print("\tTime of Flight:", str(times[min_index]) + "[s]")
    print("\tDistance to Mars:", str(distances[min_index]) + "[km]")

    x_values = [datetime.datetime.strptime(d,"%Y-%m-%d").date() for d in dates]
    y_values = distances

    ax = plt.gca()
    formatter = mdates.DateFormatter("%d-%m-%Y")
    ax.xaxis.set_major_formatter(formatter)
    locator = mdates.DayLocator()
    ax.xaxis.set_major_locator(locator)
    plt.scatter(x_values, y_values)
    # Format the date into months & days
    plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%m-%d'))
    # Change the tick interval
    plt.gca().xaxis.set_major_locator(mdates.DayLocator(interval=30))
    # Puts x-axis labels on an angle
    plt.gca().xaxis.set_tick_params(rotation = 30)
    plt.show()

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
        extract_launch(INPUT_FILE, launch_delta, DISTANCES_FILE)
    elif args.process_type == PLOT_LAUNCH:
        plot_launches(DISTANCES_FILE)

# call main
if __name__ == '__main__':
    main()