package org.jurassicraft.server.dinosaur;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.Diet;
import org.jurassicraft.server.entity.dinosaur.TyrannosaurusEntity;
import org.jurassicraft.server.period.TimePeriod;

public class TyrannosaurusDinosaur extends Dinosaur {
    @Override
    protected DinosaurMetadata buildMetadata() {
        return new DinosaurMetadata(new ResourceLocation(JurassiCraft.MODID, "tyrannosaurus"))
                .setEntity(TyrannosaurusEntity.class, TyrannosaurusEntity::new)
                .setDinosaurType(DinosaurType.AGGRESSIVE)
                .setTimePeriod(TimePeriod.CRETACEOUS)
                .setEggColorMale(0x4E502C, 0x353731)
                .setEggColorFemale(0xBA997E, 0x7D5D48)
                .setHealth(10, 80)
                .setSpeed(0.35, 0.42)
                .setAttackSpeed(1.1)
                .setStrength(5, 20)
                .setMaximumAge(this.fromDays(60))
                .setEyeHeight(0.6F, 3.8F)
                .setSizeX(0.45F, 3.0F)
                .setSizeY(0.8F, 4.0F)
                .setStorage(54)
                .setDiet(Diet.CARNIVORE.get())
                .setBones("arm_bones", "foot_bones", "leg_bones", "neck_vertebrae", "pelvis", "ribcage", "shoulder_bone", "skull", "tail_vertebrae", "tooth")
                .setHeadCubeName("Head")
                .setScale(2.4F, 0.35F)
                .setMaxHerdSize(3)
                .setAttackBias(1000.0)
                .setBreeding(false, 2, 4, 60, false, true)
                .setSpawn(5, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.FOREST)
                .setRecipe(new String[][] {
                        { "", "", "", "neck_vertebrae", "skull" },
                        { "tail_vertebrae", "pelvis", "ribcage", "shoulder_bone", "tooth" },
                        { "", "leg_bones", "leg_bones", "arm_bones", "" },
                        { "", "foot_bones", "foot_bones", "", "" } });
    }
}
