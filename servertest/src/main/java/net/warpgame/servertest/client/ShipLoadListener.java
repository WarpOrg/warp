package net.warpgame.servertest.client;

import net.warpgame.content.LoadShipEvent;
import net.warpgame.engine.core.component.Component;
import net.warpgame.engine.core.component.SceneComponent;
import net.warpgame.engine.core.event.Event;
import net.warpgame.engine.core.event.Listener;
import net.warpgame.engine.core.execution.EngineThread;
import net.warpgame.engine.core.property.TransformProperty;
import net.warpgame.engine.graphics.memory.scene.material.Material;
import net.warpgame.engine.graphics.memory.scene.material.MaterialProperty;
import net.warpgame.engine.graphics.memory.scene.mesh.MeshProperty;
import net.warpgame.engine.graphics.memory.scene.mesh.StaticMesh;
import net.warpgame.engine.graphics.rendering.screenspace.light.SceneLightManager;
import net.warpgame.engine.graphics.utility.resource.mesh.ObjLoader;
import net.warpgame.engine.graphics.utility.resource.texture.ImageData;
import net.warpgame.engine.graphics.utility.resource.texture.ImageDecoder;
import net.warpgame.engine.graphics.utility.resource.texture.PNGDecoder;
import net.warpgame.engine.graphics.image.Texture2D;
import net.warpgame.engine.physics.simplified.SimplifiedPhysicsProperty;
import org.lwjgl.opengl.GL11;

/**
 * @author Hubertus
 * Created 05.01.2018
 */
public class ShipLoadListener extends Listener<LoadShipEvent> {

    private final EngineThread graphicsThread;
    private StaticMesh mesh;
    private ImageData imageData;
    private Texture2D diffuse;
    Material material;
    private SceneLightManager lightManager;
    private final BulletCreator bulletCreator;

    protected ShipLoadListener(Component owner, EngineThread graphicsThread, SceneLightManager lightManager, BulletCreator bulletCreator) {
        super(owner, Event.getTypeId(LoadShipEvent.class));
        this.graphicsThread = graphicsThread;
        this.lightManager = lightManager;
        this.bulletCreator = bulletCreator;
        graphicsThread.scheduleOnce(this::init);
    }

    private void init() {
        mesh = ObjLoader.read(
                ClientTest.class.getResourceAsStream("he-goat.obj"),
                true).toModel();
        imageData = ImageDecoder.decodePNG(
                ClientTest.class.getResourceAsStream("he-goat_tex.png"),
                PNGDecoder.Format.RGBA
        );
        diffuse = new Texture2D(imageData);
        material = new Material(diffuse);
    }

    @Override
    public void handle(LoadShipEvent event) {
        Component ship = new SceneComponent(getOwner(), event.getShipComponentId());
        ship.addProperty(new TransformProperty().move(event.getPos()));
        ship.addProperty(new SimplifiedPhysicsProperty(10f));
        ship.addListener(new BulletCreatedListener(ship, bulletCreator));
        graphicsThread.scheduleOnce(() -> {

            ship.addProperty(new MeshProperty(mesh));
            ship.addProperty(new MaterialProperty(material));

        });

    }
}
