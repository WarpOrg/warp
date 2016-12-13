package pl.warp.engine.graphics.metadata.loader;

import pl.warp.engine.core.EngineContext;
import pl.warp.engine.core.scene.Component;
import pl.warp.engine.graphics.metadata.ComponentMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jaca777
 *         Created 2016-12-13 at 19
 */
public class ComponentLoader {

    private PropertyLoader propertyLoader;
    private EngineContext context;

    public ComponentLoader(PropertyLoader propertyLoader, EngineContext context) {
        this.propertyLoader = propertyLoader;
        this.context = context;
    }

    public Component loadComponentMetadata(ComponentMetadata metadata){
        Component component = metadata.newInstance(context);
        loadProperties(metadata, component);
        loadChildren(metadata, component);
        return component;
    }

    private void loadProperties(ComponentMetadata metadata, Component component) {
        metadata.getPropertyMetadata()
                .forEach(propertyMetadata -> propertyLoader.loadPropertyMetadata(propertyMetadata, component));
    }

    private void loadChildren(ComponentMetadata metadata, Component component) {
        Set<Component> children = new HashSet<>();
        metadata.forEachChildren(md -> {
            if (md instanceof ComponentMetadata) {
                Component child = loadComponentMetadata((ComponentMetadata) md);
                children.add(child);
            }
        });
        children.forEach(component::addChild); // prevents deadlock
    }
}
