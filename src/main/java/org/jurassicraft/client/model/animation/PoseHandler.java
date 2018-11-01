package org.jurassicraft.client.model.animation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.AnimatableModel;
import org.jurassicraft.client.model.animation.dto.AnimatableRenderDefDTO;
import org.jurassicraft.client.model.animation.dto.AnimationsDTO;
import org.jurassicraft.client.model.animation.dto.PoseDTO;
import org.jurassicraft.server.api.Animatable;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.GrowthStage;
import org.jurassicraft.server.tabula.TabulaModelHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PoseHandler<ENTITY extends EntityLivingBase & Animatable> {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(AnimatableRenderDefDTO.class, new AnimatableRenderDefDTO.AnimatableDeserializer())
            .create();

    private Map<GrowthStage, ModelData> modelData;

    public PoseHandler(Dinosaur dinosaur) {
        this(dinosaur.getIdentifier(), dinosaur.getSupportedStages());
    }

    public PoseHandler(ResourceLocation identifier, List<GrowthStage> supported) {
        this.modelData = new EnumMap<>(GrowthStage.class);
        ResourceLocation entityResource = new ResourceLocation(identifier.getResourceDomain(), "models/entities/" + identifier.getResourcePath());
        for (GrowthStage growth : GrowthStage.values()) {
            try {
                GrowthStage actualGrowth = growth;
                if (!supported.contains(actualGrowth)) {
                    actualGrowth = GrowthStage.ADULT;
                }
                if (this.modelData.containsKey(actualGrowth)) {
                    this.modelData.put(growth, this.modelData.get(actualGrowth));
                } else {
                    ModelData loaded = this.loadModelData(identifier, entityResource, actualGrowth);
                    this.modelData.put(growth, loaded);
                    if (actualGrowth != growth) {
                        this.modelData.put(actualGrowth, loaded);
                    }
                }
            } catch (Exception e) {
                JurassiCraft.getLogger().fatal("Failed to parse growth stage " + growth + " for dinosaur " + identifier, e);
                this.modelData.put(growth, new ModelData());
            }
        }
    }

    private ModelData loadModelData(ResourceLocation identifier, ResourceLocation origin, GrowthStage growth) {
        String namespace = origin.getResourceDomain();
        String name = identifier.getResourcePath();
        ResourceLocation stageOrigin = new ResourceLocation(namespace, origin.getResourcePath() + "/" + growth.getKey());
        ResourceLocation definition = new ResourceLocation(namespace, stageOrigin.getResourcePath() + "/" + name + "_" + growth.getKey() + ".json");
        try (InputStream modelStream = TabulaModelHelper.class.getResourceAsStream("/assets/" + definition.getResourceDomain() + "/" + definition.getResourcePath())) {
            if (modelStream == null) {
                throw new IllegalArgumentException("No model definition for the dino " + identifier + " with grow-state " + growth + " exists. Expected at " + definition);
            }
            Reader reader = new InputStreamReader(modelStream);
            AnimationsDTO rawAnimations = GSON.fromJson(reader, AnimationsDTO.class);
            ModelData data = this.loadModelData(stageOrigin, rawAnimations);
            JurassiCraft.getLogger().debug("Successfully loaded " + identifier + "(" + growth + ") from " + definition);
            reader.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ModelData loadModelData(ResourceLocation origin, AnimationsDTO animationsDefinition) {
        if (animationsDefinition == null || animationsDefinition.poses == null
                || animationsDefinition.poses.get(EntityAnimation.IDLE.name()) == null
                || animationsDefinition.poses.get(EntityAnimation.IDLE.name()).length == 0) {
            throw new IllegalArgumentException("Animation files must define at least one pose for the IDLE animation");
        }
        List<ResourceLocation> posedModelResources = new ArrayList<>();
        for (PoseDTO[] poses : animationsDefinition.poses.values()) {
            if (poses == null) {
                continue;
            }
            for (PoseDTO pose : poses) {
                if (pose == null) {
                    continue;
                }
                if (pose.pose == null) {
                    throw new IllegalArgumentException("Every pose must define a pose file");
                }
                ResourceLocation resolvedRes = new ResourceLocation(origin.getResourceDomain(), origin.getResourcePath() + "/" + pose.pose);
                int index = posedModelResources.indexOf(resolvedRes);
                if (index == -1) {
                    pose.index = posedModelResources.size();
                    posedModelResources.add(resolvedRes);
                } else {
                    pose.index = index;
                }
            }
        }

        Map<Animation, float[][]> animations = new HashMap<>();

        for (Map.Entry<String, PoseDTO[]> entry : animationsDefinition.poses.entrySet()) {
            Animation animation = EntityAnimation.valueOf(entry.getKey()).get();
            PoseDTO[] poses = entry.getValue();
            float[][] poseSequence = new float[poses.length][2];
            for (int i = 0; i < poses.length; i++) {
                poseSequence[i][0] = poses[i].index;
                poseSequence[i][1] = poses[i].time;
            }
            animations.put(animation, poseSequence);
        }

        if (FMLCommonHandler.instance().getSide().isClient()) {
            return this.loadModelDataClient(posedModelResources, animations);
        }

        return new ModelData(animations);
    }

    @SideOnly(Side.CLIENT)
    private ModelData loadModelDataClient(List<ResourceLocation> posedModelResources, Map<Animation, float[][]> animations) {
        PosedCuboid[][] posedCuboids = new PosedCuboid[posedModelResources.size()][];
        AnimatableModel mainModel = JabelarAnimationHandler.loadModel(posedModelResources.get(0));
        if (mainModel == null) {
            throw new IllegalArgumentException("Couldn't load the model from " + posedModelResources.get(0));
        }
        String[] identifiers = mainModel.getCubeIdentifierArray();
        int partCount = identifiers.length;
        for (int i = 0; i < posedModelResources.size(); i++) {
            ResourceLocation resource = posedModelResources.get(i);
            AnimatableModel model = JabelarAnimationHandler.loadModel(resource);
            if (model == null) {
                throw new IllegalArgumentException("Couldn't load the model from " + resource);
            }
            PosedCuboid[] pose = new PosedCuboid[partCount];
            for (int partIndex = 0; partIndex < partCount; partIndex++) {
                String identifier = identifiers[partIndex];
                AdvancedModelRenderer cube = model.getCubeByIdentifier(identifier);
                if (cube == null) {
                    AdvancedModelRenderer mainCube = mainModel.getCubeByIdentifier(identifier);
                    JurassiCraft.getLogger().error("Could not retrieve cube " + identifier + " (" + mainCube.boxName + ", " + partIndex + ") from the model " + resource);
                    pose[partIndex] = new PosedCuboid(mainCube);
                } else {
                    pose[partIndex] = new PosedCuboid(cube);
                }
            }
            posedCuboids[i] = pose;
        }
        return new ModelData(posedCuboids, animations);
    }

    @SideOnly(Side.CLIENT)
    public JabelarAnimationHandler<ENTITY> createAnimationHandler(ENTITY entity, AnimatableModel model, GrowthStage growthStage, boolean useInertialTweens) {
        ModelData growthModel = this.modelData.get(growthStage);
        if (!entity.canUseGrowthStage(growthStage)) {
            growthModel = this.modelData.get(growthStage);
        }
        return new JabelarAnimationHandler<>(entity, model, growthModel.poses, growthModel.animations, useInertialTweens);
    }

    public Map<Animation, float[][]> getAnimations(GrowthStage growthStage) {
        return this.modelData.get(growthStage).animations;
    }

    public float getAnimationLength(Animation animation, GrowthStage growthStage) {
        Map<Animation, float[][]> animations = this.getAnimations(growthStage);

        float duration = 0;

        if (animation != null) {
            float[][] poses = animations.get(animation);

            if (poses != null) {
                for (float[] pose : poses) {
                    duration += pose[1];
                }
            }
        }

        return duration;
    }

    public boolean hasAnimation(Animation animation, GrowthStage growthStage) {
        return this.modelData.get(growthStage).animations.get(animation) != null;
    }

    private class ModelData {
        @SideOnly(Side.CLIENT)
        PosedCuboid[][] poses;

        Map<Animation, float[][]> animations;

        public ModelData() {
            this(null);
        }

        @SideOnly(Side.CLIENT)
        public ModelData(PosedCuboid[][] cuboids, Map<Animation, float[][]> animations) {
            this(animations);

            if (cuboids == null) {
                cuboids = new PosedCuboid[0][];
            }

            this.poses = cuboids;
        }

        public ModelData(Map<Animation, float[][]> animations) {
            if (animations == null) {
                animations = new LinkedHashMap<>();
            }

            this.animations = animations;
        }
    }
}
