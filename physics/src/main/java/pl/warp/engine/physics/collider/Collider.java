package pl.warp.engine.physics.collider;

import com.badlogic.gdx.math.Quaternion;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pl.warp.engine.physics.PhysicsWorld;

/**
 * Created by hubertus on 7/9/16.
 */
public interface Collider {
    void addToWorld(PhysicsWorld world);

    void removeFromWorld(PhysicsWorld world);

    void setTransform(Vector3f translation, Quaternionf rotation);

    void addTransform(Vector3f translation, Quaternion rotation);

    void dispose();

    void setDefaultCollisionHandling(boolean value);

    boolean getDefaultCollisionHandling();
}
