package ahd.usim.ulib.visualization.model;


import ahd.usim.ulib.visualization.canvas.Render;

import java.util.List;

public interface Model<T> extends Render {
    List<T> getVertexes();


}
