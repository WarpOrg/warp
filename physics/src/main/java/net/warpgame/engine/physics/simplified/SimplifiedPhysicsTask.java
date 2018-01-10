package net.warpgame.engine.physics.simplified;

import net.warpgame.engine.common.physics.SimplePhysicsProperty;
import net.warpgame.engine.common.transform.TransformProperty;
import net.warpgame.engine.core.component.Component;
import net.warpgame.engine.core.component.ComponentRegistry;
import net.warpgame.engine.core.context.service.Profile;
import net.warpgame.engine.core.context.service.Service;
import net.warpgame.engine.core.context.task.RegisterTask;
import net.warpgame.engine.core.execution.task.EngineTask;

import java.util.ArrayList;

/**
 * @author Hubertus
 * Created 07.01.2018
 */
@Service
@RegisterTask(thread = "physics")
@Profile("simplePhysics")
public class SimplifiedPhysicsTask extends EngineTask{

    private ComponentRegistry componentRegistry;

    public SimplifiedPhysicsTask(ComponentRegistry componentRegistry){

        this.componentRegistry = componentRegistry;
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onClose() {

    }

    @Override
    public void update(int delta) {
        updateComponents();
    }

    private ArrayList<Component> components = new ArrayList<>();
    private void updateComponents(){
        components.clear();
        componentRegistry.getComponents(components);
        for(Component component: components){
            if(component.hasEnabledProperty(SimplePhysicsProperty.NAME)){
                TransformProperty transformProperty = component.getProperty(TransformProperty.NAME);
                SimplePhysicsProperty physicsProperty = component.getProperty(SimplePhysicsProperty.NAME);
                transformProperty.move(physicsProperty.getVelocity());
                transformProperty.getRotation().mul(physicsProperty.getAngularVelocity());

            }
        }
    }
}
