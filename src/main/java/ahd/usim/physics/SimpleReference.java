package ahd.usim.physics;

import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.usim.physics.core.AbstractReference;
import ahd.usim.physics.core.ForceUtils;
import ahd.usim.physics.core.Particle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SimpleReference extends AbstractReference {


    @Override
    public void particleAdded(Particle p) {
        particles.values().forEach(particle -> {
            particle.forces.put(p.id + ">" + particle.id + "-gravity", new Point3D(Point3D.NaN));
            p.forces.put(particle.id + ">" + p.id + "-gravity", new Point3D(Point3D.NaN));
        });
//        p.forces.put("test", new Point3D(100000, 100000, 100000));
    }

    @Override
    public void calculateForces() {
        var list = new ArrayList<>(particles.values());
        var len = list.size();
        for (int i = 0; i < len - 1; i++)
            for (int j = i + 1; j < len; j++)
                ForceUtils.gravitationalForce(list.get(i), list.get(j));
//        list.forEach(p -> p.forces.get("test").rotate(0.5, 0.5, 0));
    }
}
