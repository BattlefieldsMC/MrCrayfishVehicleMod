package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public class RenderEntityVehicle<T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable, R extends AbstractRenderVehicle<T>> extends EntityRenderer<T>
{
    private final RenderVehicleWrapper<T, R> wrapper;

    public RenderEntityVehicle(EntityRendererManager renderManager, RenderVehicleWrapper<T, R> wrapper)
    {
        super(renderManager);
        this.wrapper = wrapper;
    }

    @Override
    public ResourceLocation getEntityTexture(T entity)
    {
        return null;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        if(!entity.isAlive())
            return;

        if(entity.getRidingEntity() instanceof EntityJack)
            return;

        matrixStack.push();
        wrapper.applyPreRotations(entity, matrixStack, partialTicks);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-entityYaw));
        this.setupBreakAnimation(entity, matrixStack, partialTicks);
        wrapper.render(entity, matrixStack, renderTypeBuffer, partialTicks, light);
        matrixStack.pop();

        EntityRayTracer.instance().renderRayTraceElements(entity, matrixStack, entityYaw);
    }

    private void setupBreakAnimation(VehicleEntity vehicle, MatrixStack matrixStack, float partialTicks)
    {
        float timeSinceHit = (float) vehicle.getTimeSinceHit() - partialTicks;
        if(timeSinceHit > 0.0F)
        {
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(timeSinceHit) * timeSinceHit));
        }
    }
}
