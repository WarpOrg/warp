package pl.warp.engine.ai.behaviourtree;

/**
 * @author Hubertus
 *         Created 03.01.2017
 */
public abstract class DecoratorNode extends Node {
    protected Node child;

    @Override
    public void addChild(Node child){
        this.child = child;
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node child) {
        this.child = child;
    }
}