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

    public void beeman(Particle particle, double dt) {
        
    }
}
