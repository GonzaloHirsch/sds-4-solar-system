import numpy as np
import math
import argparse
import statistics
import matplotlib.pyplot as plt
import datetime

# Files
INPUT_FILE = "./parsable_files/energy_data.txt"

# Variables
G = 6.693 * (10**(-20))
SUN_VALUE = 0.0
EARTH_INITIAL_X = 149318892.9636662
EARTH_INITIAL_Y = 13189363.57931255
EARTH_INITIAL_VX = -3.113279917782445
EARTH_INITIAL_VY = 29.55205189256462
MARS_INITIAL_X = 205944855.1842169
MARS_INITIAL_Y = 40239779.46528339
MARS_INITIAL_VX = -3.717406842095575
MARS_INITIAL_VY = 25.84914078301731

# Mass
EARTH_MASS = 5.97219*(10**24)
MARS_MASS = 6.47171*(10**23)
SUN_MASS = 1988500*(10**24)

def calculateCineticEnergy(masses, velocities):
    energy = 0
    for i in range(0, len(masses)):
        energy += ((1/2) * masses[i] * (velocities[i]**2))

    return energy

def calculatePotentialEnergy(masses, x_pos, y_pos):
    cummulative_energy = 0
    for i in range(0, len(masses)-1):
        for j in range(i+1, len(masses)):
            dist = calculateDistance(x_pos[i], y_pos[i], x_pos[j], y_pos[j])
            e = masses[i]*masses[j] / dist
            cummulative_energy += e

    cummulative_energy *= -G
    return cummulative_energy

def calculateVelocity(vx, vy):
    return math.sqrt(vx*vx + vy*vy)

def calculateDistance(x1, y1, x2, y2):
    return math.sqrt((x1-x2)**2 + (y1-y2)**2)

def calculateInitialEnergy():
    v_earth = calculateVelocity(EARTH_INITIAL_VX, EARTH_INITIAL_VY)
    v_mars = calculateVelocity(MARS_INITIAL_VX, MARS_INITIAL_VY)

    dse = calculateDistance(SUN_VALUE, SUN_VALUE, EARTH_INITIAL_X, EARTH_INITIAL_Y)
    dsm = calculateDistance(SUN_VALUE, SUN_VALUE, MARS_INITIAL_X, MARS_INITIAL_Y)
    dem = calculateDistance(EARTH_INITIAL_X, EARTH_INITIAL_Y, MARS_INITIAL_X, MARS_INITIAL_Y)

    ec = (1/2) * (EARTH_MASS * v_earth * v_earth + MARS_MASS * v_mars * v_mars)
    ep = -G * ((SUN_MASS * EARTH_MASS / dse) + (SUN_MASS * MARS_MASS / dsm) + (EARTH_MASS * MARS_MASS / dem))
    return ec + ep


def calculateFinalEnergies(filename, masses):
    # Extracting the positions and velocities

    f = open(filename, 'r')
    x_positions = []
    y_positions = []
    velocities = []
    times = []
    energies = []

    # SUN
    x_positions.append(SUN_VALUE)
    y_positions.append(SUN_VALUE)
    velocities.append(SUN_VALUE)

    index = 0 # 0 = EARTH, 1 = MARS

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            # energies[float(data[0])] = 0
            times.append(float(data[0]))

            # Calculate energies
            if index > 0:
                ec = calculateCineticEnergy(masses, velocities)
                ep = calculatePotentialEnergy(masses, x_positions, y_positions)
                energies.append(ec + ep)

            # Cleaning results to start again
            x_positions = []
            y_positions = []
            velocities = []
            x_positions.append(SUN_VALUE)
            y_positions.append(SUN_VALUE)
            velocities.append(SUN_VALUE)
        else:
            data = [float(x) for x in data]

            # Saving information about celestial body
            x_positions.append(float(data[0]))
            y_positions.append(float(data[1]))

            vx = float(data[2])
            vy = float(data[3])
            v = math.sqrt(vx*vx + vy*vy)
            velocities.append(v)

            index += 1

    f.close()

    # Repeat for last dt
    ec = calculateCineticEnergy(masses, velocities)
    ep = calculatePotentialEnergy(masses, x_positions, y_positions)
    energies.append(ec + ep)

    return times, energies

# main() function -> creates graph
def main():

    # celestial bodies masses
    masses = [SUN_MASS, EARTH_MASS, MARS_MASS]

    initial_energy = calculateInitialEnergy()
    times, energies = calculateFinalEnergies(INPUT_FILE, masses)

    e = np.abs(np.subtract(initial_energy, energies))
    percentage = e / np.abs(initial_energy) * 100

    # Graph 1
    plt.scatter(times, e, color="red")
    plt.gca().set_ylabel("Error de energia [kJ]")
    plt.gca().set_xlabel("Delta de Tiempo [s]")
    plt.yscale("log")
    plt.xscale("log")
    plt.show()

    # Graph 2
    plt.clf()
    plt.scatter(times, percentage, color="red")
    plt.gca().set_ylabel("Porcentaje de error de energia[%]")
    plt.gca().set_xlabel("Delta de Tiempo [s]")
    plt.xscale("log")
    plt.yscale("log")
    plt.show()

    plt.clf()
    N = len(times)
    width = 0.45
    ind = np.arange(N)
    plt.bar(ind, percentage, width)

    # Labels
    plt.ylabel('Porcentaje de error de energia[%]')
    plt.xlabel('Delta de Tiempo [s]')
    plt.yscale("log")
    plt.xticks(ind, times)
    plt.legend(loc='best')
    plt.show()



# call main
if __name__ == '__main__':
    main()