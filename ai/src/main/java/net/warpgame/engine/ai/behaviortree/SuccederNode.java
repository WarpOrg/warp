package net.warpgame.engine.ai.behaviortree;

/**
 * @author Hubertus
 *         Created 05.02.17
 */
public class SuccederNode extends DecoratorNode{

    @Override
    int tick(Ticker ticker, int delta) {
        ticker.tickNode(child);
        return SUCCESS;
    }

    @Override
    protected void onOpen(Ticker ticker) {

    }

    @Override
    protected void onReEnter(Ticker ticker) {

    }

    @Override
    protected void onInit(Ticker ticker) {

    }

    @Override
    protected void onClose(Ticker ticker) {

    }
}
