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

**NOTE:** The information for the static data is given by NASA and the assignment

## Simulation
### Singular Run
Before running the script, it is needed permissions:
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

Before running the script, it is needed permissions:
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

Before running the script, it is needed permissions:
```
chmod u+x ./scripts/run_simulation_no_ship.sh
```

To run the simulation, the script can be used. It runs the simulation and post processes information to generate animation file:
```
./scripts/run_simulation_no_ship.sh 1 1800 31536000
```

The first argument is the delta time, the second argument is the delta multiplicator, and the third argument is the total time. Times are in seconds.

The present configuration is delta of 1 second, taking measurements every 1800 dts (30 minuts), and the simulation runs for 31536000 seconds (365 days)  

## Post Processing
### Oscillator Trajectory Graph
In order to plot the trajectory for the oscillators, first the simulation has to be run. To plot the trajectory for the 4 different methods, we run:
```
python3 ./post/post_oscillator.py -t p
```

### Oscillator Extract Errors
In order to calculate and extract the Mean Cuadratic Error from the simulation, we run:
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