package com.usim.ulib.visualization.model;


import com.usim.ulib.visualization.canvas.Render;

import java.util.List;

public interface Model<T> extends Render {
    List<T> getVertexes();


}
