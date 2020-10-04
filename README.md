# Simulación de Sistemas - TP4

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