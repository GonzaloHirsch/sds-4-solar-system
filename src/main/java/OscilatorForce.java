public class OscilatorForce implements Force {
    private Particle particle;
    private double gamma;
    private double k;
    private double fx;
    private double fy;

    OscilatorForce(Particle p, double gamma, double k) {
        this.particle = p;
        this.gamma = gamma;
        this.k = k;
    }

    @Override
    public void evaluate() {
        this.fx = -(this.k * this.particle.getX()) - (this.gamma * this.particle.getVx());
        this.fy = 0;
    }

    public double getFx() {
        return fx;
    }

    public double getFy() {
        return fy;
    }
}
