package net.warpgame.engine.graphics.window;

import net.warpgame.engine.core.context.service.Service;
import net.warpgame.engine.graphics.command.QueueFamilyIndices;
import net.warpgame.engine.graphics.core.Device;
import net.warpgame.engine.graphics.core.PhysicalDevice;
import net.warpgame.engine.graphics.utility.CreateAndDestroy;
import net.warpgame.engine.graphics.utility.VulkanAssertionError;
import org.lwjgl.BufferUtils;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author MarconZet
 * Created 11.04.2019
 */

@Service
public class SwapChain implements CreateAndDestroy {
    private long swapChain;

    private VkExtent2D swapChainExtent;
    private int swapChainImageFormat;

    private PhysicalDevice physicalDevice;
    private Device device;
    private Window window;
    private QueueFamilyIndices queueFamilyIndices;

    public SwapChain(PhysicalDevice physicalDevice, Device device, Window window, QueueFamilyIndices queueFamilyIndices) {
        this.physicalDevice = physicalDevice;
        this.device = device;
        this.window = window;
        this.queueFamilyIndices = queueFamilyIndices;
    }

    @Override
    public void create() {
        SwapChainSupportDetails swapChainSupport = new SwapChainSupportDetails(window, physicalDevice);

        VkSurfaceFormatKHR surfaceFormat = swapChainSupport.chooseSwapSurfaceFormat();
        int presentMode = swapChainSupport.chooseSwapPresentMode();
        swapChainExtent = swapChainSupport.chooseSwapExtent(window);
        swapChainImageFormat = surfaceFormat.format();

        int imageCount = swapChainSupport.getCapabilities().minImageCount() + 1;
        if (swapChainSupport.getCapabilities().maxImageCount() > 0) {
            imageCount = Math.min(imageCount, swapChainSupport.getCapabilities().maxImageCount());
        }

        VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.create()
                .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                .surface(window.getSurface())
                .minImageCount(imageCount)
                .imageFormat(surfaceFormat.format())
                .imageColorSpace(surfaceFormat.colorSpace())
                .imageExtent(swapChainExtent)
                .imageArrayLayers(1)
                .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                .preTransform(swapChainSupport.getCapabilities().currentTransform())
                .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                .presentMode(presentMode)
                .clipped(true)
                .oldSwapchain(VK_NULL_HANDLE);

        if (queueFamilyIndices.isPresentGraphics()) {
            IntBuffer indices = BufferUtils.createIntBuffer(2)
                    .put(queueFamilyIndices.getGraphicsFamily())
                    .put(queueFamilyIndices.getPresentFamily());
            indices.flip();
            createInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                    .pQueueFamilyIndices(indices);
        } else {
            createInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .pQueueFamilyIndices(null);
        }

        LongBuffer pSwapChain = BufferUtils.createLongBuffer(1);
        int err = vkCreateSwapchainKHR(device.get(), createInfo, null, pSwapChain);
        if (err != VK_SUCCESS) {
            throw new VulkanAssertionError("Failed to create swap chain", err);
        }
        swapChain = pSwapChain.get(0);
    }

    @Override
    public void destroy() {
        vkDestroySwapchainKHR(device.get(), swapChain, null);
    }

    public long get() {
        return swapChain;
    }

    public VkExtent2D getSwapChainExtent() {
        return swapChainExtent;
    }

    public int getSwapChainImageFormat() {
        return swapChainImageFormat;
    }
}
