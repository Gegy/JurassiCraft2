package org.jurassicraft.client.render.block;

import net.ilexiconn.llibrary.client.model.tabula.TabulaModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.entity.ElectricFencePoleBlockEntity;
import org.jurassicraft.server.block.fence.ElectricFencePoleBlock;
import org.jurassicraft.server.tabula.TabulaModelHelper;

public class ElectricFencePoleRenderer extends TileEntitySpecialRenderer<ElectricFencePoleBlockEntity> {
    private Minecraft mc = Minecraft.getMinecraft();

    private TabulaModel model;
    private ResourceLocation texture;

    public ElectricFencePoleRenderer() {
        try {
            this.model = new TabulaModel(TabulaModelHelper.loadTabulaModel(new ResourceLocation(JurassiCraft.MODID, "models/block/low_security_fence_pole_lights")));
            this.texture = new ResourceLocation(JurassiCraft.MODID, "textures/blocks/low_security_fence_pole.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(ElectricFencePoleBlockEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile != null) {
            boolean active = false;

            BlockPos position = tile.getPos();
            IBlockState state = tile.getWorld().getBlockState(position);
            if (state.getBlock() == BlockHandler.LOW_SECURITY_FENCE_POLE) {
                active = state.getActualState(tile.getWorld(), position).getValue(ElectricFencePoleBlock.ACTIVE);
            }

            if (active) {
                GlStateManager.pushMatrix();

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);

                double scale = 1.0;
                GlStateManager.scale(-scale, -scale, scale);

                this.mc.getTextureManager().bindTexture(this.texture);

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

                this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);

                GlStateManager.popMatrix();
            }
        }
    }
}
