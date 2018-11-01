package org.jurassicraft.client.render.entity.dinosaur;

import net.ilexiconn.llibrary.client.model.tabula.TabulaModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.AnimatableModel;
import org.jurassicraft.client.model.animation.EntityAnimator;
import org.jurassicraft.client.render.entity.DinosaurRenderer;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.GrowthStage;
import org.jurassicraft.server.tabula.TabulaModelHelper;

@SideOnly(Side.CLIENT)
public class DinosaurRenderInfo implements IRenderFactory<DinosaurEntity> {
    private static TabulaModel DEFAULT_EGG_MODEL;
    private static ResourceLocation DEFAULT_EGG_TEXTURE;

    static {
        try {
            DEFAULT_EGG_MODEL = new TabulaModel(TabulaModelHelper.loadTabulaModel(new ResourceLocation(JurassiCraft.MODID, "models/entities/egg/tyrannosaurus")));
            DEFAULT_EGG_TEXTURE = new ResourceLocation(JurassiCraft.MODID, "textures/entities/egg/tyrannosaurus.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Dinosaur dinosaur;
    private final EntityAnimator<?> animator;
    private final AnimatableModel modelAdult;
    private final AnimatableModel modelInfant;
    private final AnimatableModel modelJuvenile;
    private final AnimatableModel modelAdolescent;
    private final AnimatableModel modelSkeleton;
    private TabulaModel eggModel;
    private ResourceLocation eggTexture;
    private float shadowSize = 0.65F;

    public DinosaurRenderInfo(Dinosaur dinosaur, EntityAnimator<?> animator, float shadowSize) {
        this.dinosaur = dinosaur;
        this.animator = animator;
        this.shadowSize = shadowSize;

        this.modelAdult = this.loadModel(GrowthStage.ADULT);
        this.modelInfant = this.loadModel(GrowthStage.INFANT);
        this.modelJuvenile = this.loadModel(GrowthStage.JUVENILE);
        this.modelAdolescent = this.loadModel(GrowthStage.ADOLESCENT);
        this.modelSkeleton = this.loadModel(GrowthStage.SKELETON);

        try {
            ResourceLocation identifier = dinosaur.getIdentifier();
            String domain = identifier.getResourceDomain();
            String path = identifier.getResourcePath();

            this.eggModel = new TabulaModel(TabulaModelHelper.loadTabulaModel(new ResourceLocation(domain, "models/entities/egg/" + path)));
            this.eggTexture = new ResourceLocation(domain, "textures/entities/egg/" + path + ".png");
        } catch (Exception e) {
            this.eggModel = DEFAULT_EGG_MODEL;
            this.eggTexture = DEFAULT_EGG_TEXTURE;
        }
    }

    public ModelBase getModel(GrowthStage stage) {
        switch (stage) {
            case INFANT:
                return this.modelInfant;
            case JUVENILE:
                return this.modelJuvenile;
            case ADOLESCENT:
                return this.modelAdolescent;
            case SKELETON:
                return this.modelSkeleton;
            default:
                return this.modelAdult;
        }
    }

    public ModelBase getEggModel() {
        return this.eggModel;
    }

    public ResourceLocation getEggTexture() {
        return this.eggTexture;
    }

    public EntityAnimator<?> getModelAnimator(GrowthStage stage) {
        if (stage == GrowthStage.SKELETON) {
            return null;
        }
        return this.animator;
    }

    public float getShadowSize() {
        return this.shadowSize;
    }

    public AnimatableModel loadModel(GrowthStage stage) {
        if (!this.dinosaur.doesSupportGrowthStage(stage)) {
            return this.getModelAdult();
        }
        return new AnimatableModel(this.dinosaur.getModelContainer(stage), this.getModelAnimator(stage));
    }

    public Dinosaur getDinosaur() {
        return this.dinosaur;
    }

    @Override
    public Render<? super DinosaurEntity> createRenderFor(RenderManager manager) {
        return new DinosaurRenderer(this, manager);
    }

    public AnimatableModel getModelAdult() {
        return this.modelAdult;
    }
}
