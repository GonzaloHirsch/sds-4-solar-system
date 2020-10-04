public class OscilatorForce implements Force {
    private final double gamma;
    private final double k;
    private double fx;

    OscilatorForce(double gamma, double k) {
        this.gamma = gamma;
        this.k = k;
    }

    @Override
    public void evaluate(double x, double y, double vx, double vy) {
        this.fx = -(this.k * x) - (this.gamma * vx);
    }

    @Override
    public void evaluate(double x, double vx) {
        this.fx = -(this.k * x) - (this.gamma * vx);
    }

    public double getFx() {
        return fx;
    }

    public double getFy() {
        return 0.0;
    }

}
