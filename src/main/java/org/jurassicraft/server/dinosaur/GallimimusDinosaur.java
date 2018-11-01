package org.jurassicraft.server.dinosaur;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.Diet;
import org.jurassicraft.server.entity.dinosaur.GallimimusEntity;
import org.jurassicraft.server.food.FoodType;
import org.jurassicraft.server.period.TimePeriod;

public class GallimimusDinosaur extends Dinosaur {
    @Override
    protected DinosaurMetadata buildMetadata() {
        return new DinosaurMetadata(new ResourceLocation(JurassiCraft.MODID, "gallimimus"))
                .setEntity(GallimimusEntity.class, GallimimusEntity::new)
                .setDinosaurType(DinosaurType.SCARED)
                .setTimePeriod(TimePeriod.CRETACEOUS)
                .setEggColorMale(0xD5BA86, 0xD16918)
                .setEggColorFemale(0xCCBA94, 0xAB733D)
                .setHealth(5, 30)
                .setSpeed(0.2, 0.3)
                .setStrength(1, 5)
                .setMaximumAge(this.fromDays(35))
                .setEyeHeight(0.58F, 2.7F)
                .setSizeX(0.3F, 1.2F)
                .setSizeY(0.55F, 2.25F)
                .setStorage(27)
                .setDiet(Diet.HERBIVORE.get().withModule(new Diet.DietModule(FoodType.INSECT).withCondition(entity -> entity.getAgePercentage() < 25)))
                .setBones("skull", "tail_vertebrae", "shoulder", "ribcage", "pelvis", "neck_vertebrae", "leg_bones", "foot_bones", "arm_bones")
                .setHeadCubeName("Head Base")
                .setScale(0.85F, 0.2F)
                .setImprintable(true)
                .setFlee(true)
                .setFlockSpeed(1.4F)
                .setBreeding(false, 2, 6, 20, false, true)
                .setJumpHeight(3)
                .setSpawn(25, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.DRY)
                .setRecipe(new String[][] {
                        { "", "", "", "neck_vertebrae", "skull" },
                        { "tail_vertebrae", "pelvis", "ribcage", "shoulder", "" },
                        { "", "leg_bones", "leg_bones", "arm_bones", "" },
                        { "", "foot_bones", "foot_bones", "", "" } }
                );
    }
}
