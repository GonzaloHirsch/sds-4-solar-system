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
DISTANCES_SECONDLY_FILE = "./parsable_files/launch_distances_secondly.txt"
DISTANCES_MINUTELY_FILE = "./parsable_files/launch_distances_minutely.txt"
DISTANCES_HOURLY_FILE = "./parsable_files/launch_distances_hourly.txt"
DISTANCES_DAILY_FILE = "./parsable_files/launch_distances_daily.txt"
DISTANCES_WEEKLY_FILE = "./parsable_files/launch_distances_weekly.txt"

# Variables
INITIAL_DATE = datetime.datetime(2020, 9, 28)

# Type variables
EXTRACT_SECONDLY_LAUNCH = "els"
EXTRACT_MINUTELY_LAUNCH = "elm"
EXTRACT_HOURLY_LAUNCH = "elh"
EXTRACT_DAILY_LAUNCH = "eld"
EXTRACT_WEEKLY_LAUNCH = "elw"

PLOT_SECONDLY_LAUNCH = "pls"
PLOT_MINUTELY_LAUNCH = "plm"
PLOT_HOURLY_LAUNCH = "plh"
PLOT_DAILY_LAUNCH = "pld"
PLOT_WEEKLY_LAUNCH = "plw"

PLOT_VELOCITY = "pv"

TYPE_SECOND = "secondly"
TYPE_MINUTE = "minutely"
TYPE_HOUR = "hourly"
TYPE_DAY = "dayly"
TYPE_WEEK = "weekly"

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
def plot_launches(filename, type):
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
    print("\tLaunch Day:", (INITIAL_DATE + datetime.timedelta(seconds=launches[min_index])).strftime('%Y-%m-%d %H:%M:%S') , "(" + str(launches[min_index]) + "[s])")
    print("\tArrival day:", (INITIAL_DATE + datetime.timedelta(seconds=times[min_index])).strftime('%Y-%m-%d %H:%M:%S') + "[s]")
    print("\tTime of Flight:", str(times[min_index]) + "[s]")
    print("\tDistance to Mars:", str(distances[min_index]) + "[km]")

    if type == TYPE_MINUTE or type == TYPE_SECOND or type == TYPE_HOUR:
        date_index = 0
        x_values = []
        for i in range(len(launches)):
            sub_date = INITIAL_DATE + datetime.timedelta(seconds=launches[i])
            x_values.append(sub_date)
        y_values = distances
    elif type == TYPE_DAY:
        x_values = [datetime.datetime.strptime(d,"%Y-%m-%d").date() for d in dates]
        y_values = distances
    elif type == TYPE_WEEK:
        x_values = [datetime.datetime.strptime(d,"%Y-%m-%d").date() for d in dates]
        y_values = distances

    ax = plt.gca()
    formatter = mdates.DateFormatter("%d-%m-%Y")
    ax.xaxis.set_major_formatter(formatter)
    locator = mdates.DayLocator()
    ax.xaxis.set_major_locator(locator)
    plt.scatter(x_values, y_values, color="red")
    # Removes the scientific notation on top
    # https://stackoverflow.com/questions/28371674/prevent-scientific-notation-in-matplotlib-pyplot
    ax.ticklabel_format(style='plain', axis='y')
    # Format the date into months & days
    # Change the tick interval

    if type == TYPE_SECOND:
        interval = 1
        plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%H:%M:%S'))
        plt.gca().xaxis.set_major_locator(mdates.SecondLocator(interval=interval))
        plt.gca().set_ylabel("Distancia a Marte [km]")
        plt.gca().set_xlabel("Fecha de Despegue [" + dates[0] + "]")
    elif type == TYPE_MINUTE:
        interval = 10
        plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%H:%M:%S'))
        plt.gca().xaxis.set_major_locator(mdates.MinuteLocator(interval=interval))
        plt.gca().set_ylabel("Distancia a Marte [km]")
        plt.gca().set_xlabel("Fecha de Despegue [" + dates[0] + "]")
    elif type == TYPE_HOUR:
        interval = 4
        plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%m-%d %H:%M'))
        plt.gca().xaxis.set_major_locator(mdates.HourLocator(interval=interval))
        plt.gca().set_ylabel("Distancia a Marte [km]")
        plt.gca().set_xlabel("Fecha de Despegue [Año " + dates[0].split("-")[0] + "]")
    elif type == TYPE_DAY:
        interval = 2
        plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%d-%m'))
        plt.gca().xaxis.set_major_locator(mdates.DayLocator(interval=interval))
        plt.gca().set_ylabel("Distancia a Marte [km]")
        plt.gca().set_xlabel("Fecha de Despegue [Año " + dates[0].split("-")[0] + "]")
    elif type == TYPE_WEEK:
        # https://stackoverflow.com/questions/46555819/months-as-axis-ticks
        interval = 2
        plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%m-%Y'))
        plt.gca().xaxis.set_major_locator(mdates.MonthLocator(interval=interval))
        plt.gca().set_ylabel("Distancia a Marte [km]")
        plt.gca().set_xlabel("Fecha de Despegue")
    # Puts x-axis labels on an angle
    plt.gca().xaxis.set_tick_params(rotation = 30)
    plt.show()

