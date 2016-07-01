package pl.warp.engine.core.scene;

/**
 * @author Jaca777
 *         Created 2016-06-30 at 13
 */
class PropertyNotPresentException extends RuntimeException {
    public PropertyNotPresentException(String propertyName) {
        super("Property with name " + propertyName + " not present in the component.");
    }
    public <T extends Property> PropertyNotPresentException(Class<T> propertyType) {
        super("Property of type " + propertyType.getName() + " not present in the component.");
    }
}