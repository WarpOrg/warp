package net.warpgame.servertest.server;

import net.warpgame.content.BoardShipEvent;
import net.warpgame.content.LoadShipEvent;
import net.warpgame.engine.core.component.Component;
import net.warpgame.engine.core.event.Event;
import net.warpgame.engine.core.event.Listener;
import net.warpgame.engine.core.property.Property;
import net.warpgame.engine.core.property.TransformProperty;
import net.warpgame.engine.net.NetComponentRegistry;
import net.warpgame.engine.net.SerializationType;
import net.warpgame.engine.net.StateSynchronizerProperty;
import net.warpgame.engine.net.messagetypes.event.ConnectedEvent;
import net.warpgame.engine.physics.FullPhysicsProperty;
import net.warpgame.engine.physics.PhysicsService;
import net.warpgame.engine.physics.RigidBodyConstructor;
import net.warpgame.engine.physics.shapeconstructors.RigidBodyBoxShapeConstructor;
import net.warpgame.engine.server.Client;
import net.warpgame.engine.server.ClientRegistry;
import net.warpgame.servertest.RemoteInputProperty;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Hubertus
 * Created 05.01.2018
 */
public class ConnectedListener extends Listener<ConnectedEvent> {

    private final NetComponentRegistry componentRegistry;
    private final PhysicsService physicsService;
    private final ClientRegistry clientRegistry;
    private final NetComponentRegistry netComponentRegistry;
    private Component scene;

    ConnectedListener(Component owner,
                      NetComponentRegistry componentRegistry,
                      PhysicsService physicsService,
                      ClientRegistry clientRegistry,
                      NetComponentRegistry netComponentRegistry) {
        super(owner, Event.getTypeId(ConnectedEvent.class));

        this.componentRegistry = componentRegistry;
        this.physicsService = physicsService;
        this.clientRegistry = clientRegistry;
        this.netComponentRegistry = netComponentRegistry;
    }

    @Override
    public void handle(ConnectedEvent event) {
        if (event.getSourcePeerId() == 0)
            return;
        System.out.println("client connected");
        Component ship = netComponentRegistry.createPublicComponent(getOwner());
        TransformProperty transformProperty = new TransformProperty();
        ship.addProperty(transformProperty);
        ship.addProperty(new RemoteInputProperty());
        ship.addListener(new ClientInputListener(ship));
        RigidBodyBoxShapeConstructor shapeConstructor = new RigidBodyBoxShapeConstructor(new Vector3f(2, 2, 2));
        RigidBodyConstructor constructor = new RigidBodyConstructor(shapeConstructor, 10f);
        FullPhysicsProperty physicsProperty = new FullPhysicsProperty(constructor.construct(transformProperty));

        ship.addProperty(physicsProperty);
        ship.addProperty(new StateSynchronizerProperty(SerializationType.POSITION_AND_VELOCITY));
        ship.addScript(MovementScript.class);


        RigidBodyBoxShapeConstructor bulletShapeConstructor = new RigidBodyBoxShapeConstructor(new Vector3f(0.1f, 0.1f, 0.1f));
        RigidBodyConstructor bulletRigidBodyConstuctor = new RigidBodyConstructor(bulletShapeConstructor, 0.1f);
        ship.addListener(new GunListener(ship, componentRegistry, bulletRigidBodyConstuctor));

        Client client = clientRegistry.getClient(event.getSourcePeerId());

        if (client != null) {
            getOwner().triggerEvent(new LoadShipEvent(ship.getId(), (Vector3f) transformProperty.getTranslation(), Client.ALL));//TODO change to Vector3fc
            sendScene(client, ship.getId());
            getOwner().triggerEvent(new BoardShipEvent(ship.getId(), client.getId()));
        }
    }

    private void sendScene(Client client, int currentShip) {
        ArrayList<Component> components = new ArrayList<>();
        componentRegistry.getComponents(components);
        TransformProperty property;
        for (Component c : components) {
            if (c.getId() != 0 && c.getId() != currentShip) {
                property = c.getProperty(Property.getTypeId(TransformProperty.class));
                getOwner().triggerEvent(new LoadShipEvent(c.getId(), (Vector3f) property.getTranslation(), client.getId()));//TODO change to Vector3fc
            }
        }
    }

}
