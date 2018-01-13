package net.warpgame.engine.core.component.listenable;

import net.warpgame.engine.core.component.Component;
import net.warpgame.engine.core.event.Event;

/**
 * @author Jaca777
 *         Created 2016-07-07 at 00
 */
public class ChildRemovedEvent extends Event {
    public static final String CHILD_REMOVED_EVENT_NAME = "childRemovedEvent";

    private Component removedChild;

    public ChildRemovedEvent(Component removedChild) {
        super(CHILD_REMOVED_EVENT_NAME);
        this.removedChild = removedChild;
    }

    public Component getRemovedChild() {
        return removedChild;
    }
}