# Simulación de Sistemas - TP4

## Permissions
In order to use all of the scripts, we need to give them run permission:
```
find ./scripts/ -type f -iname "*.sh" -exec chmod +x {} \;
```

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

The properties for each column are Radius, Position (x), Position (y), Color (R), Color (G) and Color (B)

**NOTE:** Contents are tab(\t) separated

**NOTE-2:** The RGB contents is the color of the particle

## Pre Processing - Deprecated
**NOTE:** As assignment changed, this is no longer used

In order to obtain the velocity and position of the ship, we use a python script to calculate it:
```
python3 ./preprocessing/calculate_spaceship_data.py -V 7.12 -v 8 -o 1500
```

The -V parameter is the orbital velocity of the station, -v parameter is the orbital velocity of the ship and -o is the orbital distance of the station. 
Beware that if your ship will not initiate flight at t = 0, the -v must be 0 km/s.

Units are in km and km/s.

## Simulation
### Singular Run
It runs a simulation given the delta of time.

To run the analytical and numerical simulations at the same time, the script can be used:
```
./scripts/run_oscillator.sh 0.0001 50
```

Where `0.0001` is the delta of time, and `50` is the amount of delta of time to be skipped each time when generating output. 

### Multiple Run
Another option to generate data is to run the script that generates data using dt from 10^-2 to 10^-8

To run the analytical and numerical simulations at the same time, the script can be used:
```
./scripts/run_oscillator_all.sh 20
```

Where `20` is the amount of delta of time to be skipped each time when generating output. 

### System Simulation without Ship
This option is to run the simulation without the ship.

To run the simulation, the script can be used. It runs the simulation and post processes information to generate animation file:
```
./scripts/run_simulation.sh 0.1 216000 31536000 ns 0
```

The first argument is the delta time, the second argument is the delta multiplicator, the third argument is the total time and the fourth argument indicates whether or not the ship is simulated(ws/ns). Times are in seconds.

The present configuration is delta of 0.1 second, taking measurements every 216000 dts (6 hours), and the simulation runs for 31536000 seconds (365 days). Without the ship. Blastoff at T minus 0 seconds

### System Simulation with Ship
This option is to run the simulation with the ship.

To run the simulation, the script can be used. It runs the simulation and post processes information to generate animation file:
```
./scripts/run_simulation.sh 0.1 216000 31536000 ws 10
```

The first argument is the delta time, the second argument is the delta multiplicator, the third argument is the total time and the fourth argument indicates whether or not the ship is simulated(ws/ns). Times are in seconds.

The present configuration is delta of 0.1 second, taking measurements every 216000 dts (6 hours), and the simulation runs for 31536000 seconds (365 days). With the ship . Blastoff T minus 10 seconds

### System Simulation with [Biweekly|Daily|Hourly|Minutely|Secondly] Launches
There are several scripts to run launched every 2 weeks, 1 day, 1 hour, 1 minute and 1 second. Those scripts are used to simulate many launches in order to extract minimum distance to Mars.

The results of running those scripts are down bellow, but take similar input to previous specified scripts.

The base launch dates have to be specified in most of them.

## Post Processing
There are a lot of post processing options, not all are specified here.

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

## Results
### Launch Dates
Analyzing bi-weekly launch dates we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-09-05 (61084800.0[s])
	Arrival day: 2022-11-28[s]
	Time of Flight: 68385600.0[s]
	Distance to Mars: 1476863.4794012152[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_weekly.sh 100 108 126144000 ws
```

Analyzing daily launch dates in a month range given by the previous week, we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-09-06 (61171200.0[s])
	Arrival day: 2022-11-29[s]
	Time of Flight: 68461200.0[s]
	Distance to Mars: 442850.2584836569[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_daily.sh 100 108 94608000 ws
```

Analyzing hourly launch dates in a 2-day range given by the previous date, we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-09-05 19:00:00 (61153200.0[s])
	Arrival day: 2022-11-29 06:00:00[s]
	Time of Flight: 68450400.0[s]
	Distance to Mars: 58278.764735948986[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_hourly.sh 100 108 126144000 ws
```

Analyzing minutely launch dates in a 2-hour range given by the previous date (taking into account the change in delta, we do a previous day also), we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-09-05 17:43:00 (61148580.0[s])
	Arrival day: 2022-11-29 06:00:00[s]
	Time of Flight: 68450400.0[s]
	Distance to Mars: 13002.458901888458[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_minutely.sh 10 1080 126144000 ws
```

Analyzing secondly launch dates in a 2-minute range given by the previous date, we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-09-05 17:41:58 (61148518.0[s])
	Arrival day: 2022-11-29 06:00:00[s]
	Time of Flight: 68450400.0[s]
	Distance to Mars: 13691.899442978884[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_secondly.sh 1 10800 126144000 ws
```

Animation was generated with:
```
 - dt   -> 1 seg
 - dt2  -> 10800 seg (3 hs)
 - total time -> 126144000
 - launch -> 61148518
```

### Launch Dates with V0 = 4
Analyzing bi-weekly launch dates we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-07-25 00:00:00 (57456000.0[s])
	Arrival day: 2023-05-03 09:00:00[s]
	Time of Flight: 81853200.0[s]
	Distance to Mars: 71700.90650821925[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_weekly.sh 10 1080 126144000 ws 4
```

Analyzing daily launch dates in a month range given by the previous week, we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-07-25 00:00:00 (57456000.0[s])
	Arrival day: 2023-05-03 09:00:00[s]
	Time of Flight: 81853200.0[s]
	Distance to Mars: 71700.90650821925[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_daily.sh 10 1080 94608000 ws 4
```

Analyzing hourly launch dates in a 2-day range given by the previous date, we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-07-24 22:00:00 (57448800.0[s])
	Arrival day: 2023-05-02 06:00:00[s]
	Time of Flight: 81756000.0[s]
	Distance to Mars: 10559.614544166609[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_hourly.sh 10 1080 126144000 ws 4
```

Analyzing minutely launch dates in a 2-hour range given by the previous date (taking into account the change in delta, we do a previous day also), we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-07-24 21:47:00 (57448020.0[s])
	Arrival day: 2023-05-29 00:00:00[s]
	Time of Flight: 84067200.0[s]
	Distance to Mars: 235.6650510401519[km]
```
Alternatively, 2 more tentative dates were found:
```
Date 1: 57447660
Date 2: 57447360 -> This one is more promising, will test with this one
```
This is obtained by running:
```
./scripts/run_simulation_launch_minutely.sh 10 1080 126144000 ws 4
```

Analyzing secondly launch dates in a 2-minute range given by the previous date, we obtain:
```
Minimum distance to Mars achieved:
	Launch Day: 2022-07-24 21:35:40 (57447340.0[s])
	Arrival day: 2023-05-02 02:00:00[s]
	Time of Flight: 81741600.0[s]
	Distance to Mars: 2577.461178242103[km]
```
This is obtained by running:
```
./scripts/run_simulation_launch_secondly.sh 1 1200 126144000 ws 4
```

Animation was generated with:
```
 - dt   -> 1 seg
 - dt2  -> 1200 seg (20 mins)
 - total time -> 126144000
 - launch -> 61148518
```