package ahd.usim.physics.core;

import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.utils.annotation.ChangeReference;

import java.util.HashMap;
import java.util.Map;

public class Particle {
    public final String id;

    public final Point3D position;
    public final Point3D velocity;
    public final Point3D acceleration;

    public final Point3D totalForce;

    protected double mass;
    protected AbstractReference reference;

    public final Map<String, Point3D> forces;

    Particle(String id, Point3D position, Point3D velocity, AbstractReference reference) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Point3D(Point3D.NaN);
        this.reference = reference;
        this.forces = new HashMap<>();
        this.totalForce = new Point3D(Point3D.NaN);
        this.id = id;
    }

    Particle(String id, AbstractReference reference) {
        this(id, new Point3D(), new Point3D(), reference);
    }

    public Point3D calculateTotalForce() {
        totalForce.set(0, 0, 0);
        forces.values().forEach(totalForce::add);
        return totalForce;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public AbstractReference getReference() {
        return reference;
    }
}
