package ahd.ulib.visualization.model;


import ahd.ulib.visualization.canvas.Render;

import java.util.List;

public interface Model<T> extends Render {
    List<T> getVertexes();


}
