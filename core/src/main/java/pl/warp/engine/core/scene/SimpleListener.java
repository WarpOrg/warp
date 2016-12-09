package pl.warp.engine.core.scene;

import java.util.function.Consumer;

/**
 * @author Jaca777
 *         Created 2016-06-30 at 14
 */
public class SimpleListener<T extends Component, U extends Event> extends Listener<T, U> {

    private Consumer<U> handler;

    protected SimpleListener(T owner, String eventTypeName, Consumer<U> handler) {
        super(owner, eventTypeName);
        this.handler = handler;
    }

    protected SimpleListener(T owner, Class<U> eventClass, Consumer<U> handler) {
        super(owner, eventClass);
        this.handler = handler;
    }

    @Override
    public void handle(U event) {
        this.handler.accept(event);
    }

    public static <T extends Component, U extends Event> SimpleListener createListener(T owner, String eventTypeName, Consumer<U> handler) {
        return new SimpleListener(owner, eventTypeName, handler);
    }

    public static <T extends Component, U extends Event> SimpleListener createListener(T owner, Class<U> eventClass, Consumer<U> handler) {
        return new SimpleListener(owner, eventClass, handler);
    }
}
