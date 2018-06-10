package net.warpgame.engine.graphics.rendering.scene.mesh;

import net.warpgame.engine.core.property.Property;

/**
 * @author Jaca777
 *         Created 2016-06-28 at 11
 */
public class MeshProperty extends Property {

    private SceneMesh mesh;

    public MeshProperty(SceneMesh mesh) {
        this.mesh = mesh;
    }

    public SceneMesh getMesh() {
        return mesh;
    }
}
