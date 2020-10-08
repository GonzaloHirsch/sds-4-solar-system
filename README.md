# Simulación de Sistemas - TP4

## Files
### Static File
The static file contains the mass and radius of the Sun, Earth, Mars and Spaceship. The contents of the file are:
```
sun_mass sun_radius
earth_mass earth_radius
mars_mass mars_radius
spaceship_mass spaceship_radius
```

### Dynamic File
The dynamic file contains the positions and velocities of the Sun, Earth, Mars and Spaceship. The contents of the file are:
```
0
sun_x sun_y sun_vx sun_vy
earth_x earth_y earth_vx earth_vy
mars_x mars_y mars_vx mars_vy
ship_x ship_y ship_vx ship_vy
```

**NOTE:** The information for the static data is given by NASA and the assignment

### Output File
The output file contains the output of the simulation, every N*dt. The contents are:
```
0
sun_x sun_y sun_vx sun_vy
earth_x earth_y earth_vx earth_vy
mars_x mars_y mars_vx mars_vy
ship_x ship_y ship_vx ship_vy
.
.
.
K*dt
sun_x_ sun_y sun_vx sun_vy
earth_x earth_y earth_vx earth_vy
mars_x mars_y mars_vx mars_vy
ship_x ship_y ship_vx ship_vy
```

### Animation File
The animation file contains the information to use ovito in order to generate an animation. The file format is **XYZ**.

The file contents are:
```
Number_of_particles
<Empty Line>
sun_radius      sun_x       sun_y       sun_R       sun_G       sun_B
earth_radius    earth_x     earth_y     earth_R     earth_G     earth_B
mars_radius     mars_x      mars_y      mars_R      mars_G      mars_B
ship_radius     ship_x      ship_y      ship_R      ship_G      ship_B
dummy_1_radius	250000000	250000000	dummy_1_R   dummy_1_G   dummy_1_B
dummy_2_radius	-250000000	250000000   dummy_2_R   dummy_2_G   dummy_2_B
dummy_3_radius	250000000	-250000000  dummy_3_R   dummy_3_G   dummy_3_B
dummy_4_radius	-250000000	-250000000  dummy_4_R   dummy_4_G   dummy_4_B
```

**NOTE:** Contents are tab(\t) separated
**NOTE-2:** The RGB contents is the color of the particle

## Pre Processing
In order to obtain the velocity and position of the ship, we use a python script to calculate it:
```
python3 ./preprocessing/calculate_spaceship_data.py -V 7.12 -v 8 -o 1500
```

The -V parameter is the orbital velocity of the station, -v parameter is the orbital velocity of the ship and -o is the orbital distance of the station. 

Units are in km and km/s.

## Simulation
### Singular Run
It runs a simulation given the delta of time.

Before running the script, it needs permission:
```
chmod u+x ./scripts/run_oscillator.sh
```

To run the analytical and numerical simulations at the same time, the script can be used:
```
./scripts/run_oscillator.sh 0.0001 50
```

Where `0.0001` is the delta of time, and `50` is the amount of delta of time to be skipped each time when generating output. 

### Multiple Run
Another option to generate data is to run the script that generates data using dt from 10^-2 to 10^-8

Before running the script, it needs permission:
```
chmod u+x ./scripts/run_oscillator_all.sh
```

To run the analytical and numerical simulations at the same time, the script can be used:
```
./scripts/run_oscillator_all.sh 20
```

Where `20` is the amount of delta of time to be skipped each time when generating output. 

### System Simulation without Ship
This option is to run the simulation without the ship.

Before running the script, it needs permission:
```
chmod u+x ./scripts/run_simulation.sh
```

To run the simulation, the script can be used. It runs the simulation and post processes information to generate animation file:
```
./scripts/run_simulation.sh 0.1 216000 31536000 ns
```

The first argument is the delta time, the second argument is the delta multiplicator, the third argument is the total time and the fourth argument indicates whether or not the ship is simulated(ws/ns). Times are in seconds.

The present configuration is delta of 0.1 second, taking measurements every 216000 dts (6 hours), and the simulation runs for 31536000 seconds (365 days). Without the ship. 

### System Simulation with Ship
This option is to run the simulation with the ship.

Before running the script, it needs permission:
```
chmod u+x ./scripts/run_simulation.sh
```

To run the simulation, the script can be used. It runs the simulation and post processes information to generate animation file:
```
./scripts/run_simulation.sh 0.1 216000 31536000 ws
```

The first argument is the delta time, the second argument is the delta multiplicator, the third argument is the total time and the fourth argument indicates whether or not the ship is simulated(ws/ns). Times are in seconds.

The present configuration is delta of 0.1 second, taking measurements every 216000 dts (6 hours), and the simulation runs for 31536000 seconds (365 days). With the ship .

## Post Processing
### Oscillator Trajectory Graph
In order to plot the trajectory for the oscillators, first the simulation has to be run. To plot the trajectory for the 4 different methods, we run:
```
python3 ./post/post_oscillator.py -t p
```

### Oscillator Extract Errors
In order to calculate and extract the Mean Quadratic Error from the simulation, we run:
```
python3 ./post/post_oscillator.py -t ee -dt 0.001
```

Where `0.001` is the delta of time used in the run to be analyzed 

### Oscillator Errors Plotting
In order to plot the oscillator errors, it is recommended to run the **Multiple Run** script in order to automate data recovery and avoid mistakes. We run:
```
python3 ./post/post_oscillator.py -t pe
```

This will generate a plot in logarithmic scales

## Visualization
Ovito is used for this.

In order to generate the animation files, a script can be used to convert simulation output into an animation file:
```
python3 ./visualization/process.py
```

**NOTE:** Scripts mentioned before already run this command, it is not necessary to run it, but if changes are introduced, it should be used.
**NOTE-2:** This script changes the radius of the objects in order to make them visible in the animation because normal radius would be very small and not/barely visible.