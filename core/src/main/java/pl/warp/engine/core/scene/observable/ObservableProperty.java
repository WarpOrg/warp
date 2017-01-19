package pl.warp.engine.core.scene.observable;

import pl.warp.engine.core.scene.Component;
import pl.warp.engine.core.scene.Property;

import java.util.Arrays;

/**
 * @author Jaca777
 *         Created 2016-12-26 at 20
 */
public class ObservableProperty<T extends Component> extends Property<T> {
    public ObservableProperty(T owner, String name, ObservableValue... values) {
        super(owner, name);
        Arrays.asList(values).forEach(c -> c.registerObserver(s -> stateChanged((ObservableValue<?>) s)));
    }

    private void stateChanged(ObservableValue<?> changedValue) {
        getOwner().triggerEvent(new ObservablePropertyChangedEvent(this, changedValue));
    }
}