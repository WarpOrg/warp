package net.warpgame.engine.core.component;

import net.warpgame.engine.core.context.service.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hubertus
 * Created 09.12.2017
 */
@Service
public class ComponentRegistry {
    private Map<Integer, Component> componentMap = new HashMap<>();
    private int maxId = 0;

    public synchronized void addComponent(Component component, int id) throws IdExistsException {
        if (componentMap.containsKey(id)) throw new IdExistsException();
        componentMap.put(id, component);
        if (maxId < id) maxId = id;
    }

    public synchronized int addComponent(Component component) {
        componentMap.put(maxId, component);
        maxId++;
        return maxId - 1;
    }

    public synchronized Component getComponent(int id) {
        return componentMap.get(id);
    }

    public synchronized Component getRootComponent() {
        return componentMap.get(0);
    }

    public synchronized void removeComponent(int id) {
        componentMap.remove(id);
    }

    public synchronized void getComponents(Collection<Component> target) {
        target.addAll(componentMap.values());
    }
}
