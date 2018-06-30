package net.warpgame.engine.client;

import io.netty.buffer.ByteBuf;
import net.warpgame.engine.core.property.TransformProperty;
import net.warpgame.engine.core.component.Component;
import net.warpgame.engine.core.component.ComponentRegistry;
import net.warpgame.engine.core.context.service.Service;
import net.warpgame.engine.core.context.task.RegisterTask;
import net.warpgame.engine.core.execution.task.EngineTask;
import net.warpgame.engine.core.property.Property;
import net.warpgame.engine.net.SerializationType;
import net.warpgame.engine.physics.simplified.SimplifiedPhysicsProperty;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author Hubertus
 * Created 14.12.2017
 */
@Service
@RegisterTask(thread = "client")
public class SceneUpdaterTask extends EngineTask {

    //(int) componentId, (int) serializationType
    private static final int COMPONENT_HEADER_SIZE = 2 * 4;

    private SerializedSceneHolder sceneHolder;
    private ComponentRegistry componentRegistry;
    private UpdateBlockerService blockerService;

    public SceneUpdaterTask(SerializedSceneHolder sceneHolder,
                            ComponentRegistry componentRegistry,
                            UpdateBlockerService blockerService) {
        this.sceneHolder = sceneHolder;
        this.componentRegistry = componentRegistry;
        this.blockerService = blockerService;
    }


    @Override
    protected void onInit() {

    }

    @Override
    protected void onClose() {

    }

    @Override
    public void update(int delta) {
        if (sceneHolder.isSceneAvailable())
            updateScene(sceneHolder.getScene());
    }

    private Vector3f translation = new Vector3f();
    private Quaternionf rotation = new Quaternionf();
    private Vector3f velocity = new Vector3f();
    private SerializationType[] serializationTypes = SerializationType.values();

    private void updateScene(ByteBuf serializedScene) {
        while (serializedScene.isReadable()) {
            int componentId = serializedScene.readInt();
            Component c = componentRegistry.getComponent(componentId);
            SerializationType serializationType = serializationTypes[serializedScene.readInt()];

            if (c != null && !blockerService.isBlocked(c.getId())) deserialize(c, serializationType, serializedScene);
            else skip(serializationType, serializedScene);
        }
    }

    private void deserialize(Component c, SerializationType serializationType, ByteBuf serializedScene) {
        switch (serializationType) {
            case POSITION_AND_VELOCITY:
                deserializePositionAndVelocity(c, serializedScene);
                break;
            case POSITION:
                deserializePosition(c, serializedScene);
                break;
        }

    }

    private void skip(SerializationType serializationType, ByteBuf serializedScene) {
        switch (serializationType) {
            case POSITION:
                serializedScene.readerIndex(
                        serializedScene.readerIndex() + SerializationType.Size.POSITION_SIZE - COMPONENT_HEADER_SIZE);
                break;
            case POSITION_AND_VELOCITY:
                serializedScene.readerIndex(
                        serializedScene.readerIndex() + SerializationType.Size.POSITION_AND_VELOCITY_SIZE - COMPONENT_HEADER_SIZE);
                break;
        }
    }

    private void deserializePosition(Component c, ByteBuf data) {
        if (c.hasEnabledProperty(Property.getTypeId(TransformProperty.class))) {
            TransformProperty transformProperty = c.getProperty(Property.getTypeId(TransformProperty.class));
            translation.set(
                    data.readFloat(),
                    data.readFloat(),
                    data.readFloat());
            rotation.set(
                    data.readFloat(),
                    data.readFloat(),
                    data.readFloat(),
                    data.readFloat());
            transformProperty.setTranslation(translation);
            transformProperty.setRotation(rotation);
        }
    }

    private void deserializePositionAndVelocity(Component c, ByteBuf data) {
        deserializePosition(c, data);
        if (c.hasEnabledProperty(Property.getTypeId(SimplifiedPhysicsProperty.class))) {
            SimplifiedPhysicsProperty physicsProperty = c.getProperty(Property.getTypeId(SimplifiedPhysicsProperty.class));
            velocity.set(
                    data.readFloat(),
                    data.readFloat(),
                    data.readFloat()
            );
            physicsProperty.setVelocity(velocity);
        }
    }
}
