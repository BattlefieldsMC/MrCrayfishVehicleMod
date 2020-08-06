package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class FuelDrumRenderer extends TileEntityRenderer<FuelDrumTileEntity>
{
    public FuelDrumRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(FuelDrumTileEntity fuelDrumTileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int lightTexture, int overlayTexture)
    {
        if(Minecraft.getInstance().player.isCrouching())
        {
            if(fuelDrumTileEntity.hasFluid() && this.renderDispatcher.cameraHitResult != null && this.renderDispatcher.cameraHitResult.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult result = (BlockRayTraceResult) this.renderDispatcher.cameraHitResult;
                if(result.getPos().equals(fuelDrumTileEntity.getPos()))
                {
                    this.drawFluidLabel(this.renderDispatcher.fontRenderer, fuelDrumTileEntity.getFluidTank(), matrixStack);
                    //this.drawFluidLabel(getFontRenderer(), te.getFluidTank(), (float) x + 0.5F, (float) y + 1.25F, (float) z + 0.5F);
                }
            }
        }
    }

    private void drawFluidLabel(FontRenderer fontRendererIn, FluidTank tank, MatrixStack matrixStack)
    {
        if(tank.getFluid().isEmpty())
            return;

        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
        RenderSystem.translated(0.5, 1.25, 0.5);
        RenderSystem.rotatef(-Minecraft.getInstance().player.rotationYaw, 0.0F, 1.0F, 0.0F);
        RenderSystem.rotatef(Minecraft.getInstance().player.rotationPitch, 1.0F, 0.0F, 0.0F);
        RenderSystem.scalef(-0.025F, -0.025F, 0.025F);
        RenderSystem.disableTexture();

        float level = tank.getFluidAmount() / (float) tank.getCapacity();
        double width = 30;
        double fuelWidth = width * level;
        double remainingWidth = width - fuelWidth;
        double offsetWidth = width / 2.0;

        FluidStack stack = tank.getFluid();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(tank.getFluid().getFluid().getAttributes().getStillTexture());
        if(sprite != null)
        {
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();

            float deltaV = maxV - minV;
            maxV = minV + (deltaV * 4F * 0.0625F);

            float deltaU = maxU - minU;
            maxU = minU + deltaU * level;

            Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-offsetWidth - 1, -2.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth - 1, 5.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + width + 1, 5.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + width + 1, -2.0, -0.01).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
            tessellator.draw();

            RenderSystem.enableTexture();
            RenderSystem.translated(0, 0, -0.05);

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-offsetWidth, -1.0, 0.0).tex(minU, maxV).endVertex();
            buffer.pos(-offsetWidth, 4.0, 0.0).tex(minU, minV).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, 4.0, 0.0).tex(maxU, minV).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, -1.0, 0.0).tex(maxU, maxV).endVertex();
            tessellator.draw();

            RenderSystem.disableTexture();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-offsetWidth + fuelWidth, -1.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth, 4.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth + remainingWidth, 4.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            buffer.pos(-offsetWidth + fuelWidth + remainingWidth, -1.0, 0.0).color(0.4F, 0.4F, 0.4F, 1.0F).endVertex();
            tessellator.draw();
        }

        RenderSystem.enableTexture();

        RenderSystem.scalef(0.5F, 0.5F, 0.5F);
        String name = stack.getDisplayName().getFormattedText();
        int nameWidth = fontRendererIn.getStringWidth(name) / 2;
        fontRendererIn.drawString(name, -nameWidth, -14, -1);

        RenderSystem.enableLighting();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.popMatrix();
    }
}
