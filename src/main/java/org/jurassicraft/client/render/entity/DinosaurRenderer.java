package org.jurassicraft.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.client.render.entity.dinosaur.DinosaurRenderInfo;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.dinosaur.DinosaurMetadata;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.GrowthStage;

import java.awt.*;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class DinosaurRenderer extends RenderLiving<DinosaurEntity> {
    public Dinosaur dinosaur;
    public DinosaurRenderInfo renderInfo;

    public Random random;

    public DinosaurRenderer(DinosaurRenderInfo renderInfo, RenderManager renderManager) {
        super(renderManager, renderInfo.getModel(GrowthStage.INFANT), renderInfo.getShadowSize());

        this.dinosaur = renderInfo.getDinosaur();
        this.random = new Random();
        this.renderInfo = renderInfo;

        this.addLayer(new LayerEyelid(this));
    }

    @Override
    public void preRenderCallback(DinosaurEntity entity, float partialTick) {
        DinosaurMetadata metadata = this.dinosaur.getMetadata();
        float scaleModifier = entity.getAttributes().getScaleModifier();
        float scale = (float) entity.interpolate(metadata.getScaleInfant(), metadata.getScaleAdult()) * scaleModifier;

        this.shadowSize = scale * this.renderInfo.getShadowSize();

        GlStateManager.translate(metadata.getOffsetX() * scale, metadata.getOffsetY() * scale, metadata.getOffsetZ() * scale);

        String name = entity.getCustomNameTag();
        
        switch (name) {
            case "Kashmoney360":
            case "JTGhawk137":
                GlStateManager.scale(0.1F, scale, scale);
                break;
            case "Gegy":
                GlStateManager.scale(scale, 0.01F, scale);
                break;
            case "Notch":
                GlStateManager.scale(scale * 2, scale * 2, scale * 2);
                break;
            case "jglrxavpok":
                GlStateManager.scale(scale, scale, scale * -1);
                break;
            case "Wyn":
                int color = Color.HSBtoRGB((entity.world.getTotalWorldTime() % 1000) / 100f, 1f, 1f);
                GlStateManager.color((color & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, ((color >> 16) & 0xFF) / 255f);
            default:
                GlStateManager.scale(scale, scale, scale);
                break;
        }
    }

    @Override
    public void doRender(DinosaurEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.mainModel = this.renderInfo.getModel(entity.getGrowthStage());
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    public ResourceLocation getEntityTexture(DinosaurEntity entity) {
        GrowthStage growthStage = entity.getGrowthStage();
        if (!this.dinosaur.doesSupportGrowthStage(growthStage)) {
            growthStage = GrowthStage.ADULT;
        }
        return entity.isMale() ? this.dinosaur.getMaleTexture(growthStage) : this.dinosaur.getFemaleTexture(growthStage);
    }

    @Override
    protected void applyRotations(DinosaurEntity entity, float p_77043_2_, float p_77043_3_, float partialTicks) {
        GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);
    }

    @SideOnly(Side.CLIENT)
    public class LayerEyelid implements LayerRenderer<DinosaurEntity> {
        private final DinosaurRenderer renderer;

        public LayerEyelid(DinosaurRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(DinosaurEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float yaw, float pitch, float scale) {
            if (!entity.isInvisible()) {
                if (entity.areEyelidsClosed()) {
                    ResourceLocation texture = this.renderer.dinosaur.getEyelidTexture(entity);
                    if (texture != null) {
                        ITextureObject textureObject = Minecraft.getMinecraft().getTextureManager().getTexture(texture);
                        if (textureObject != TextureUtil.MISSING_TEXTURE) {
                            this.renderer.bindTexture(texture);

                            this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, age, yaw, pitch, scale);
                            this.renderer.setLightmap(entity); //TODO: Make sure this works this.renderer.setLightmap(entity, partialTicks);
                        }
                    }
                }
            }
        }

        @Override
        public boolean shouldCombineTextures() {
            return true;
        }
    }
}
