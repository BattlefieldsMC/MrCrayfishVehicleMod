package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractRenderTrailer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.SeederTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class RenderSeederTrailer extends AbstractRenderTrailer<SeederTrailerEntity>
{
    @Override
    public void render(SeederTrailerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        //Render the body
        this.renderDamagedPart(entity, SpecialModels.SEEDER_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, true, -17.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks, light);
        this.renderWheel(entity, matrixStack, renderTypeBuffer, false, 17.5F * 0.0625F, -0.5F, 0.0F, 2.0F, partialTicks, light);

        StorageInventory inventory = entity.getInventory();
        if(inventory != null)
        {
            int layer = 0;
            int index = 0;
            for(int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                if(!stack.isEmpty())
                {
                    matrixStack.push();
                    {
                        matrixStack.translate(-10.5 * 0.0625, -3 * 0.0625, -2 * 0.0625);
                        matrixStack.scale(0.45F, 0.45F, 0.45F);

                        int count = Math.max(1, stack.getCount() / 16);
                        int width = 4;
                        int maxLayerCount = 8;
                        for(int j = 0; j < count; j++)
                        {
                            matrixStack.push();
                            {
                                int layerIndex = index % maxLayerCount;
                                //double yOffset = Math.sin(Math.PI * (((layerIndex + 0.5) % (double) width) / (double) width)) * 0.1;
                                //GlStateManager.translate(0, yOffset * ((double) layer / inventory.getSizeInventory()), 0);
                                matrixStack.translate(0, layer * 0.05, 0);
                                matrixStack.translate((layerIndex % width) * 0.75, 0, (float) (layerIndex / width) * 0.5);
                                matrixStack.translate(0.7 * (layer % 2), 0, 0);
                                matrixStack.rotate(Vector3f.XP.rotationDegrees(90F));
                                matrixStack.rotate(Vector3f.ZP.rotationDegrees(47F * index));
                                matrixStack.rotate(Vector3f.XP.rotationDegrees(2F * layerIndex));
                                matrixStack.translate(layer * 0.001, layer * 0.001, layer * 0.001); // Fixes Z fighting
                                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer);
                            }
                            matrixStack.pop();
                            index++;
                            if(index % maxLayerCount == 0)
                            {
                                layer++;
                            }
                        }
                    }
                    matrixStack.pop();
                }
            }
        }

        this.renderSpike(entity, matrixStack, renderTypeBuffer, -12.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, -8.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, -4.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 0.0F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 4.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 8.0F * 0.0625F, partialTicks, light);
        this.renderSpike(entity, matrixStack, renderTypeBuffer, 12.0F * 0.0625F, partialTicks, light);
    }

    private void renderSpike(TrailerEntity trailer, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, double offsetX, float partialTicks, int light)
    {
        matrixStack.push();
        matrixStack.translate(offsetX, -0.65, 0.0);
        float wheelRotation = trailer.prevWheelRotation + (trailer.wheelRotation - trailer.prevWheelRotation) * partialTicks;
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-wheelRotation));
        matrixStack.scale(0.75F, 0.75F, 0.75F);
        RenderUtil.renderColoredModel(SpecialModels.SEED_SPIKER.getModel(), ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.pop();
    }
}
