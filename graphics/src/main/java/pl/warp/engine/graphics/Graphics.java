package pl.warp.engine.graphics;

import pl.warp.engine.core.*;
import pl.warp.engine.graphics.camera.Camera;
import pl.warp.engine.graphics.light.Environment;
import pl.warp.engine.graphics.particles.ParticleEmitterRenderer;
import pl.warp.engine.graphics.particles.ParticleRenderer;
import pl.warp.engine.graphics.pipeline.OnScreenRenderer;
import pl.warp.engine.graphics.pipeline.Pipeline;
import pl.warp.engine.graphics.pipeline.builder.PipelineBuilder;
import pl.warp.engine.graphics.mesh.MeshRenderer;
import pl.warp.engine.graphics.skybox.SkyboxRenderer;
import pl.warp.engine.graphics.window.Display;
import pl.warp.engine.graphics.window.GLFWWindowManager;

/**
 * @author Jaca777
 *         Created 2016-07-10 at 21
 */
public class Graphics {

    private EngineContext context;
    private Camera mainViewCamera;
    private RenderingSettings settings;

    private SyncEngineThread thread;
    private Display display;
    private GLFWWindowManager windowManager;
    private Environment environment;

    public Graphics(EngineContext context, Camera mainViewCamera, RenderingSettings settings) {
        this.context = context;
        this.mainViewCamera = mainViewCamera;
        this.settings = settings;
        createThread();
    }

    private void createThread() {
        this.thread = new SyncEngineThread(new SyncTimer(settings.getFps()), new OnePerUpdateExecutionStrategy());
    }

    public void create() {
        createWindow();
        createEnvironment();
        createRenderingTask();
        thread.start();
    }

    private void createWindow() {
        this.display = new Display(settings.getWidth(), settings.getHeight());
        this.windowManager = new GLFWWindowManager(this.thread::interrupt);
        WindowTask windowTask = new WindowTask(windowManager, display);
        thread.scheduleTask(windowTask);
    }

    private void createEnvironment() {
        this.environment = new Environment();
        EnvironmentTask environmentTask = new EnvironmentTask(context.getScene(), environment);
        thread.scheduleTask(environmentTask);
    }

    private void createRenderingTask() {
        Pipeline pipeline = createPipeline();
        RenderingTask task = new RenderingTask(context, display, windowManager, pipeline);
        thread.scheduleTask(task);
    }

    private Pipeline createPipeline() {
        MeshRenderer meshRenderer = new MeshRenderer(mainViewCamera, environment);
        SkyboxRenderer skyboxRenderer = new SkyboxRenderer(mainViewCamera);
        ParticleRenderer particleRenderer = new ParticleRenderer(mainViewCamera);
        ParticleEmitterRenderer emitterRenderer = new ParticleEmitterRenderer();
        Renderer[] renderers = {skyboxRenderer, meshRenderer, emitterRenderer, particleRenderer};
        SceneRenderer sceneRenderer = new SceneRenderer(context.getScene(), settings, renderers);
        OnScreenRenderer onScreenRenderer = new OnScreenRenderer();
        return PipelineBuilder.from(sceneRenderer).to(onScreenRenderer);
    }


    public EngineThread getThread() {
        return thread;
    }

    public EngineContext getContext() {
        return context;
    }

    public RenderingSettings getSettings() {
        return settings;
    }

    public GLFWWindowManager getWindowManager() {
        return windowManager;
    }

    public Environment getEnvironment() {
        return environment;
    }
}