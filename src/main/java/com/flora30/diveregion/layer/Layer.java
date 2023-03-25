package com.flora30.diveregion.layer;

import org.bukkit.Location;

public class Layer extends com.flora30.diveapi.data.Layer {

    public Layer(LayerArea area) {
        super(area);
    }

    public LayerArea getLayerArea() {
        return layerArea;
    }

}
