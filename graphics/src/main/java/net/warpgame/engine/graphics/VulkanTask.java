package net.warpgame.engine.graphics;

import net.warpgame.engine.core.context.service.Profile;
import net.warpgame.engine.core.context.service.Service;
import net.warpgame.engine.core.context.task.RegisterTask;
import net.warpgame.engine.core.execution.task.EngineTask;
import net.warpgame.engine.graphics.command.StandardCommandPool;
import net.warpgame.engine.graphics.core.InstanceManager;
import net.warpgame.engine.graphics.window.RenderPass;
import net.warpgame.engine.graphics.window.SwapChain;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * @author MarconZet
 * Created 05.04.2019
 */

@Service
@Profile("graphics")
@RegisterTask(thread = "graphics")
public class VulkanTask extends EngineTask {
    private InstanceManager instanceManager;
    private StandardCommandPool commandPool;
    private SwapChain swapChain;
    private RenderPass renderPass;

    public VulkanTask(InstanceManager instanceManager, SwapChain swapChain, RenderPass renderPass) {
        this.instanceManager = instanceManager;
        this.swapChain = swapChain;
        this.renderPass = renderPass;
    }

    @Override
    protected void onInit() {
        instanceManager.create();
        commandPool = new StandardCommandPool(instanceManager.getDevice(), instanceManager.getGraphicsQueue());
        swapChain.create();
        renderPass.create();
    }

    @Override
    public void update(int delta) {
        ZerviceBypass.run = !glfwWindowShouldClose(instanceManager.getWindow().get())  ;
        glfwPollEvents();
    }

    @Override
    protected void onClose() {
        renderPass.destroy();
        swapChain.destroy();
        commandPool.destroy();
        instanceManager.destroy();
    }
}
