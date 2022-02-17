package ahd.usim.physics.core;

import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.utils.annotation.ChangeReference;
import org.jetbrains.annotations.NotNull;

public final class ForceUtils {
    public static double G = 6.674e-11;

    @SuppressWarnings("UnusedReturnValue")
    public static double gravitationalForce(@NotNull Particle p1, @NotNull Particle p2) {
        var p1p = p1.position;
        var p2p = p2.position;
        var sd = p1p.squareOfDistanceFrom(p2p);
        if (p1.mass == 0 || p2.mass == 0 || sd == 0) {
            p1.forces.get(p2.id + ">" + p1.id + "-gravity").set(p2.forces.get(p1.id + ">" + p2.id + "-gravity").set(0, 0, 0));
            return 0;
        }
        var ans = G * p1.mass * p2.mass / sd;
        p2.forces.
                get(p1.id + ">" + p2.id + "-gravity").
                set(p1.forces.get(p2.id + ">" + p1.id + "-gravity").
                        set(p2p.x - p1p.x, p2p.y - p1p.y, p2p.z - p1p.z).
                        normalize().scale(ans)).scale(-1);
        return ans;
    }
}
