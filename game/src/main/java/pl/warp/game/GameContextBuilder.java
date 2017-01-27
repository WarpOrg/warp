package pl.warp.game;

import pl.warp.engine.core.scene.Scene;
import pl.warp.engine.core.scene.input.Input;
import pl.warp.engine.core.scene.script.ScriptManager;
import pl.warp.engine.physics.RayTester;

/**
 * @author Jaca777
 *         Created 2017-01-27 at 17
 */
public class GameContextBuilder {
    private GameContext gameContext;

    public GameContextBuilder() {
        this.gameContext = new GameContext();
    }

    public void setRayTester(RayTester rayTester) {
        gameContext.setRayTester(rayTester);
    }

    public GameContext getGameContext() {
        return gameContext;
    }

    public void setScene(Scene scene) {
        gameContext.setScene(scene);
    }

    public void setScriptManager(ScriptManager scriptManager) {
        gameContext.setScriptManager(scriptManager);
    }

    public void setInput(Input input) {
        gameContext.setInput(input);
    }
}