def plot_velocity_for_launch(filename, launch_date):
    # Extracting velocities, we do not care about positions here
    f = open(filename, 'r')
    velocities = []
    times = []

    particle_index = 0
    launch_index = -1
    time_index = 0

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            time = float(data[0])
            times.append(time)
            particle_index = 0
            if time >= launch_date and launch_index < 0:
                launch_index = time_index
            time_index += 1
        else:
            # If it is the ship
            if particle_index == 3:
                data = [float(x) for x in data]
                v_mod = math.sqrt((data[2] * data[2]) + (data[3] * data[3]))
                velocities.append(v_mod)
            particle_index += 1

    f.close()

    # Keeping only the data after the launch
    velocities = velocities[launch_index + 1:]
    times = times[launch_index + 1:]

    #dates = [(INITIAL_DATE + datetime.timedelta(seconds=t)).strftime('%Y-%m-%d %H:%M:%S') for t in times]
    dates = [(INITIAL_DATE + datetime.timedelta(seconds=t)).strftime('%Y-%m-%d %H:%M:%S') for t in times]
    x_values = [datetime.datetime.strptime(d,"%Y-%m-%d %H:%M:%S") for d in dates]
    y_values = velocities

    ax = plt.gca()
    formatter = mdates.DateFormatter("%d-%m-%Y")
    ax.xaxis.set_major_formatter(formatter)
    locator = mdates.DayLocator()
    ax.xaxis.set_major_locator(locator)
    plt.scatter(x_values, y_values, color="red")
    # Removes the scientific notation on top
    # https://stackoverflow.com/questions/28371674/prevent-scientific-notation-in-matplotlib-pyplot
    ax.ticklabel_format(style='plain', axis='y')
    # Format the date into months & days
    # Change the tick interval
    plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%m-%Y'))
    plt.gca().xaxis.set_major_locator(mdates.MonthLocator(interval=1))
    plt.gca().set_ylabel("Módulo de la Velocidad [km/s]")
    plt.gca().set_xlabel("Fecha [mes-año]")
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
    parser.add_argument('-l', dest='launch_delta', required=False)
    parser.add_argument('-t', dest='process_type', required=True)
    args = parser.parse_args()

    if args.launch_delta != None:
        launch_delta = float(args.launch_delta)

    if args.process_type == PLOT_VELOCITY:
        plot_velocity_for_launch(INPUT_FILE, launch_delta)
    elif args.process_type == EXTRACT_SECONDLY_LAUNCH:
        extract_launch(INPUT_FILE, launch_delta, DISTANCES_SECONDLY_FILE)
    elif args.process_type == EXTRACT_MINUTELY_LAUNCH:
        extract_launch(INPUT_FILE, launch_delta, DISTANCES_MINUTELY_FILE)
    elif args.process_type == EXTRACT_HOURLY_LAUNCH:
        extract_launch(INPUT_FILE, launch_delta, DISTANCES_HOURLY_FILE)
    if args.process_type == EXTRACT_DAILY_LAUNCH:
        extract_launch(INPUT_FILE, launch_delta, DISTANCES_DAILY_FILE)
    elif args.process_type == EXTRACT_WEEKLY_LAUNCH:
        extract_launch(INPUT_FILE, launch_delta, DISTANCES_WEEKLY_FILE)
    elif args.process_type == PLOT_SECONDLY_LAUNCH:
        plot_launches(DISTANCES_SECONDLY_FILE, TYPE_SECOND)
    elif args.process_type == PLOT_MINUTELY_LAUNCH:
        plot_launches(DISTANCES_MINUTELY_FILE, TYPE_MINUTE)
    elif args.process_type == PLOT_HOURLY_LAUNCH:
        plot_launches(DISTANCES_HOURLY_FILE, TYPE_HOUR)
    elif args.process_type == PLOT_DAILY_LAUNCH:
        plot_launches(DISTANCES_DAILY_FILE, TYPE_DAY)
    elif args.process_type == PLOT_WEEKLY_LAUNCH:
        plot_launches(DISTANCES_WEEKLY_FILE, TYPE_WEEK)

# call main
if __name__ == '__main__':
    main()