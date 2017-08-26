package pl.warp.engine.core.component;

import pl.warp.engine.core.event.Event;
import pl.warp.engine.core.event.Listener;
import pl.warp.engine.core.EngineContext;
import pl.warp.engine.core.property.Property;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Jaca777
 *         Created 2017-01-27 at 19
 */
public interface Component {
    <T extends Property> T getProperty(Class<T> c);

    <T extends Property> boolean hasProperty(Class<T> c);

    <T extends Property> T getProperty(String name);

    boolean hasProperty(String name);

    <T extends Property> Optional<T> getPropertyIfExists(String name);

    boolean hasEnabledProperty(String name);

    <T extends Property> boolean hasEnabledProperty(Class<T> c);

    Set<Property> getProperties();

    <T extends Event> void triggerEvent(T event);

    Set<Listener> getListeners();

    <T extends Event> void triggerOnChildren(T event);

    <T extends Event> void broadcastEvent(T event);

    <T extends Event> void triggerOnRoot(T event);

    Component getParent();

    boolean hasParent();

    Component getChild(int index);

    int getChildrenNumber();

    //Considered redundant
    <T extends Property> Set<T> getChildrenProperties(Class<T> propertyClass);

    //Considered redundant
    <T extends Property> Set<T> getChildrenProperties(String propertyName);

    void addChild(Component child);

    void destroy();

    void forEachChildren(Consumer<Component> f);

    EngineContext getContext();

    void addProperty(Property property);

    void removeProperty(Property property);

    void setParent(Component parent);

    void removeChild(Component component);

    void addListener(Listener<?, ?> listener);

    void removeListener(Listener<?, ?> listener);
}