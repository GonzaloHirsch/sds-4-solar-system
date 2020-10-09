import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;

public class SolarSystemSimulation {
    // Total time elapsed
    private double totalTime = 0;
    // Time limit in millis
    private final double tf;
    // Delta time in millis
    private final double dt;
    // Time multiplicator
    private final int tm;
    // Spaceship is in the air
    private boolean isInFlight;
    // Time since start of simulation that the spaceship must wait before blastoff
    private double blastoffTime;

    // Structure for the results, it is a list that contains pairs of:
    // Time -> Array of positions and velocities
    // Expected size of the matrix is 4x4, to hold the X,Y,Vx,Vy for each of the particles
    // Size can be 3x4 in case the spaceship is not taken into account
    private final List<ImmutablePair<Double, double[][]>> results;

    // Array to hold the particles in order to improve code
    private final Particle[] particles;

    // Variable to define how many of the particles we take into account
    private int particlesToSimulate = 4;

    // Variable to hold the gear predictions and derivatives for each particle
    private final Map<Integer, double[][]> gearDerivatives = new HashMap<>();
    private final Map<Integer, double[][]> gearPredictions = new HashMap<>();

    // Constants for the indexes in the gear data
    private static final int X_VALUES = 0;
    private static final int Y_VALUES = 1;

    // Gear predictor coefficients
    private final double[] gearAlphas = new double[]{3.0/20, 251.0/360, 1, 11.0/18, 1.0/6, 1.0/60};

    // Gear predictor delta powers and factorials for reduced computation
    private double[] gearDeltaFactorials;

    public SolarSystemSimulation(double tf, double dt, int tm, Particle sun, Particle earth, Particle mars, Particle spaceship, double blastoffTime){
        this.tf = tf;
        this.dt = dt;
        this.tm = tm;
        this.blastoffTime = blastoffTime;

        // Creating the structure for results, we store every tm*dt results
        int rows = (int) Math.floor(this.tf/(this.tm * this.dt)) + 1;
        // We have the number of rows, so we specify initial capacity to improve performance
        this.results = new ArrayList<>(rows);

        // Initializing the particle array
        this.particles = new Particle[]{sun, earth, mars, spaceship};

        if (blastoffTime == 0.0) {
            this.isInFlight = true;
            this.updateShipForBlastoff();
        } else {
            this.isInFlight = false;
            this.stationaryShip();
        }

        // Initializing the gear data
        this.initGearData();
    }

