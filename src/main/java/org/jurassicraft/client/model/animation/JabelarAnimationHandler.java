package org.jurassicraft.client.model.animation;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.AnimatableModel;
import org.jurassicraft.server.api.Animatable;
import org.jurassicraft.server.tabula.TabulaModelHelper;

import java.io.IOException;
import java.util.Map;

/**
 * @author jabelar
 *         This class handles per-entity animations.
 */
@SideOnly(Side.CLIENT)
public class JabelarAnimationHandler<ENTITY extends EntityLivingBase & Animatable> {
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final AnimationPass DEFAULT_PASS;
    private final AnimationPass MOVEMENT_PASS;
    private final AnimationPass ON_LAND_PASS;

    public JabelarAnimationHandler(ENTITY entity, AnimatableModel model, PosedCuboid[][] poses, Map<Animation, float[][]> poseSequences, boolean useInertialTweens) {
        this.DEFAULT_PASS = new AnimationPass(poseSequences, poses, useInertialTweens);
        this.MOVEMENT_PASS = new MovementAnimationPass(poseSequences, poses, useInertialTweens);
        this.ON_LAND_PASS = new OnLandAnimationPass(poseSequences, poses, useInertialTweens);

        this.init(entity, model);
    }

    public static AnimatableModel loadModel(ResourceLocation model) {
        try {
            return new AnimatableModel(TabulaModelHelper.loadTabulaModel(model), null);
        } catch (IOException e) {
            JurassiCraft.getLogger().error("Could not load Tabula model " + model, e);
        }
        return null;
    }

    private void init(ENTITY entity, AnimatableModel model) {
        AdvancedModelRenderer[] parts = this.getParts(model);
        this.DEFAULT_PASS.init(parts, entity);
        this.MOVEMENT_PASS.init(parts, entity);
        if (entity.isMarineCreature()) {
            this.ON_LAND_PASS.init(parts, entity);
        }
    }

    public void performAnimations(ENTITY entity, float limbSwing, float limbSwingAmount, float ticks) {
        this.DEFAULT_PASS.performAnimations(entity, limbSwing, limbSwingAmount, ticks);
        if (!entity.isCarcass()) {
            this.MOVEMENT_PASS.performAnimations(entity, limbSwing, limbSwingAmount, ticks);
            if (entity.isMarineCreature()) {
                this.ON_LAND_PASS.performAnimations(entity, limbSwing, limbSwingAmount, ticks);
            }
        }
    }

    private AdvancedModelRenderer[] getParts(AnimatableModel model) {
        AdvancedModelRenderer[] parts = new AdvancedModelRenderer[model.getIdentifierCubes().size()];
        int i = 0;
        for (Map.Entry<String, AdvancedModelRenderer> part : model.getIdentifierCubes().entrySet()) {
            parts[i++] = part.getValue();
        }
        return parts;
    }
}
