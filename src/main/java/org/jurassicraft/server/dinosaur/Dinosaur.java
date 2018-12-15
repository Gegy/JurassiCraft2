package org.jurassicraft.server.dinosaur;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.animation.PoseHandler;
import org.jurassicraft.server.api.GrowthStageGenderContainer;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.GrowthStage;
import org.jurassicraft.server.tabula.TabulaModelHelper;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Dinosaur implements Comparable<Dinosaur> {
    private final Map<GrowthStage, List<ResourceLocation>> overlays = new EnumMap<>(GrowthStage.class);
    private final Map<GrowthStage, ResourceLocation> maleTextures = new EnumMap<>(GrowthStage.class);
    private final Map<GrowthStage, ResourceLocation> femaleTextures = new EnumMap<>(GrowthStage.class);
    private final Map<GrowthStageGenderContainer, ResourceLocation> eyelidTextures = new HashMap<>();
    private final Map<GrowthStage, TabulaModelContainer> models = new EnumMap<>(GrowthStage.class);

    private final DinosaurMetadata metadata;

    private boolean shouldRegister = true;

    private PoseHandler<?> poseHandler;

    public Dinosaur() {
        this.metadata = this.buildMetadata();
    }

    protected abstract DinosaurMetadata buildMetadata();

    public static Matrix4d getParentRotationMatrix(TabulaModelContainer model, TabulaCubeContainer cube, boolean includeParents, boolean ignoreSelf, float rot) {
        List<TabulaCubeContainer> parentCubes = new ArrayList<>();

        do {
            if (ignoreSelf) {
                ignoreSelf = false;
            } else {
                parentCubes.add(cube);
            }
        }
        while (includeParents && cube.getParentIdentifier() != null && (cube = TabulaModelHelper.getCubeByIdentifier(cube.getParentIdentifier(), model)) != null);

        Matrix4d mat = new Matrix4d();
        mat.setIdentity();
        Matrix4d transform = new Matrix4d();

        transform.rotY(rot / 180 * Math.PI);
        mat.mul(transform);

        for (int i = parentCubes.size() - 1; i >= 0; i--) {
            cube = parentCubes.get(i);
            transform.setIdentity();
            transform.setTranslation(new Vector3d(cube.getPosition()));
            mat.mul(transform);

            double rotX = cube.getRotation()[0];
            double rotY = cube.getRotation()[1];
            double rotZ = cube.getRotation()[2];

            transform.rotZ(rotZ / 180 * Math.PI);
            mat.mul(transform);
            transform.rotY(rotY / 180 * Math.PI);
            mat.mul(transform);
            transform.rotX(rotX / 180 * Math.PI);
            mat.mul(transform);
        }

        return mat;
    }

    private static double[][] getTransformation(Matrix4d matrix) {
        double sinRotationAngleY, cosRotationAngleY, sinRotationAngleX, cosRotationAngleX, sinRotationAngleZ, cosRotationAngleZ;

        sinRotationAngleY = -matrix.m20;
        cosRotationAngleY = Math.sqrt(1 - sinRotationAngleY * sinRotationAngleY);

        if (Math.abs(cosRotationAngleY) > 0.0001) {
            sinRotationAngleX = matrix.m21 / cosRotationAngleY;
            cosRotationAngleX = matrix.m22 / cosRotationAngleY;
            sinRotationAngleZ = matrix.m10 / cosRotationAngleY;
            cosRotationAngleZ = matrix.m00 / cosRotationAngleY;
        } else {
            sinRotationAngleX = -matrix.m12;
            cosRotationAngleX = matrix.m11;
            sinRotationAngleZ = 0;
            cosRotationAngleZ = 1;
        }

        double rotationAngleX = epsilon(Math.atan2(sinRotationAngleX, cosRotationAngleX)) / Math.PI * 180;
        double rotationAngleY = epsilon(Math.atan2(sinRotationAngleY, cosRotationAngleY)) / Math.PI * 180;
        double rotationAngleZ = epsilon(Math.atan2(sinRotationAngleZ, cosRotationAngleZ)) / Math.PI * 180;
        return new double[][] { { epsilon(matrix.m03), epsilon(matrix.m13), epsilon(matrix.m23) }, { rotationAngleX, rotationAngleY, rotationAngleZ } };
    }

    private static double epsilon(double x) {
        return x < 0 ? x > -0.0001 ? 0 : x : x < 0.0001 ? 0 : x;
    }

    public void init() {
        for (GrowthStage stage : GrowthStage.VALUES) {
            if (this.doesSupportGrowthStage(stage)) {
                this.setModelContainer(stage, this.parseModel(stage.getKey()));
            } else {
                this.setModelContainer(stage, this.getModelContainer(GrowthStage.ADULT));
            }
        }

        ResourceLocation identifier = this.getMetadata().getIdentifier();
        String domain = identifier.getResourceDomain();
        String name = identifier.getResourcePath();

        String textureRoot = "textures/entities/" + name + "/";

        for (GrowthStage growthStage : GrowthStage.values()) {
            String growthStageName = growthStage.getKey();

            if (!this.doesSupportGrowthStage(growthStage)) {
                growthStageName = GrowthStage.ADULT.getKey();
            }

            this.maleTextures.put(growthStage, new ResourceLocation(domain, textureRoot + name + "_male_" + growthStageName + ".png"));
            this.femaleTextures.put(growthStage, new ResourceLocation(domain, textureRoot + name + "_female_" + growthStageName + ".png"));
            this.eyelidTextures.put(new GrowthStageGenderContainer(growthStage, true), new ResourceLocation(JurassiCraft.MODID, textureRoot + name + "_male_" + growthStageName + "_eyelid.png"));
            this.eyelidTextures.put(new GrowthStageGenderContainer(growthStage, false), new ResourceLocation(JurassiCraft.MODID, textureRoot + name + "_female_" + growthStageName + "_eyelid.png"));

            List<ResourceLocation> overlaysForGrowthStage = new ArrayList<>();

            for (int i = 1; i <= this.metadata.getOverlayCount(); i++) {
                overlaysForGrowthStage.add(new ResourceLocation(JurassiCraft.MODID, textureRoot + name + "_overlay_" + growthStageName + "_" + i + ".png"));
            }

            this.overlays.put(growthStage, overlaysForGrowthStage);
        }

        this.poseHandler = new PoseHandler(this);
    }

    @Nullable
    protected TabulaModelContainer parseModel(String growthStage) {
        ResourceLocation identifier = this.getIdentifier();
        String domain = identifier.getResourceDomain();
        String path = identifier.getResourcePath();
        ResourceLocation location = new ResourceLocation(domain, "models/entities/" + path + "/" + growthStage + "/" + path + "_" + growthStage + "_idle");

        try {
            return TabulaModelHelper.loadTabulaModel(location);
        } catch (Exception e) {
            JurassiCraft.getLogger().fatal("Couldn't load model " + location, e);
        }
        return null;
    }

    public ResourceLocation getMaleTexture(GrowthStage stage) {
        return this.maleTextures.get(stage);
    }

    public ResourceLocation getFemaleTexture(GrowthStage stage) {
        return this.femaleTextures.get(stage);
    }

    public void disableRegistry() {
        this.shouldRegister = false;
    }

    public boolean shouldRegister() {
        return this.shouldRegister;
    }

    @Override
    public int hashCode() {
        return this.metadata.getIdentifier().hashCode();
    }

    protected int fromDays(int days) {
        return (days * 24000) / 8;
    }

    @Override
    public int compareTo(Dinosaur dinosaur) {
        return this.getIdentifier().compareTo(dinosaur.getIdentifier());
    }

    public ResourceLocation getOverlayTexture(GrowthStage stage, int overlay) {
        return this.overlays.containsKey(stage) ? this.overlays.get(stage).get(overlay) : null;
    }

    public ResourceLocation getEyelidTexture(DinosaurEntity entity) {
        return this.eyelidTextures.get(new GrowthStageGenderContainer(entity.getGrowthStage(), entity.isMale()));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Dinosaur && ((Dinosaur) object).getIdentifier().equals(this.getIdentifier());
    }

    public double[] getCubePosition(String cubeName, GrowthStage stage) {
        TabulaModelContainer model = this.getModelContainer(stage);

        TabulaCubeContainer cube = TabulaModelHelper.getCubeByName(cubeName, model);

        if (cube != null) {
            return cube.getPosition();
        }

        return new double[] { 0.0, 0.0, 0.0 };
    }

    public double[] getParentedCubePosition(String cubeName, GrowthStage stage, float rot) {
        TabulaModelContainer model = this.getModelContainer(stage);

        TabulaCubeContainer cube = TabulaModelHelper.getCubeByName(cubeName, model);

        if (cube != null) {
            return getTransformation(getParentRotationMatrix(model, cube, true, false, rot))[0];
        }

        return new double[] { 0.0, 0.0, 0.0 };
    }

    public double[] getHeadPosition(GrowthStage stage, float rot) {
        return this.getParentedCubePosition(this.metadata.getHeadCubeName(), stage, rot);
    }

    public TabulaModelContainer getModelContainer(GrowthStage stage) {
        TabulaModelContainer model = this.models.get(stage);
        if (model == null) {
            return this.models.get(GrowthStage.ADULT);
        }
        return model;
    }

    private void setModelContainer(GrowthStage stage, TabulaModelContainer model) {
        this.models.put(stage, model);
    }

    public PoseHandler<?> getPoseHandler() {
        return this.poseHandler;
    }

    public DinosaurMetadata getMetadata() {
        return this.metadata;
    }

    public ResourceLocation getIdentifier() {
        return this.getMetadata().getIdentifier();
    }

    public String getLocalizedName() {
        ResourceLocation identifier = this.metadata.getIdentifier();
        return I18n.format("entity." + identifier.getResourceDomain() + "." + identifier.getResourcePath() + ".name");
    }

    public boolean doesSupportGrowthStage(GrowthStage stage) {
        return stage == GrowthStage.ADULT || stage == GrowthStage.SKELETON;
    }

    public List<GrowthStage> getSupportedStages() {
        List<GrowthStage> supportedStages = new ArrayList<>(4);
        for (GrowthStage stage : GrowthStage.VALUES) {
            if (this.doesSupportGrowthStage(stage)) {
                supportedStages.add(stage);
            }
        }
        return supportedStages;
    }

    public void applyMeatEffect(EntityPlayer player, boolean cooked) {
    }

    public DinosaurEntity construct(World world) {
        return this.metadata.construct(world);
    }

    public enum DinosaurType {
        AGGRESSIVE,
        NEUTRAL,
        PASSIVE,
        SCARED
    }

    public enum BirthType {
        LIVE_BIRTH,
        EGG_LAYING
    }
}
