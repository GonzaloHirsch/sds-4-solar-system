import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;

public class SolarSystemSimulation {
    private final Particle SUN;
    private final Particle EARTH;
    private final Particle MARS;
    private final Particle SPACESHIP;

    // Total time elapsed
    private double totalTime = 0;
    // Time limit in millis
    private final double tf;
    // Delta time in millis
    private final double dt;
    // Time multiplicator
    private final int tm;

    // Structure for the results, it is a list that contains pairs of:
    // Time -> Array of positions and velocities
    // Expected size of the matrix is 4x4, to hold the X,Y,Vx,Vy for each of the particles
    // Size can be 3x4 in case the spaceship is not taken into account
    private final ArrayList<ImmutablePair<Double, double[][]>> results;

    // Array to hold the particles in order to improve code
    private final Particle[] particles;

    // Variable to define how many of the particles we take into account
    private final int particlesToSimulate;

    public SolarSystemSimulation(double tf, double dt, int tm, boolean includeShipInSimulation){
        this.tf = tf;
        this.dt = dt;
        this.tm = tm;

        // If the ship is included, we simulate the 4 particles, if not, we simulate the first 3
        this.particlesToSimulate = includeShipInSimulation ? 4 : 3;

        // Creating the structure for results, we store every tm*dt results
        int rows = (int) Math.floor(this.tf/(this.tm * this.dt));
        // We have the number of rows, so we specify initial capacity to improve performance
        this.results = new ArrayList<>(rows);

        SUN = null;
        EARTH = null;
        MARS = null;
        SPACESHIP = null;

        // Initializing the particle array
        this.particles = new Particle[]{SUN, EARTH, MARS, SPACESHIP};
    }

    public ArrayList<ImmutablePair<Double, double[][]>> simulateSolarSystem(){
        int index = -1;

        while (this.totalTime < this.tf){
            // Calculating the next position
            // position = this.integrator.analyticalSolution(this.totalTime, GAMMA, K, MASS);

            // Checking if results can be stored
            index = this.checkAndStoreResults(index);

            // Updating the time
            this.totalTime += this.dt;
        }


        return this.results;
    }

    private int checkAndStoreResults(int i){
        // Calculate the possible index to use
        int target_index = (int) Math.floor(this.totalTime / (this.dt * this.tm));
        if (target_index > i){
            // Creating the data structure for the particles
            double[][] particleData = new double[this.particlesToSimulate][4];
            for (int j = 0; j < this.particlesToSimulate; j++){
                particleData[j][0] = this.particles[j].getX();
                particleData[j][1] = this.particles[j].getY();
                particleData[j][2] = this.particles[j].getVx();
                particleData[j][3] = this.particles[j].getVy();
            }

            // Creating the pair for the output list
            ImmutablePair<Double, double[][]> data = new ImmutablePair<>(this.totalTime, particleData);

            // Adding our data points to the results
            this.results.add(data);

            return target_index;
        }
        return i;
    }
}
