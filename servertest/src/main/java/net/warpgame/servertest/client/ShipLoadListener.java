package net.warpgame.servertest.client;

import net.warpgame.content.LoadShipEvent;
import net.warpgame.engine.core.component.Component;
import net.warpgame.engine.core.component.SceneComponent;
import net.warpgame.engine.core.event.Event;
import net.warpgame.engine.core.event.Listener;
import net.warpgame.engine.core.execution.EngineThread;
import net.warpgame.engine.core.property.TransformProperty;
import net.warpgame.engine.graphics.material.Material;
import net.warpgame.engine.graphics.material.MaterialProperty;
import net.warpgame.engine.graphics.mesh.MeshProperty;
import net.warpgame.engine.graphics.mesh.StaticMesh;
import net.warpgame.engine.graphics.rendering.screenspace.light.LightSource;
import net.warpgame.engine.graphics.rendering.screenspace.light.LightSourceProperty;
import net.warpgame.engine.graphics.rendering.screenspace.light.SceneLightManager;
import net.warpgame.engine.graphics.resource.mesh.ObjLoader;
import net.warpgame.engine.graphics.resource.texture.ImageData;
import net.warpgame.engine.graphics.resource.texture.ImageDecoder;
import net.warpgame.engine.graphics.resource.texture.PNGDecoder;
import net.warpgame.engine.graphics.texture.Texture2D;
import net.warpgame.engine.physics.simplified.SimplifiedPhysicsProperty;
import org.joml.Vector3f;
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
                true).toMesh();
        imageData = ImageDecoder.decodePNG(
                ClientTest.class.getResourceAsStream("he-goat_tex.png"),
                PNGDecoder.Format.RGBA
        );
        diffuse = new Texture2D(
                imageData.getHeight(),
                imageData.getHeight(),
                GL11.GL_RGBA16,
                GL11.GL_RGBA,
                true,
                imageData.getData());
        material = new Material(diffuse);
    }

    @Override
    public void handle(LoadShipEvent event) {
        Component ship = new SceneComponent(getOwner(), event.getShipComponentId());
        ship.addProperty(new TransformProperty().move(event.getPos()));
        ship.addProperty(new SimplifiedPhysicsProperty(10f));
        ship.addProperty(new MeshProperty(mesh));
        ship.addProperty(new MaterialProperty(material));
        ship.addListener(new BulletCreatedListener(ship, bulletCreator));

        Component light = new SceneComponent(ship, 1000000 + event.getShipComponentId());
        light.addProperty(new TransformProperty().move(0f, 1f, 0f));
        LightSource lightSource = new LightSource(new Vector3f(1.3f, 1.3f, 1.3f).mul(20));
        LightSourceProperty lightSourceProperty = new LightSourceProperty(lightSource);
        light.addProperty(lightSourceProperty);
        lightManager.addLight(lightSourceProperty);
    }
}