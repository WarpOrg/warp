package pl.warp.engine.physics.property;

import pl.warp.engine.core.scene.Component;
import pl.warp.engine.core.scene.Property;

/**
 * @author Hubertus
 *         Created 12.08.16
 */
public abstract class PhysicalProperty extends Property<Component>{

    public PhysicalProperty(Component owner, String name) {
        super(owner, name);
    }
}
