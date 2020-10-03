public class Integrator {

    private double deltaTime;

    // Gear predictor coefficients
    private final double[] gearAlphas = new double[]{3.0/16, 251.0/360, 1, 11.0/18, 1.0/6, 1.0/60};

    // Gear predictor delta powers and factorials for reduced computation
    private final double[] gearDeltaFactorials;

    // Gear predictor derivatives
    private double gearDerivatives[];

    public Integrator(Particle p, double deltaTime, double k){
        this.deltaTime = deltaTime;
        this.gearDeltaFactorials = new double[]{1, deltaTime, Math.pow(deltaTime, 2)/2, Math.pow(deltaTime, 3)/6, Math.pow(deltaTime, 4)/24, Math.pow(deltaTime, 5)/120};
        this.gearDerivatives = this.initGearDerivatives(k, p);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                  ANALYTICAL
    /////////////////////////////////////////////////////////////////////////////////////

    public double analyticalSolution(double time, double gamma, double k, double mass) {
        double exponential = Math.exp(-(gamma / (2*mass)) * time);
        double cosine = Math.cos(Math.pow((k/mass - (gamma*gamma)/(4*(mass*mass))), 0.5) * time);
        return exponential * cosine;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                  BEEMAN
    /////////////////////////////////////////////////////////////////////////////////////

    public void beeman(Particle p, Force f, double dt) {
        /* Calculating force components */
        f.evaluate();

        /* Calculating future accelerations TODO -> algo con la fuerza */
        double predictedAx = f.getFx() / p.getMass();
        double predictedAy = f.getFy() / p.getMass();

        /* Predicting positions */
        double predictedX = this.beemanPositionPrediction(p.getX(), p.getVx(), p.getAx(), p.getPrevAx(), dt);
        double predictedY = this.beemanPositionPrediction(p.getY(), p.getVy(), p.getAy(), p.getPrevAy(), dt);

        /* Predicting velocities */
        double predictedVx = this.beemanVelocityPrediction(p.getVx(), p.getAx(), p.getPrevAx(), predictedAx, dt);
        double predictedVy = this.beemanVelocityPrediction(p.getVy(), p.getAy(), p.getPrevAy(), predictedAy, dt);

        /* Set particles future predictions */
        p.setFutureX(predictedX);
        p.setFutureY(predictedY);
        p.setFutureVx(predictedVx);
        p.setFutureVy(predictedVy);
        p.setFutureAx(predictedAx);
        p.setFutureAy(predictedAy);
    }

    private double beemanPositionPrediction(double r, double v, double a, double aPrev, double dt) {
        return r + v*dt + (2.0/3.0)*a*dt*dt + (1.0/6.0)*aPrev*dt*dt;
    }

    private double beemanVelocityPrediction(double v, double a, double aPrev, double aFuture, double dt) {
        return v + ((1.0/3.0)*aFuture + (5.0/6.0)*a + (1.0/6.0)*aPrev) * dt;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                              GEAR PREDICTOR CORRECTOR
    /////////////////////////////////////////////////////////////////////////////////////

    private double[] initGearDerivatives(double k, Particle p){
        double[] derivatives = new double[6];
        derivatives[0] = p.getX();  // Position
        derivatives[1] = p.getVx(); // Velocity
        derivatives[2] = (- k / p.getMass()) * p.getX();    // Acceleration
        // FIXME: ver si hay que poner 0 o como dice el PPT en pag 29
        derivatives[3] = 0;
        derivatives[4] = 0;
        derivatives[5] = 0;
        return derivatives;
    }

    private double[] makeGearPredictions(double[] derivatives){
        // Cloning derivatives to make the predictions
        double[] predictions = derivatives.clone();

        for (int i = 0, partialSum = 0; i < predictions.length; i++){
            for (int j = 1; j + i < predictions.length; j++){
                partialSum += (derivatives[j + i] * this.gearDeltaFactorials[j]);
            }
            predictions[i] += partialSum;
        }

        return predictions;
    }

    public void gearPredictorCorrector(Particle p, Force f, double dt){
        // Making the predictions
        double[] predictions = this.makeGearPredictions(this.gearDerivatives);

        // FIXME: evaluar bien la fuerza
        f.evaluate();

        // Calculated acceleration - predicted acceleration
        double deltaA = (f.getFx() / p.getMass()) - predictions[2];

        // DeltaA * dt^2 / 2!
        double deltaR2 = deltaA * this.gearDeltaFactorials[2];

        // Correcting the values
        // corrected_value_q = predicted_value_q + alpha_q * deltaR2 * q! / dt^q
        for (int i = 0; i < this.gearDerivatives.length; i++){
            this.gearDerivatives[i] = predictions[i] + this.gearAlphas[i] * deltaR2 * (1 / this.gearDeltaFactorials[i]);
        }
    }
}
