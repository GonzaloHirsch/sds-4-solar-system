public class Integrator {

    private final double deltaTime;

    // Gear predictor coefficients
    private final double[] gearAlphas = new double[]{3.0/16, 251.0/360, 1, 11.0/18, 1.0/6, 1.0/60};

    // Gear predictor delta powers and factorials for reduced computation
    private final double[] gearDeltaFactorials;

    // Gear predictor derivatives
    private final double[] gearDerivatives;

    public Integrator(Particle p, double deltaTime, double k, double gamma){
        this.deltaTime = deltaTime;
        this.gearDeltaFactorials = new double[]{1, deltaTime, Math.pow(deltaTime, 2)/2, Math.pow(deltaTime, 3)/6, Math.pow(deltaTime, 4)/24, Math.pow(deltaTime, 5)/120};
        this.gearDerivatives = this.initGearDerivatives(k, gamma, p);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                  ANALYTICAL
    /////////////////////////////////////////////////////////////////////////////////////

    public double analyticalSolution(double time, double gamma, double k, double mass) {
        double exponential = Math.exp(-(gamma / (2*mass)) * time);
        double cosine = Math.cos(Math.pow(((k/mass) - (gamma*gamma)/(4*(mass*mass))), 0.5) * time);
        return exponential * cosine;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                  BEEMAN
    /////////////////////////////////////////////////////////////////////////////////////

    public void beeman(Particle p, Force f, double dt) {
        /* Calculating force components */
        f.evaluate(p.getX(), p.getY(), p.getVx(), p.getVy());

        /* Calculating future accelerations */
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
        return v + ((1.0/3.0)*aFuture + (5.0/6.0)*a - (1.0/6.0)*aPrev) * dt;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                              EULER PREDICTOR CORRECTOR
    /////////////////////////////////////////////////////////////////////////////////////

    public double[] eulerPrediction(Particle p, Force f, double dt) {
        double predictedX = p.getX() + p.getVx() * dt;
        double predictedY = p.getY() + p.getVy() * dt;

        double predictedVx = p.getVx() + p.getAx() * dt;
        double predictedVy = p.getVy() + p.getAy() * dt;

        f.evaluate(predictedX, predictedY, predictedVx, predictedVy);

        double[] calculations = new double[6];

        calculations[0] = f.getFx() / p.getMass();          // Ax(t+dt)
        calculations[1] = f.getFy() / p.getMass();          // Ay(t+dt)
        calculations[2] = p.getVx() + calculations[0] * dt; // Vx(t+dt)
        calculations[3] = p.getVy() + calculations[1] * dt; // Vy(t+dt)
        calculations[4] = p.getX()  + calculations[2] * dt; // X(t+dt)
        calculations[5] = p.getY()  + calculations[3] * dt; // Y(t+dt)

        return calculations;        
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                              EULER MODIFICADO
    /////////////////////////////////////////////////////////////////////////////////////

    public double[] eulerModified(Particle p, Force f, double dt) {
        f.evaluate(p.getX(), p.getY(), p.getVx(), p.getVy());

        double predictedVx = p.getVx() + (dt/p.getMass()) * f.getFx();
        double predictedVy = p.getVy() + (dt/p.getMass()) * f.getFy();

        double predictedX = p.getX() + (dt * predictedVx) + ((dt * dt) / (2 * p.getMass())) * f.getFx();
        double predictedY = p.getY() + (dt * predictedVy) + ((dt * dt) / (2 * p.getMass())) * f.getFy();

        f.evaluate(predictedX, predictedY, predictedVx, predictedVy);

        double[] calculations = new double[6];

        calculations[0] = f.getFx() / p.getMass(); // Ax(t+dt)
        calculations[1] = f.getFy() / p.getMass(); // Ay(t+dt)
        calculations[2] = predictedVx;             // Vx(t+dt)
        calculations[3] = predictedVy;             // Vy(t+dt)
        calculations[4] = predictedX;              // X(t+dt)
        calculations[5] = predictedY;              // Y(t+dt)

        return calculations;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                              GEAR PREDICTOR CORRECTOR
    /////////////////////////////////////////////////////////////////////////////////////

    private double[] initGearDerivatives(double k, double gamma, Particle p){
        double[] derivatives = new double[6];
        double km = -(k / p.getMass());
        double gammam = -(gamma / p.getMass());

        derivatives[0] = p.getX();  // Position
        derivatives[1] = p.getVx(); // Velocity
        derivatives[2] = (km * derivatives[0]) + (gammam * derivatives[1]);    // Acceleration
//        derivatives[3] = (km * derivatives[1]) + (gammam * derivatives[2]);
//        derivatives[4] = (km * derivatives[2]) + (gammam * derivatives[3]);
//        derivatives[5] = (km * derivatives[3]) + (gammam * derivatives[4]);
        derivatives[3] = 0;
        derivatives[4] = 0;
        derivatives[5] = 0;
        return derivatives;
    }

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

    public void gearPredictorCorrector(Particle p, Force f){
        // Making the predictions
        double[] predictions = this.makeGearPredictions(this.gearDerivatives);

        // FIXME: evaluar bien la fuerza
        f.evaluate(predictions[0], predictions[1]);

        // Calculated acceleration - predicted acceleration
        double deltaA = (f.getFx() / p.getMass()) - predictions[2];

        // DeltaA * dt^2 / 2!
        double deltaR2 = deltaA * this.gearDeltaFactorials[2];

        // Correcting the values
        // corrected_value_q = predicted_value_q + alpha_q * deltaR2 * q! / dt^q
        for (int i = 0; i < this.gearDerivatives.length; i++){
            this.gearDerivatives[i] = predictions[i] + (this.gearAlphas[i] * deltaR2 * (1 / this.gearDeltaFactorials[i]));
        }

        // Setting the values
        p.setX(this.gearDerivatives[0]);
        p.setVx(this.gearDerivatives[1]);
        p.setAx(this.gearDerivatives[2]);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                    VERLET
    /////////////////////////////////////////////////////////////////////////////////////

    public void verlet(Particle p, Force f, double k){
        // Calculating the force in t
        f.evaluate(p.getX(), p.getY(), p.getVx(), p.getVy());

        // Calculating the next x position
        double nextX = p.getX() + this.deltaTime * p.getVx() + ((Math.pow(this.deltaTime, 2) / p.getMass()) * f.getFx());

        // Mid step for velocity
        double midNextVx = p.getVx() + (f.getFx() / p.getMass()) * (this.deltaTime / 2);

        // FIXME: VERIFICAR ESTA FORMULA
        // Next step for acceleration
        double nextAx = (- k / p.getMass()) * nextX;

        // Next step for velocity
        double nextVx = midNextVx + (nextAx * (this.deltaTime / 2));

        // Setting the next variables
        p.setX(nextX);
        p.setVx(nextVx);
        p.setAx(nextAx);
    }

}
