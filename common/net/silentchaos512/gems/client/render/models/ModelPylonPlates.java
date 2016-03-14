package net.silentchaos512.gems.client.render.models;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class ModelPylonPlates {

    private IFlexibleBakedModel pylonPlatesPassiveModel;
    private IFlexibleBakedModel pylonPlatesBurnerModel;

    public ModelPylonPlates()
    {
        //load
        OBJModel model = ModelHelper.loadModel("ChaosPylonPlates");
        //retexture
        IModel pylonPlatesPassive = ModelHelper.retexture(model,"#skin.001","ChaosPylonPassive");
        IModel pylonPlatesBurner = ModelHelper.retexture(model,"#skin.001","ChaosPylonBurner");
        //activate
        pylonPlatesPassiveModel = ModelHelper.bake(pylonPlatesPassive);
        pylonPlatesBurnerModel = ModelHelper.bake(pylonPlatesBurner);
    }

    public void renderPylonPlates(int pylonType)
    {
        switch (pylonType)
        {
            case 1:
                renderModel(pylonPlatesBurnerModel);
                break;
            default:
                renderModel(pylonPlatesPassiveModel);
        }
    }

    private void renderModel(IFlexibleBakedModel model)
    {
        renderModel(model, -1);
    }

    private void renderModel(IFlexibleBakedModel model, int color)
    {
        ModelHelper.renderModel(model,color);
    }
}