    public SolarSystemSimulation(double tf, double dt, int tm, Particle sun, Particle earth, Particle mars, Particle spaceship){
        this(tf, dt, tm, sun, earth, mars, spaceship, 0.0);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 SIMULATION RUNNING
    /////////////////////////////////////////////////////////////////////////////////////

    public List<ImmutablePair<Double, double[][]>> simulateSolarSystem(){
        // Simulate without the ship
        this.particlesToSimulate = 3;
        return this.simulateSystem();
    }

    public List<ImmutablePair<Double, double[][]>> simulateSpaceshipTraveling(){
        // Simulate with the ship
        this.particlesToSimulate = 4;
        return this.simulateSystem();
    }

    private List<ImmutablePair<Double, double[][]>> simulateSystem(){
        int index = -1;

        while (this.totalTime <= this.tf){
            // Checking if results can be stored
            index = this.checkAndStoreResults(index);

            // Running the Gear method
            this.runGearPredictorCorrectorMethod();

            // Updating the time
            this.totalTime += this.dt;
        }

        return this.results;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 RESULT STORING
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if given the total time and the delta, results can be stored
     * @param i current index of results
     * @return new current index of results
     */
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

    /////////////////////////////////////////////////////////////////////////////////////
    //                                FORCE CALCULATION
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Computes the force of each particle over the given particle
     * @param p particle to calculate the forces
     * @param isPredicted flag indicating if the type of force is predicted or not
     * @return a vector with the components of the total force
     */
    private Vector2D calculateForceOverParticle(Particle p, boolean isPredicted){
        Particle other;
        double fx = 0, fy = 0, f, r, ex, ey;
        for (int i = 0; i < this.particlesToSimulate; i++){
            // Check if the other particle is not my particle
            other = this.particles[i];
            if (other.getId() != p.getId()){
                // Determine the variables to use based on if the data is predicted or not
                if (isPredicted){
                    // Calculating the distance between the particles
                    r = p.predictedDistanceTo(other);
                    // Calculating the coefficients for the projection
                    ex = (other.getFutureX() - p.getFutureX()) / r;
                    ey = (other.getFutureY() - p.getFutureY()) / r;
                } else {
                    // Calculating the distance between the particles
                    r = p.distanceTo(other);
                    // Calculating the coefficients for the projection
                    ex = (other.getX() - p.getX()) / r;
                    ey = (other.getY() - p.getY()) / r;
                }
                // Module of the force
                f = (Constants.G * other.getMass() * p.getMass()) / (r * r);
                // Calculating the projections
                fx += (f * ex);
                fy += (f * ey);
            }
        }
        return new Vector2D(fx, fy);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                   GEAR METHOD
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Calculates the initial Gear Derivatives for a given particle
     * @param p particle to be calculated
     * @return matrix of values for each component
     */
    private double[][] calculateInitialGearDerivatives(Particle p){
        double[][] derivatives = new double[2][6];

        // Calculating the force
        Vector2D force = this.calculateForceOverParticle(p, false);

        // X Derivatives
        derivatives[X_VALUES][0] = p.getX();
        derivatives[X_VALUES][1] = p.getVx();
        derivatives[X_VALUES][2] = force.getX() / p.getMass();
        derivatives[X_VALUES][3] = 0;
        derivatives[X_VALUES][4] = 0;
        derivatives[X_VALUES][5] = 0;

        // Y Derivatives
        derivatives[Y_VALUES][0] = p.getY();
        derivatives[Y_VALUES][1] = p.getVy();
        derivatives[Y_VALUES][2] = force.getY() / p.getMass();
        derivatives[Y_VALUES][3] = 0;
        derivatives[Y_VALUES][4] = 0;
        derivatives[Y_VALUES][5] = 0;

        return derivatives;
    }

    /**
     * Initializes the gear factorials as well as the gear derivatives
     */
    private void initGearData(){
        // Initializing the factorials for the gear method
        this.gearDeltaFactorials = new double[]{1, this.dt, Math.pow(this.dt, 2)/2, Math.pow(this.dt, 3)/6, Math.pow(this.dt, 4)/24, Math.pow(this.dt, 5)/120};

        // Initializing the particle derivatives
        Particle p;
        double[][] derivatives;
        for (int i = 0; i < this.particlesToSimulate; i++) {
            p = this.particles[i];
            // Calculating derivatives
            derivatives = this.calculateInitialGearDerivatives(p);
            // Storing derivatives
            this.gearDerivatives.put(p.getId(), derivatives);
        }
    }

    /**
     * Runs the gear predictor corrector method over all the particles in the system
     */
    private void runGearPredictorCorrectorMethod(){
        Particle p;
        double[][] predictions, derivatives;
        double deltaAx, deltaAy, deltaR2x, deltaR2y;
        Vector2D force;

        // If the ship must blastoff, calculate new velocities
        if (!this.isInFlight && this.blastoffTime < this.totalTime) {
            this.updateShipForBlastoff();
        }

        // We predict the values for each of the particles
        for (int i = 1; i < this.particlesToSimulate; i++){
            predictions = new double[2][];
            p = this.particles[i];

            // Making the predictions and storing them
            predictions[X_VALUES] = this.makeGearPredictions(this.gearDerivatives.get(p.getId())[X_VALUES]);
            predictions[Y_VALUES] = this.makeGearPredictions(this.gearDerivatives.get(p.getId())[Y_VALUES]);

            this.gearPredictions.put(p.getId(), predictions);

            // Updating the particle with the predictions
            this.storePredictionsInParticles(p, predictions);
        }

        // We estimate the force for each particle
        for (int i = 1; i < this.particlesToSimulate; i++){
            p = this.particles[i];

            // Recovering the predictions and derivatives
            predictions = this.gearPredictions.get(p.getId());
            derivatives = this.gearDerivatives.get(p.getId());

            // Calculating the force
            force = this.calculateForceOverParticle(p, true);

            // Calculated acceleration - predicted acceleration
            deltaAx = (force.getX() / p.getMass()) - predictions[X_VALUES][2];
            deltaAy = (force.getY() / p.getMass()) - predictions[Y_VALUES][2];

            // DeltaA * dt^2 / 2!
            deltaR2x = deltaAx * this.gearDeltaFactorials[2];
            deltaR2y = deltaAy * this.gearDeltaFactorials[2];

            // Correcting the values
            // corrected_value_q = predicted_value_q + alpha_q * deltaR2 * q! / dt^q
            for (int j = 0; j < derivatives[X_VALUES].length; j++){
                derivatives[X_VALUES][j] = predictions[X_VALUES][j] + (this.gearAlphas[j] * deltaR2x * (1 / this.gearDeltaFactorials[j]));
                derivatives[Y_VALUES][j] = predictions[Y_VALUES][j] + (this.gearAlphas[j] * deltaR2y * (1 / this.gearDeltaFactorials[j]));
            }

            // Setting the values
            p.setX(derivatives[X_VALUES][0]);
            p.setVx(derivatives[X_VALUES][1]);
            p.setAx(derivatives[X_VALUES][2]);
            p.setY(derivatives[Y_VALUES][0]);
            p.setVy(derivatives[Y_VALUES][1]);
            p.setAy(derivatives[Y_VALUES][2]);

            // Storing the derivatives for next time
            this.gearDerivatives.put(p.getId(), derivatives);
        }
    }

    /**
     * Stores the predictions inside the given particle
     * @param p particle to be updated
     * @param predictions predictions to be stored
     */
    private void storePredictionsInParticles(Particle p, double[][] predictions){
        p.setFutureX(predictions[X_VALUES][0]);
        p.setFutureVx(predictions[X_VALUES][1]);
        p.setFutureAx(predictions[X_VALUES][2]);
        p.setFutureY(predictions[Y_VALUES][0]);
        p.setFutureVy(predictions[Y_VALUES][1]);
        p.setFutureAy(predictions[Y_VALUES][2]);
    }

    /**
     * Makes the gear predictions given the current derivatives
     * @param derivatives derivatives array to be used
     * @return array of doubles containing the predictions
     */
    private double[] makeGearPredictions(double[] derivatives){
        double[] predictions = new double[derivatives.length];
        double partialSum;

        for (int i = 0; i < predictions.length; i++){
            partialSum = 0;
            for (int j = 1; j + i < predictions.length; j++){
                partialSum += (derivatives[j + i] * this.gearDeltaFactorials[j]);
            }
            predictions[i] += (derivatives[i] + partialSum);
        }

        return predictions;
    }

    /**
     * Calculate the new velocity of the spaceship when blasting
     * off and update the particle
     */
    private void updateShipForBlastoff() {
        double[] values = updateShip(Constants.SHIP_INITIAL_VELOCITY);

        // Updating gear derivatives
        // This case only happens when the blastoff time is 0, because it initializes before the gear derivatives and can throw Null Pointer
        // FIXME: Ver si podemos modularizarlo mejor esto
//        if (this.gearDerivatives.containsKey(Constants.SHIP_INDEX)){
//            this.gearDerivatives.get(Constants.SHIP_INDEX)[X_VALUES][0] = values[0];
//            this.gearDerivatives.get(Constants.SHIP_INDEX)[Y_VALUES][0] = values[1];
//            this.gearDerivatives.get(Constants.SHIP_INDEX)[X_VALUES][1] = values[2];
//            this.gearDerivatives.get(Constants.SHIP_INDEX)[Y_VALUES][1] = values[3];
//        }

        double[][] derivatives = calculateInitialGearDerivatives(this.particles[Constants.SHIP_INDEX]);
        this.gearDerivatives.put(Constants.SHIP_INDEX, derivatives);

/*        System.out.println("----------------------\nDerivatives");
        System.out.println(Arrays.deepToString(this.gearDerivatives.get(Constants.SHIP_INDEX)));*/

        this.isInFlight = true;
    }

    private void stationaryShip() {
        updateShip(0.0);
    }

    private double[] updateShip(double initialVelocity) {
        Particle earth = this.particles[1];

        double earthDistanceToSun = Math.sqrt(Math.pow(earth.getX(), 2) + Math.pow(earth.getY(), 2));
        double shipDistanceToSun = earthDistanceToSun + Constants.STATION_ORBITAL_DISTANCE + earth.getRadius();

        double theta = Math.atan2(earth.getY(), earth.getX());

        double x = Math.cos(theta) * shipDistanceToSun;
        double y = Math.sin(theta) * shipDistanceToSun;
        double vx = (Math.signum(earth.getVx()) * Math.abs(Math.sin(theta) * (initialVelocity + Constants.STATION_ORBITAL_VELOCITY)) + earth.getVx());
        double vy = (Math.signum(earth.getVy()) * Math.abs(Math.cos(theta) * (initialVelocity + Constants.STATION_ORBITAL_VELOCITY)) + earth.getVy());

        this.particles[Constants.SHIP_INDEX].setX(x);
        this.particles[Constants.SHIP_INDEX].setY(y);
        this.particles[Constants.SHIP_INDEX].setVx(vx);
        this.particles[Constants.SHIP_INDEX].setVy(vy);

        return new double[]{x, y, vx, vy};
    }

}
