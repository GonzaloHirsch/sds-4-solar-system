public class Particle implements Comparable<Particle> {
    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        PROPERTIES
    //////////////////////////////////////////////////////////////////////////////////////////

    /* Convention, ID starts at 0 */
    private int id;
    private double radius;
    private double mass;

    /* Positions */
    private double x;
    private double y;
    private double prevX;
    private double prevY;
    private Double futureX;
    private Double futureY;

    /* Velocities */
    private double vx;
    private double vy;
    private double prevVx;
    private double prevVy;
    private Double futureVx;
    private Double futureVy;

    /* Acceleration */
    private double ax;
    private double ay;
    private double prevAx;
    private double prevAy;
    private Double futureAx;
    private Double futureAy;

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////////

    public Particle(int id, double x, double y, double vx, double vy, double radius, double mass) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.mass = mass;
    }

    public Particle(int id, double radius, double mass) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        GETTERS
    //////////////////////////////////////////////////////////////////////////////////////////

    public int getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getFutureX() {
        return futureX;
    }

    public double getFutureY() {
        return futureY;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getPrevVx() {
        return prevVx;
    }

    public double getPrevVy() {
        return prevVy;
    }

    public double getFutureVx() {
        return futureVx;
    }

    public double getFutureVy() {
        return futureVy;
    }

    public double getAx() {
        return ax;
    }

    public double getAy() {
        return ay;
    }

    public double getPrevAx() {
        return prevAx;
    }

    public double getPrevAy() {
        return prevAy;
    }

    public double getFutureAx() {
        return futureAx;
    }

    public double getFutureAy() {
        return futureAy;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        SETTERS
    //////////////////////////////////////////////////////////////////////////////////////////

    public void setId(int id) {
        this.id = id;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setFutureX(double x) {
        this.futureX = x;
    }

    public void setFutureY(double y) {
        this.futureY = y;
    }

    public void setFutureVx(double vx) {
        this.futureVx = vx;
    }

    public void setFutureVy(double vy) {
        this.futureVx = vy;
    }

    public void setFutureAx(double ax) {
        this.futureAx = ax;
    }

    public void setFutureAy(double ay) {
        this.futureAy = ay;
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return this.id == particle.getId();
    }

    @Override
    public String toString() {
        return String.format("[Particle #%d] {x = %f, y = %f, radius = %f, mass = %f}\n",
                this.id,
                this.x,
                this.y,
                this.radius,
                this.mass
        );
    }

    public int compareTo(Particle particle) {
        return Integer.compare(id, particle.getId());
    }

    public void update() {
        /* Updating positions */
        this.prevX   = this.x;
        this.prevY   = this.y;
        this.x       = this.futureX;
        this.y       = this.futureY;
        this.futureX = null;
        this.futureY = null;

        /* Updating velocities */
        this.prevVx   = this.vx;
        this.prevVy   = this.vy;
        this.vx       = this.futureVx;
        this.vy       = this.futureVy;
        this.futureVx = null;
        this.futureVy = null;

        /* Updating accelerations */
        this.prevAx   = this.ax;
        this.prevAy   = this.ay;
        this.ax       = this.futureAx;
        this.ay       = this.futureAy;
        this.futureAx = null;
        this.futureAy = null;
    }
}
