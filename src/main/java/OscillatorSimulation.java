public class OscillatorSimulation {

    private static final double MASS  = 70;             // kg
    private static final double K = Math.pow(10, 4);    // N/m
    private static final double GAMMA = 100;            // kg/s
    private static final double X0 = 1.0;               // m
    private static final double V0 = -GAMMA / (2*MASS); // m/s
    private final double tf;
    private final double dt;
    private final int tm;
    private final double[][] results;
    private final Integrator integrator;
    private static final Particle PARTICLE = new Particle(0, 10, MASS);
    private static final OscilatorForce OFORCE = new OscilatorForce(GAMMA, K);

    private double totalTime = 0;

    public OscillatorSimulation(Particle particle, double tf, double dt, int tm) {
        this.tf = tf;
        this.dt = dt;
        this.tm = tm;

        // Setting the starting values for the particle
        OFORCE.evaluate(X0, V0);
        PARTICLE.setAx(OFORCE.getFx() / MASS);
        PARTICLE.setVx(V0);
        PARTICLE.setX(X0);

        // Creating an instance of the integrator
        this.integrator = new Integrator(PARTICLE, this.dt, K);

        // Using Euler to find the values at (t - dt)
        double[] prevValues = this.integrator.eulerPrediction(PARTICLE, OFORCE, (-1) * dt);
        PARTICLE.setPrevValues(prevValues);

        // Creating the structure for results, we store every tm*dt results
        int rows = (int) Math.floor(this.tf/(this.tm * this.dt));
        this.results = new double[rows][5];
    }

    public double[][] runAnalytical(){
        double position;
        int index = 0;

        // In this case we can skip the dt we don't want
        while (this.totalTime <= this.tf){
            position = this.integrator.analyticalSolution(this.totalTime, GAMMA, K, MASS);
            this.results[index++][1] = position;
            this.totalTime += (this.tm * this.dt);
        }
        return this.results;
    }

    public double[][] runBeeman(){
        while (totalTime <= this.tf){

            totalTime += this.dt;
        }
        return this.results;
    }

    public double[][] runVerlet(){
        while (totalTime <= this.tf){

            totalTime += this.dt;
        }
        return this.results;
    }

    public double[][] runGearPredictorCorrector(){
        while (totalTime <= this.tf){

            totalTime += this.dt;
        }
        return this.results;
    }
}
