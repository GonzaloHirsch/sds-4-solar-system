public class OscilatorForce implements Force {
    private double gamma;
    private double k;
    private double fx;
    private double fy;

    OscilatorForce(double gamma, double k) {
        this.gamma = gamma;
        this.k = k;
    }

    @Override
    public void evaluate(double x, double y, double vx, double vy) {
        this.fx = -(this.k * x) - (this.gamma * vx);
        this.fy = 0;
    }

    public double getFx() {
        return fx;
    }

    public double getFy() {
        return fy;
    }

}
