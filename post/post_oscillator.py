import numpy as np
import math
import argparse
import random as rnd
import statistics
import matplotlib.pyplot as plt
import matplotlib.ticker as mtick
from matplotlib.ticker import (MultipleLocator, FormatStrFormatter, AutoMinorLocator)

# Files with the values
BEEMAN_FILE = "./parsable_files/beeman.txt"
VERLET_FILE = "./parsable_files/verlet.txt"
GEAR_FILE = "./parsable_files/gear.txt"
ANALYTIC_FILE = "./parsable_files/analytic.txt"
ERRORS_FILE = "./parsable_files/errors_file.txt"
FILES = [ANALYTIC_FILE , VERLET_FILE, GEAR_FILE, BEEMAN_FILE]

PLOT = 'p'
EXTRACT_ERROR = 'ee'
PLOT_ERROR = 'pe'
BEEMAN = 'Beeman'
VERLET = 'Verlet'
GEAR = 'Gear'


# Plots the information for each file
def plot_oscillator_graphs():

    file_information = {}
    for file in FILES:
        times, positions = extract_info_for_file(file)
        file_information[file] = [times, positions]

    for file in file_information:
        plain_filename = file.rstrip("\t\n").split("/")[2]
        name = plain_filename.split(".")[0]
        if name == 'analytic':
            name = 'analítico'
        plt.plot(file_information[file][0], file_information[file][1], 'o', label=name, markersize=2)

    plt.gca().set_xlabel("Tiempo [s]")
    plt.gca().set_ylabel("Posición de la partícula [m]")
    plt.legend()
    plt.show()


# Plots the information for each file
def extract_errors():
    errors = {}
    file_information = {}
    for file in FILES:
        times, positions = extract_info_for_file(file)
        file_information[file] = [times, positions]

    for file in file_information:
        if file != ANALYTIC_FILE:
            e = np.subtract(file_information[ANALYTIC_FILE][1], file_information[file][1])**2
            errors[file] = np.sum(e) / len(e)

    return errors

# Extracting the time and positions for a file
def extract_info_for_file(filename):
    f = open(filename, 'r')
    positions = []
    times = []

    for line in f:
        data = line.rstrip("\t\n").split(" ")
        time = float(data[0])
        x = float(data[1])

        times.append(time)
        positions.append(x)

    return times, positions


def save_errors(errors, dt):
    wf = open(ERRORS_FILE, 'a')
    wf.write('{}\n'.format(dt))
    wf.write('{} {}\n'.format(BEEMAN, errors[BEEMAN_FILE]))
    wf.write('{} {}\n'.format(GEAR, errors[GEAR_FILE]))
    wf.write('{} {}\n'.format(VERLET, errors[VERLET_FILE]))


def plot_errors():
    f = open(ERRORS_FILE, 'r')
    times = []
    beeman =[]
    verlet =[]
    gear = []

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            times.append(float(data[0]))
        else:
            type = data[0]
            error = float(data[1])

            if type == BEEMAN:
                beeman.append(error)
            elif type == GEAR:
                gear.append(error)
            elif type == VERLET:
                verlet.append(error)

    times, beeman, gear, verlet = zip(*sorted(zip(times, beeman, gear, verlet)))
    plt.plot(times, beeman, 'o', label=BEEMAN, markersize=5)
    plt.plot(times, gear, 'o', label=GEAR, markersize=5)
    plt.plot(times, verlet, 'o', label=VERLET, markersize=5)

    plt.yscale("log")
    plt.xscale("log")
    plt.gca().set_xlabel("Delta de Tiempo [s]")
    plt.gca().set_ylabel("Error Cuadrático Medio [m^2]")
    plt.legend()
    plt.show()

# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Post processing for the points data to generate data statistics")

    # add arguments
    parser.add_argument('-dt', dest='delta', required=False)
    parser.add_argument('-t', dest='process_type', required=True)
    args = parser.parse_args()

    if args.process_type == PLOT:
        plot_oscillator_graphs()
    elif args.process_type == EXTRACT_ERROR:
        errors = extract_errors()
        save_errors(errors, args.delta)
    elif args.process_type == PLOT_ERROR:
        plot_errors()

# call main
if __name__ == '__main__':
    main()