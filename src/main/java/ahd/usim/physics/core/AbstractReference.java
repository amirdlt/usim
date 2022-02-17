package ahd.usim.physics.core;

import ahd.ulib.jmath.datatypes.tuples.Point3D;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractReference {
    private static int referenceCounter = 0;

    protected final Map<String, Particle> particles;
    private final String id;

    protected double dt;
    protected double currentTime;

    public AbstractReference() {
        particles = new HashMap<>();
        id = "r" + referenceCounter++;
        dt = 1e-3;
        currentTime = 0;
    }

    public void addParticle(Point3D position, Point3D velocity, double mass) {
        var id = this.id + ".p" + particles.size();
        var p = new Particle(id, position, velocity, this);
        p.setMass(mass);
        particleAdded(p);
        particles.put(id, p);
    }

    public abstract void particleAdded(Particle p);

    public abstract void calculateForces();

    private void updateVelocities() {
        updateAccelerations();
        particles.values().forEach(p -> {
            var a = p.acceleration;
            p.velocity.addVector(a.x * dt, a.y * dt, a.z * dt);
        });
    }

    private void updateAccelerations() {
        calculateForces();
        particles.values().stream().filter(p -> p.getMass() != 0).forEach(p -> {
            var tf = p.calculateTotalForce();
            p.acceleration.set(tf.x / p.mass, tf.y / p.mass, tf.z / p.mass);
        });
    }

    public Map<String, Particle> getParticles() {
        return particles;
    }

    public void updatePositions() {
        updateVelocities();
        particles.values().forEach(p -> {
            var v = p.velocity;
            p.position.addVector(v.x * dt, v.y * dt, v.z * dt);
        });
        currentTime += dt;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public double getCurrentTime() {
        return currentTime;
    }
}
