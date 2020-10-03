public class Integrator {

    private double mass  = 70; // kg
    private double k     = Math.pow(10, 4); // N/m
    private double gamma = 100; // kg/s
    private double tf    = 5; // s

    public double analyticalSolution(double time) {
        double exponential = Math.exp(-(gamma / (2*mass)) * time);
        double cosine = Math.cos(Math.pow((k/mass - (gamma*gamma)/(4*(mass*mass))), 0.5) * time);
        return exponential * cosine;
    }

    public void beeman(Particle p, double dt) {

        /* Calculating future accelerations TODO -> algo con la fuerza */
        double predictedAx = 0.0;
        double predictedAy = 0.0;

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
}
