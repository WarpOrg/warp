package pl.warp.engine.physics.property;

import org.joml.Vector3f;
import pl.warp.engine.core.scene.Component;
import pl.warp.engine.core.scene.Property;

import java.util.Objects;

/**
 * Created by hubertus on 7/4/16.
 */

public class PhysicalBodyProperty extends Property<Component> {

    public static final String PHYSICAL_BODY_PROPERTY_NAME = "physicalBody";

    private Vector3f torque = new Vector3f();
    private Vector3f velocity = new Vector3f();
    private float mass;
    private float radius;
    private float interia;

    private Vector3f nextTickTranslation;
    private Vector3f nextTickRotation;

    public PhysicalBodyProperty(Component owner, float mass, float radius) {
        super(owner, PHYSICAL_BODY_PROPERTY_NAME);
        this.mass = mass;
        this.radius = radius;
        interia = mass * radius * radius;
    }

    public Vector3f getTorque() {
        return torque;
    }

    public void setTorque(Vector3f torque) {
        this.torque.set(torque);
    }

    public void addTorque(Vector3f value) {
        torque.add(value.div(interia));
    }

    public void removeTorque(Vector3f value) {
        torque.sub(value.div(interia));
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public synchronized void applyForce(Vector3f force) {
        velocity.add(force.div(mass));
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getInteria() {
        return interia;
    }

    public float getRadius() {
        return radius;
    }

    public Vector3f getNextTickTranslation() {
        return nextTickTranslation;
    }

    public void setNextTickTranslation(Vector3f nextTickTranslation) {
        this.nextTickTranslation = nextTickTranslation;
    }

    public Vector3f getNextTickRotation() {
        return nextTickRotation;
    }

    public void setNextTickRotation(Vector3f nextTickRotation) {
        this.nextTickRotation = nextTickRotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhysicalBodyProperty that = (PhysicalBodyProperty) o;
        return Float.compare(that.mass, mass) == 0 &&
                Float.compare(that.radius, radius) == 0 &&
                Float.compare(that.interia, interia) == 0 &&
                Objects.equals(torque, that.torque) &&
                Objects.equals(velocity, that.velocity) &&
                Objects.equals(nextTickTranslation, that.nextTickTranslation) &&
                Objects.equals(nextTickRotation, that.nextTickRotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(torque, velocity, mass, radius, interia, nextTickTranslation, nextTickRotation);
    }
}
