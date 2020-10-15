import numpy as np
import math
import argparse
import statistics
import matplotlib.pyplot as plt
import datetime

# Files
INPUT_FILE = "./parsable_files/energy_data.txt"

# NASA Values after 1 year
G = 6.693 * (10**(-20))
SUN_VALUE = 0.0
EARTH_NASA_X = 1.493890685719635 * (10**8)
EARTH_NASA_Y = 1.252824102724684 * (10**7)
MARS_NASA_X = -2.409985270575768 * (10**8)
MARS_NASA_Y = -4.333655301342551 * (10**7)

# Mass
EARTH_MASS = 5.97219*(10**24)
MARS_MASS = 6.47171*(10**23)
SUN_MASS = 1988500*(10**24)

EARTH = 0
MARS = 1


def calculateDistance(x1, y1, x2, y2):
    return math.sqrt((x1-x2)**2 + (y1-y2)**2)


def extractTimesAndPositions(filename):
    # Extracting the positions and velocities

    f = open(filename, 'r')
    times = []
    x_positions = {EARTH: [], MARS: []}
    y_positions = {EARTH: [], MARS: []}

    index = 0 # 0 = EARTH, 1 = MARS

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            # energies[float(data[0])] = 0
            times.append(float(data[0]))
            index = 0
        else:
            data = [float(x) for x in data]

            # Saving information about celestial body
            x_positions[index].append(float(data[0]))
            y_positions[index].append(float(data[1]))

            index += 1

    f.close()
    print(x_positions)
    print(y_positions)

    return times, x_positions, y_positions

# main() function -> creates graph
def main():

    dist_errors = []
    earth=[]
    mars=[]
    times, x_positions, y_positions = extractTimesAndPositions(INPUT_FILE)

    for i in range(0, len(times)):
        earth_dist = abs(calculateDistance(EARTH_NASA_X, EARTH_NASA_Y, x_positions[EARTH][i], y_positions[EARTH][i]))
        mars_dist = abs(calculateDistance(MARS_NASA_X, MARS_NASA_Y, x_positions[MARS][i], y_positions[MARS][i]))
        earth.append(earth_dist)
        mars.append(mars_dist)
        dist_errors.append(earth_dist + mars_dist)

    # Graph 1
    print(earth)
    print(mars)
    print (times)
    print (dist_errors)
    plt.scatter(times, dist_errors, color="red")
    plt.gca().set_ylabel("Error con posiciones de la NASA [km]")
    plt.gca().set_xlabel("Delta de Tiempo [s]")
    plt.yscale("log")
    plt.xscale("log")
    plt.show()

    N = len(times)
    width = 0.35
    ind = np.arange(N)
    plt.bar(ind, earth, width, label='Earth')
    plt.bar(ind + width, mars, width, label='Mars')

    # Labels
    plt.ylabel('Error con posiciones de la Nasa [km]')
    plt.yscale("log")
    plt.xticks(ind + width / 2, times)
    plt.legend(loc='best')
    plt.show()


# call main
if __name__ == '__main__':
    main()