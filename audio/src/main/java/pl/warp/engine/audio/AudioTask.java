package pl.warp.engine.audio;

import pl.warp.engine.core.EngineTask;

/**
 * Created by hubertus on 17.12.16.
 */
public class AudioTask extends EngineTask {

int source;
    private AudioContext context;
    @Override
    protected void onInit() {
        context = new AudioContext();

    }

    @Override
    protected void onClose() {

    }

    @Override
    public void update(int delta) {

    }

    public AudioContext getContext() {
        return context;
    }
}
