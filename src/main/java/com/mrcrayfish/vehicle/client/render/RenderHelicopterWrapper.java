package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.RayTraceResult;

/**
 * Author: MrCrayfish
 */
public class RenderHelicopterWrapper<T extends HelicopterEntity & EntityRayTracer.IEntityRayTraceable, R extends AbstractRenderVehicle<T>> extends RenderVehicleWrapper<T, R>
{
    public RenderHelicopterWrapper(R renderVehicle)
    {
        super(renderVehicle);
    }

    public void render(T entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        if(!entity.isAlive())
            return;

        matrixStack.push();

        VehicleProperties properties = entity.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.rotate(Vector3f.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.rotate(Vector3f.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        //Translate the body
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Apply vehicle scale
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0, 0.5, 0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.translate(0, properties.getAxleOffset() * 0.0625F, 0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0, properties.getWheelOffset() * 0.0625F, 0);

        //Render body
        renderVehicle.render(entity, matrixStack, renderTypeBuffer, partialTicks, light);

        //Render the engine if the vehicle has explicitly stated it should
        if(entity.shouldRenderEngine() && entity.hasEngine())
        {
            this.renderEngine(entity, properties.getEnginePosition(), RenderUtil.getEngineModel(entity), matrixStack, renderTypeBuffer, light);
        }

        //Render the fuel port of the vehicle
        if(entity.shouldRenderFuelPort() && entity.requiresFuel())
        {
            PoweredVehicleEntity.FuelPortType fuelPortType = entity.getFuelPortType();
            EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
            if(result != null && result.getType() == RayTraceResult.Type.ENTITY && result.getEntity() == entity && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING))
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getOpenModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.NO_OVERLAY);
                if(renderVehicle.shouldRenderFuelLid())
                {
                    //this.renderPart(properties.getFuelPortLidPosition(), entity.fuelPortLid);
                }
                entity.playFuelPortOpenSound();
            }
            else
            {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getClosedModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.NO_OVERLAY);
                entity.playFuelPortCloseSound();
            }
        }

        if(entity.isKeyNeeded())
        {
            this.renderPart(properties.getKeyPortPosition(), renderVehicle.getKeyHoleModel().getModel(), matrixStack, renderTypeBuffer, entity.getColor(), light, OverlayTexture.NO_OVERLAY);
            if(!entity.getKeyStack().isEmpty())
            {
                this.renderKey(properties.getKeyPosition(), entity.getKeyStack(), RenderUtil.getModel(entity.getKeyStack()), matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            }
        }

        matrixStack.pop();
    }

    @Override
    public void applyPreRotations(T entity, MatrixStack matrixStack, float partialTicks)
    {
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
    }
}
