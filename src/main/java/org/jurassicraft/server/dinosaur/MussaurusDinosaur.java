package org.jurassicraft.server.dinosaur;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.Diet;
import org.jurassicraft.server.entity.dinosaur.MussaurusEntity;
import org.jurassicraft.server.period.TimePeriod;

public class MussaurusDinosaur extends Dinosaur {
    @Override
    protected DinosaurMetadata buildMetadata() {
        return new DinosaurMetadata(new ResourceLocation(JurassiCraft.MODID, "mussaurus"))
                .setEntity(MussaurusEntity.class, MussaurusEntity::new)
                .setDinosaurType(DinosaurType.SCARED)
                .setFlee(true)
                .setTimePeriod(TimePeriod.TRIASSIC)
                .setEggColorMale(0x6F9845, 0x211F16)
                .setEggColorFemale(0x526024, 0x222611)
                .setHealth(2, 15)
                .setSpeed(0.25, 0.32)
                .setStrength(1, 2)
                .setMaximumAge(this.fromDays(30))
                .setEyeHeight(0.25F, 1.2F)
                .setSizeX(0.25F, 1F)
                .setSizeY(0.2F, 0.9F)
                .setStorage(9)
                .setDiet(Diet.HERBIVORE.get())
                .setBones("arm_bones", "leg_bones", "neck_vertebrae", "pelvis", "ribcage", "shoulder", "skull", "tail_vertebrae", "teeth")
                .setHeadCubeName("Head1")
                .setScale(0.6F, 0.1F)
                .setImprintable(true)
                .setFlockSpeed(1.25F)
                .setMaxHerdSize(20)
                .setAttackBias(-500.0)
                .setOffset(0.0F, 0.0F, 0.5F)
                .setBreeding(false, 2, 8, 15, false, true)
                .setRecipe(new String[][] {
                        { "", "pelvis", "", "", "" },
                        { "tail_vertebrae", "ribcage", "shoulder", "neck_vertebrae", "skull" },
                        { "leg_bones", "leg_bones", "arm_bones", "arm_bones", "teeth" }
                })
                .setSpawn(15, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.FOREST);
    }
}
