package psqq_item.psqq_item.screen.MotorGenerators;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import psqq_item.psqq_item.ModMain;

import java.text.DecimalFormat;

public class MotorGeneratorsScreen extends AbstractContainerScreen<MotorGeneratorsMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ModMain.MOD_ID, "textures/gui/motor_generators_gui.png");

    // 定义用于格式化小数的格式化器
    private final DecimalFormat decimalFormat = new DecimalFormat("0.000");

    public MotorGeneratorsScreen(MotorGeneratorsMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 176;
        this.imageHeight = 168;
        this.inventoryLabelY = 86;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if(menu.isCrafting()) {
            // 绘制进度条
            guiGraphics.blit(TEXTURE, x + 83, y + 37, 177, 0, menu.getScaledProgress(), 17);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // 渲染进度百分比文本
        if(menu.isCrafting()) {

            int textx = (width - imageWidth) / 2;
            int texty = (height - imageHeight) / 2;

            // 计算进度百分比
            float percentage = 0;
            int maxProgress = menu.getDataValue(1);
            if (maxProgress != 0) {
                percentage = (float) menu.getDataValue(0) / maxProgress * 100;
            }

            int textColor;
            if (percentage < 30) {
                textColor = 0xFF0000; // 红色
            } else if (percentage < 70) {
                textColor = 0xFFAA00; // 橙色
            } else {
                textColor = 0x00AA00; // 绿色
            }

            // 格式化进度百分比，保留三位小数
            String progressText = decimalFormat.format(percentage) + "%";

            // 在GUI右下角渲染文本
            guiGraphics.drawString(font, progressText, textx + 124, texty + 70, textColor, false);
        }
    }
}