package org.jurassicraft.client.render;

import com.google.common.collect.Maps;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.MultipartStateMap;
import org.jurassicraft.client.model.animation.EntityAnimator;
import org.jurassicraft.client.model.animation.entity.BrachiosaurusAnimator;
import org.jurassicraft.client.model.animation.entity.CoelacanthAnimator;
import org.jurassicraft.client.model.animation.entity.DilophosaurusAnimator;
import org.jurassicraft.client.model.animation.entity.GallimimusAnimator;
import org.jurassicraft.client.model.animation.entity.MicroraptorAnimator;
import org.jurassicraft.client.model.animation.entity.MussaurusAnimator;
import org.jurassicraft.client.model.animation.entity.ParasaurolophusAnimator;
import org.jurassicraft.client.model.animation.entity.TriceratopsAnimator;
import org.jurassicraft.client.model.animation.entity.TyrannosaurusAnimator;
import org.jurassicraft.client.model.animation.entity.VelociraptorAnimator;
import org.jurassicraft.client.render.block.CleaningStationRenderer;
import org.jurassicraft.client.render.block.DNAExtractorRenderer;
import org.jurassicraft.client.render.block.DNASequencerRenderer;
import org.jurassicraft.client.render.block.DisplayBlockRenderer;
import org.jurassicraft.client.render.block.ElectricFencePoleRenderer;
import org.jurassicraft.client.render.block.FeederRenderer;
import org.jurassicraft.client.render.block.IncubatorRenderer;
import org.jurassicraft.client.render.entity.AttractionSignRenderer;
import org.jurassicraft.client.render.entity.DinosaurEggRenderer;
import org.jurassicraft.client.render.entity.FordExplorerRenderer;
import org.jurassicraft.client.render.entity.GoatRenderer;
import org.jurassicraft.client.render.entity.HeliRenderer;
import org.jurassicraft.client.render.entity.JeepWranglerRenderer;
import org.jurassicraft.client.render.entity.MuralRenderer;
import org.jurassicraft.client.render.entity.NullRenderer;
import org.jurassicraft.client.render.entity.PaddockSignRenderer;
import org.jurassicraft.client.render.entity.VenomRenderer;
import org.jurassicraft.client.render.entity.dinosaur.DinosaurRenderInfo;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.EncasedFossilBlock;
import org.jurassicraft.server.block.FossilBlock;
import org.jurassicraft.server.block.FossilizedTrackwayBlock;
import org.jurassicraft.server.block.NestFossilBlock;
import org.jurassicraft.server.block.TourRailBlock;
import org.jurassicraft.server.block.entity.CleaningStationBlockEntity;
import org.jurassicraft.server.block.entity.DNAExtractorBlockEntity;
import org.jurassicraft.server.block.entity.DNASequencerBlockEntity;
import org.jurassicraft.server.block.entity.DisplayBlockEntity;
import org.jurassicraft.server.block.entity.ElectricFencePoleBlockEntity;
import org.jurassicraft.server.block.entity.FeederBlockEntity;
import org.jurassicraft.server.block.entity.IncubatorBlockEntity;
import org.jurassicraft.server.block.plant.AncientCoralBlock;
import org.jurassicraft.server.block.tree.AncientLeavesBlock;
import org.jurassicraft.server.block.tree.TreeType;
import org.jurassicraft.server.conf.JurassiCraftConfig;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.dinosaur.DinosaurMetadata;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.GoatEntity;
import org.jurassicraft.server.entity.TranquilizerDartEntity;
import org.jurassicraft.server.entity.VenomEntity;
import org.jurassicraft.server.entity.item.AttractionSignEntity;
import org.jurassicraft.server.entity.item.DinosaurEggEntity;
import org.jurassicraft.server.entity.item.MuralEntity;
import org.jurassicraft.server.entity.item.PaddockSignEntity;
import org.jurassicraft.server.entity.vehicle.FordExplorerEntity;
import org.jurassicraft.server.entity.vehicle.HelicopterEntity;
import org.jurassicraft.server.entity.vehicle.JeepWranglerEntity;
import org.jurassicraft.server.item.Dart;
import org.jurassicraft.server.item.DinosaurSpawnEggItem;
import org.jurassicraft.server.item.FossilItem;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.item.JournalItem;
import org.jurassicraft.server.plant.Plant;
import org.jurassicraft.server.plant.PlantHandler;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import static org.jurassicraft.server.block.BlockHandler.*;
import static org.jurassicraft.server.item.ItemHandler.*;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid=JurassiCraft.MODID, value = Side.CLIENT)
public enum RenderingHandler {
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();
    private static Map<Dinosaur, DinosaurRenderInfo> renderInfos = Maps.newHashMap();

    //TODO: CLEAN THIS UP OMG PLZ
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelEvent(final ModelRegistryEvent event)
    {
        for (EnumDyeColor color : EnumDyeColor.values()) {
            registerItemRenderer(Item.getItemFromBlock(CULTIVATOR_BOTTOM), color.getMetadata(), "cultivate/cultivate_bottom_" + color.getName().toLowerCase(Locale.ENGLISH));
        }

        for (TreeType type : TreeType.values()) {
            ModelLoader.setCustomStateMapper(ANCIENT_FENCES.get(type), new MultipartStateMap());
            ModelLoader.setCustomStateMapper(ANCIENT_FENCE_GATES.get(type), new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());
            ModelLoader.setCustomStateMapper(BlockHandler.ANCIENT_DOORS.get(type), new StateMap.Builder().ignore(BlockDoor.POWERED).build());
        }
        ModelLoader.setCustomStateMapper(ENALLHELIA, new StateMap.Builder().ignore(AncientCoralBlock.LEVEL).build());
        ModelLoader.setCustomStateMapper(AULOPORA, new StateMap.Builder().ignore(AncientCoralBlock.LEVEL).build());
        ModelLoader.setCustomStateMapper(CLADOCHONUS, new StateMap.Builder().ignore(AncientCoralBlock.LEVEL).build());
        ModelLoader.setCustomStateMapper(LITHOSTROTION, new StateMap.Builder().ignore(AncientCoralBlock.LEVEL).build());
        ModelLoader.setCustomStateMapper(STYLOPHYLLOPSIS, new StateMap.Builder().ignore(AncientCoralBlock.LEVEL).build());
        ModelLoader.setCustomStateMapper(HIPPURITES_RADIOSUS, new StateMap.Builder().ignore(AncientCoralBlock.LEVEL).build());

        ModelLoader.setCustomStateMapper(BlockHandler.LOW_SECURITY_FENCE_BASE, new MultipartStateMap());
        ModelLoader.setCustomStateMapper(BlockHandler.LOW_SECURITY_FENCE_POLE, new MultipartStateMap());
        ModelLoader.setCustomStateMapper(BlockHandler.LOW_SECURITY_FENCE_WIRE, new MultipartStateMap());
//        ModelLoader.setCustomStateMapper(BlockHandler.MED_SECURITY_FENCE_BASE, new MultipartStateMap());
//        ModelLoader.setCustomStateMapper(BlockHandler.MED_SECURITY_FENCE_POLE, new MultipartStateMap());
//        ModelLoader.setCustomStateMapper(BlockHandler.MED_SECURITY_FENCE_WIRE, new MultipartStateMap());
//        ModelLoader.setCustomStateMapper(BlockHandler.HIGH_SECURITY_FENCE_BASE, new MultipartStateMap());
//        ModelLoader.setCustomStateMapper(BlockHandler.HIGH_SECURITY_FENCE_POLE, new MultipartStateMap());
//        ModelLoader.setCustomStateMapper(BlockHandler.HIGH_SECURITY_FENCE_WIRE, new MultipartStateMap());

        int i = 0;

        for (EncasedFossilBlock fossil : ENCASED_FOSSILS) {
            for (int meta = 0; meta < 16; meta++) {
                registerBlockRenderer(fossil, meta, "encased_fossil_" + i);
            }

            i++;
        }

        i = 0;

        for (FossilBlock fossil : BlockHandler.FOSSILS) {
            for (int meta = 0; meta < 16; meta++) {
                registerBlockRenderer(fossil, meta, "fossil_block_" + i);
            }

            i++;
        }

        registerBlockRenderer(BlockHandler.PLANT_FOSSIL, "plant_fossil_block");

        for (TreeType type : TreeType.values()) {
            String name = type.name().toLowerCase(Locale.ENGLISH);
            registerBlockRenderer(ANCIENT_LEAVES.get(type), name + "_leaves");
            registerBlockRenderer(ANCIENT_SAPLINGS.get(type), name + "_sapling");
            registerBlockRenderer(ANCIENT_PLANKS.get(type), name + "_planks");
            registerBlockRenderer(ANCIENT_LOGS.get(type), name + "_log");
            registerBlockRenderer(ANCIENT_STAIRS.get(type), name + "_stairs");
            registerBlockRenderer(ANCIENT_SLABS.get(type), name + "_slab");
            registerBlockRenderer(ANCIENT_DOUBLE_SLABS.get(type), name + "_double_slab");
            registerBlockRenderer(ANCIENT_FENCES.get(type), name + "_fence");
            registerBlockRenderer(ANCIENT_FENCE_GATES.get(type), name + "_fence_gate");
            registerBlockRenderer(PETRIFIED_LOGS.get(type), name + "_log_petrified");
        }

        /*for (EnumDyeColor color : EnumDyeColor.values()) {
            registerBlockRenderer(CULTIVATOR_BOTTOM, color.ordinal(), "cultivate/cultivate_bottom_" + color.getName().toLowerCase(Locale.ENGLISH));
        }*/

        registerBlockRenderer(SCALY_TREE_FERN, "scaly_tree_fern");
        registerBlockRenderer(SMALL_ROYAL_FERN, "small_royal_fern");
        registerBlockRenderer(SMALL_CHAIN_FERN, "small_chain_fern");
        registerBlockRenderer(SMALL_CYCAD, "small_cycad");
        registerBlockRenderer(CYCADEOIDEA, "bennettitalean_cycadeoidea");
        registerBlockRenderer(CRY_PANSY, "cry_pansy");
        registerBlockRenderer(ZAMITES, "cycad_zamites");
        registerBlockRenderer(DICKSONIA, "dicksonia");
        registerBlockRenderer(WOOLLY_STALKED_BEGONIA, "woolly_stalked_begonia");
        registerBlockRenderer(LARGESTIPULE_LEATHER_ROOT, "largestipule_leather_root");
        registerBlockRenderer(RHACOPHYTON, "rhacophyton");
        registerBlockRenderer(GRAMINIDITES_BAMBUSOIDES, "graminidites_bambusoides");
        registerBlockRenderer(ENALLHELIA, "enallhelia");
        registerBlockRenderer(AULOPORA, "aulopora");
        registerBlockRenderer(CLADOCHONUS, "cladochonus");
        registerBlockRenderer(LITHOSTROTION, "lithostrotion");
        registerBlockRenderer(STYLOPHYLLOPSIS, "stylophyllopsis");
        registerBlockRenderer(HIPPURITES_RADIOSUS, "hippurites_radiosus");
        registerBlockRenderer(HELICONIA, "heliconia");

        registerBlockRenderer(REINFORCED_STONE, "reinforced_stone");
        registerBlockRenderer(REINFORCED_BRICKS, "reinforced_bricks");

        registerBlockRenderer(CULTIVATOR_BOTTOM, "cultivate_bottom");
        registerBlockRenderer(CULTIVATOR_TOP, "cultivate_top");

        registerBlockRenderer(AMBER_ORE, "amber_ore");
        registerBlockRenderer(ICE_SHARD, "ice_shard");
        registerBlockRenderer(CLEANING_STATION, "cleaning_station");
        registerBlockRenderer(FOSSIL_GRINDER, "fossil_grinder");
        registerBlockRenderer(DNA_SEQUENCER, "dna_sequencer");
        registerBlockRenderer(DNA_COMBINATOR_HYBRIDIZER, "dna_combinator_hybridizer");
        registerBlockRenderer(DNA_SYNTHESIZER, "dna_synthesizer");
        registerBlockRenderer(EMBRYONIC_MACHINE, "embryonic_machine");
        registerBlockRenderer(EMBRYO_CALCIFICATION_MACHINE, "embryo_calcification_machine");
        registerBlockRenderer(INCUBATOR, "incubator");
        registerBlockRenderer(DNA_EXTRACTOR, "dna_extractor");
        registerBlockRenderer(FEEDER, "feeder");
        registerBlockRenderer(SKELETON_ASSEMBLY, "skeleton_assembly");
        registerBlockRenderer(GYPSUM_STONE, "gypsum_stone");
        registerBlockRenderer(GYPSUM_COBBLESTONE, "gypsum_cobblestone");
        registerBlockRenderer(GYPSUM_BRICKS, "gypsum_bricks");
        registerBlockRenderer(BlockHandler.DISPLAY_BLOCK, "display_block");

        registerBlockRenderer(MOSS, "moss");
        registerBlockRenderer(CLEAR_GLASS, "clear_glass");

        registerBlockRenderer(BlockHandler.WILD_ONION, "wild_onion_plant");
        registerBlockRenderer(BlockHandler.GRACILARIA, "gracilaria_seaweed");
        registerBlockRenderer(PEAT, "peat");
        registerBlockRenderer(PEAT_MOSS, "peat_moss");
        registerBlockRenderer(DICROIDIUM_ZUBERI, "dicroidium_zuberi");
        registerBlockRenderer(WEST_INDIAN_LILAC, "west_indian_lilac");
        registerBlockRenderer(DICTYOPHYLLUM, "dictyophyllum");
        registerBlockRenderer(SERENNA_VERIFORMANS, "serenna_veriformans");
        registerBlockRenderer(LADINIA_SIMPLEX, "ladinia_simplex");
        registerBlockRenderer(ORONTIUM_MACKII, "orontium_mackii");
        registerBlockRenderer(UMALTOLEPIS, "umaltolepis");
        registerBlockRenderer(LIRIODENDRITES, "liriodendrites");
        registerBlockRenderer(RAPHAELIA, "raphaelia");
        registerBlockRenderer(ENCEPHALARTOS, "encephalartos");

        for (FossilizedTrackwayBlock.TrackwayType trackwayType : FossilizedTrackwayBlock.TrackwayType.values()) {
            registerBlockRenderer(FOSSILIZED_TRACKWAY, trackwayType.ordinal(), "fossilized_trackway_" + trackwayType.getName());
        }

        for (NestFossilBlock.Variant variant : NestFossilBlock.Variant.values()) {
            registerBlockRenderer(NEST_FOSSIL, variant.ordinal(), "nest_fossil_" + (variant.ordinal() + 1));
            registerBlockRenderer(ENCASED_NEST_FOSSIL, variant.ordinal(), "encased_nest_fossil");
        }

        registerBlockRenderer(PALEO_BALE_CYCADEOIDEA, "paleo_bale_cycadeoidea");
        registerBlockRenderer(PALEO_BALE_CYCAD, "paleo_bale_cycad");
        registerBlockRenderer(PALEO_BALE_FERN, "paleo_bale_fern");
        registerBlockRenderer(PALEO_BALE_LEAVES, "paleo_bale_leaves");
        registerBlockRenderer(PALEO_BALE_OTHER, "paleo_bale_other");

        registerBlockRenderer(AJUGINUCULA_SMITHII);
        registerBlockRenderer(BUG_CRATE);

        registerBlockRenderer(PLANKTON_SWARM);
        registerBlockRenderer(KRILL_SWARM);

        registerBlockRenderer(BlockHandler.LOW_SECURITY_FENCE_POLE);
        registerBlockRenderer(BlockHandler.LOW_SECURITY_FENCE_BASE);
        registerBlockRenderer(BlockHandler.LOW_SECURITY_FENCE_WIRE);
//        registerBlockRenderer(BlockHandler.MED_SECURITY_FENCE_POLE);
//        registerBlockRenderer(BlockHandler.MED_SECURITY_FENCE_BASE);
//        registerBlockRenderer(BlockHandler.MED_SECURITY_FENCE_WIRE);
//        registerBlockRenderer(BlockHandler.HIGH_SECURITY_FENCE_POLE);
//        registerBlockRenderer(BlockHandler.HIGH_SECURITY_FENCE_BASE);
//        registerBlockRenderer(BlockHandler.HIGH_SECURITY_FENCE_WIRE);

        registerBlockRenderer(WILD_POTATO_PLANT);

        registerBlockRenderer(RHAMNUS_SALICIFOLIUS_PLANT);

        registerBlockRenderer(TEMPSKYA);
        registerBlockRenderer(CINNAMON_FERN);
        registerBlockRenderer(BRISTLE_FERN);

        registerBlockRenderer(TOUR_RAIL, "tour_rail.tbl_jurassicraft");
        registerBlockRenderer(TOUR_RAIL_SLOW, "tour_rail_stripe.tbl_jurassicraft");
        registerBlockRenderer(TOUR_RAIL_MEDIUM, "tour_rail_stripe.tbl_jurassicraft");
        registerBlockRenderer(TOUR_RAIL_FAST, "tour_rail_stripe.tbl_jurassicraft");

        registerItemRenderer(TRACKER);
        registerItemRenderer(PLANT_CELLS_PETRI_DISH);
        registerItemRenderer(HELICOPTER);
        registerItemRenderer(PLANT_CELLS);
        registerItemRenderer(GROWTH_SERUM);
        registerItemRenderer(BREEDING_WAND);
        registerItemRenderer(BIRTHING_WAND);
        registerItemRenderer(PREGNANCY_TEST);
        registerItemRenderer(IRON_ROD);
        registerItemRenderer(IRON_BLADES);
        registerItemRenderer(PETRI_DISH);
        registerItemRenderer(PETRI_DISH_AGAR);
        registerItemRenderer(PLASTER_AND_BANDAGE);

        registerItemRenderer(FUN_FRIES);
        registerItemRenderer(OILED_POTATO_STRIPS);
        registerItemRenderer(LUNCH_BOX);
        registerItemRenderer(STAMP_SET);

        registerItemRenderer(INGEN_JOURNAL);

        for (Entry<Integer, Dinosaur> entry : EntityHandler.getDinosaurs().entrySet()) {
            registerItemRenderer(SPAWN_EGG, entry.getKey(), "dino_spawn_egg");
        }

        registerItemRenderer(PADDOCK_SIGN);

        for (AttractionSignEntity.AttractionSignType type : AttractionSignEntity.AttractionSignType.values()) {
            registerItemRenderer(ATTRACTION_SIGN, type.ordinal(), "attraction_sign_" + type.name().toLowerCase(Locale.ENGLISH));
        }

        registerItemRenderer(EMPTY_TEST_TUBE);
        registerItemRenderer(EMPTY_SYRINGE);
        registerItemRenderer(STORAGE_DISC);
        registerItemRenderer(DISC_DRIVE, "disc_reader");
        registerItemRenderer(LASER);
        registerItemRenderer(DNA_NUCLEOTIDES, "dna_base_material");
        registerItemRenderer(SEA_LAMPREY);

        registerItemRenderer(AMBER, 0, "amber_mosquito");
        registerItemRenderer(AMBER, 1, "amber_aphid");

        //registerItemRenderer(HELICOPTER, "helicopter_spawner");

        registerItemRenderer(JURASSICRAFT_THEME_DISC, "disc_jurassicraft_theme");
        registerItemRenderer(DONT_MOVE_A_MUSCLE_DISC, "disc_dont_move_a_muscle");
        registerItemRenderer(TROODONS_AND_RAPTORS_DISC, "disc_troodons_and_raptors");

        registerItemRenderer(AMBER_KEYCHAIN, "amber_keychain");
        registerItemRenderer(AMBER_CANE, "amber_cane");
        registerItemRenderer(MR_DNA_KEYCHAIN, "mr_dna_keychain");

        registerItemRenderer(DINO_SCANNER, "dino_scanner");

        registerItemRenderer(BASIC_CIRCUIT, "basic_circuit");
        registerItemRenderer(ADVANCED_CIRCUIT, "advanced_circuit");

        registerItemRenderer(GYPSUM_POWDER, "gypsum_powder");

        registerItemRenderer(AJUGINUCULA_SMITHII_SEEDS, "ajuginucula_smithii_seeds");
        registerItemRenderer(AJUGINUCULA_SMITHII_LEAVES, "ajuginucula_smithii_leaves");
        registerItemRenderer(AJUGINUCULA_SMITHII_OIL, "ajuginucula_smithii_oil");

        registerItemRenderer(ItemHandler.WILD_ONION, "wild_onion");
        registerItemRenderer(ItemHandler.GRACILARIA);
        registerItemRenderer(LIQUID_AGAR, "liquid_agar");

        registerItemRenderer(ItemHandler.PLANT_FOSSIL, "plant_fossil");
        registerItemRenderer(TWIG_FOSSIL, "twig_fossil");

        registerItemRenderer(KEYBOARD, "keyboard");
        registerItemRenderer(COMPUTER_SCREEN, "computer_screen");
        registerItemRenderer(DNA_ANALYZER, "dna_analyzer");

        registerItemRenderer(CHILEAN_SEA_BASS, "chilean_sea_bass");
        registerItemRenderer(FIELD_GUIDE, "field_guide");

        registerItemRenderer(CAR_CHASSIS, "car_chassis");
        registerItemRenderer(ENGINE_SYSTEM, "engine_system");
        registerItemRenderer(CAR_SEATS, "car_seats");
        registerItemRenderer(CAR_TIRE, "car_tire");
        registerItemRenderer(CAR_WINDSCREEN, "car_windscreen");
        registerItemRenderer(UNFINISHED_CAR, "unfinished_car");
        registerItemRenderer(JEEP_WRANGLER, "jeep_wrangler");
        registerItemRenderer(FORD_EXPLORER, "ford_explorer");

        registerItemRenderer(MURAL, "mural");

        for (Dinosaur dinosaur : EntityHandler.getDinosaurs().values()) {
            int meta = EntityHandler.getDinosaurId(dinosaur);

            String formattedName = dinosaur.getIdentifier().getResourcePath();

            for (Map.Entry<String, FossilItem> entry : ItemHandler.FOSSILS.entrySet()) {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());
                if (dinosaursForType.contains(dinosaur)) {
                    registerItemRenderer(entry.getValue(), meta, "bones/" + formattedName + "_" + entry.getKey());
                }
            }

            for (Map.Entry<String, FossilItem> entry : FRESH_FOSSILS.entrySet()) {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());
                if (dinosaursForType.contains(dinosaur)) {
                    registerItemRenderer(entry.getValue(), meta, "fresh_bones/" + formattedName + "_" + entry.getKey());
                }
            }

            registerItemRenderer(DNA, meta, "dna/dna_" + formattedName);
            registerItemRenderer(DINOSAUR_MEAT, meta, "meat/meat_" + formattedName);
            registerItemRenderer(DINOSAUR_STEAK, meta, "meat/steak_" + formattedName);
            registerItemRenderer(SOFT_TISSUE, meta, "soft_tissue/soft_tissue_" + formattedName);
            registerItemRenderer(SYRINGE, meta, "syringe/syringe_" + formattedName);
            //registerItemRenderer(ACTION_FIGURE, meta, "action_figure/action_figure_" + formattedName);

            if (!dinosaur.getMetadata().givesDirectBirth()) {
                registerItemRenderer(EGG, meta, "egg/egg_" + formattedName);
            }

            registerItemRenderer(HATCHED_EGG, meta, "hatched_egg/egg_" + formattedName);
        }

        for (Plant plant : PlantHandler.getPrehistoricPlantsAndTrees()) {
            int meta = PlantHandler.getPlantId(plant);

            String name = plant.getName().toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");

            registerItemRenderer(PLANT_DNA, meta, "dna/plants/dna_" + name);
            registerItemRenderer(PLANT_SOFT_TISSUE, meta, "soft_tissue/plants/soft_tissue_" + name);
            registerItemRenderer(PLANT_CALLUS, meta, "plant_callus");
        }

        for (JournalItem.JournalType type : JournalItem.JournalType.values()) {
            registerItemRenderer(INGEN_JOURNAL, type.getMetadata(), "ingen_journal");
        }

        for (NestFossilBlock.Variant variant : NestFossilBlock.Variant.values()) {
            registerItemRenderer(FOSSILIZED_EGG, variant.ordinal(), "fossilized_egg_" + (variant.ordinal() + 1));
        }

        for (TreeType type : TreeType.values()) {
            String name = type.name().toLowerCase(Locale.ENGLISH);
            registerItemRenderer(ItemHandler.ANCIENT_DOORS.get(type), name + "_door_item");
        }

        registerItemRenderer(PHOENIX_SEEDS);
        registerItemRenderer(PHOENIX_FRUIT);

        registerItemRenderer(CRICKETS);
        registerItemRenderer(COCKROACHES);
        registerItemRenderer(MEALWORM_BEETLES);

        registerItemRenderer(FINE_NET);
        registerItemRenderer(PLANKTON);
        registerItemRenderer(KRILL);

        registerItemRenderer(WILD_POTATO_SEEDS);
        registerItemRenderer(WILD_POTATO);
        registerItemRenderer(WILD_POTATO_COOKED);

        registerItemRenderer(RHAMNUS_SEEDS);
        registerItemRenderer(RHAMNUS_BERRIES);

        registerItemRenderer(GOAT_RAW);
        registerItemRenderer(GOAT_COOKED);
        
        registerItemRenderer(DART_GUN);
        registerItemRenderer(DART_TRANQUILIZER, "dart_colored");
        registerItemRenderer(DART_POISON_CYCASIN, "dart_colored");
        registerItemRenderer(DART_POISON_EXECUTIONER_CONCOCTION, "dart_colored");
        registerItemRenderer(DART_TIPPED_POTION, "dart_colored");

        registerItemRenderer(WEST_INDIAN_LILAC_BERRIES);
    }

    public void preInit() {
        TabulaModelHandler.INSTANCE.addDomain(JurassiCraft.MODID);
        ItemHandler.DISPLAY_BLOCK.initModels(EntityHandler.getDinosaurs().values(), this);

        registerRenderInfo(EntityHandler.BRACHIOSAURUS, new BrachiosaurusAnimator(), 1.5F);
        registerRenderInfo(EntityHandler.COELACANTH, new CoelacanthAnimator(), 0.0F);
//        registerRenderInfo(EntityHandler.ALLIGATORGAR, new AlligatorGarAnimator(), 0.0F);
        registerRenderInfo(EntityHandler.DILOPHOSAURUS, new DilophosaurusAnimator(), 0.65F);
        registerRenderInfo(EntityHandler.GALLIMIMUS, new GallimimusAnimator(), 0.65F);
        registerRenderInfo(EntityHandler.PARASAUROLOPHUS, new ParasaurolophusAnimator(), 0.65F);
        registerRenderInfo(EntityHandler.MICRORAPTOR, new MicroraptorAnimator(), 0.45F);
        registerRenderInfo(EntityHandler.MUSSAURUS, new MussaurusAnimator(), 0.8F);
        registerRenderInfo(EntityHandler.TRICERATOPS, new TriceratopsAnimator(), 0.65F);
        registerRenderInfo(EntityHandler.TYRANNOSAURUS, new TyrannosaurusAnimator(), 0.65F);
        registerRenderInfo(EntityHandler.VELOCIRAPTOR, new VelociraptorAnimator(), 0.45F);
        //registerRenderInfo(EntityHandler.STEGOSAURUS, new StegosaurusAnimator(), 0.65F);

        RenderingRegistry.registerEntityRenderingHandler(PaddockSignEntity.class, new PaddockSignRenderer());
        RenderingRegistry.registerEntityRenderingHandler(AttractionSignEntity.class, new AttractionSignRenderer());
        RenderingRegistry.registerEntityRenderingHandler(DinosaurEggEntity.class, new DinosaurEggRenderer());
        RenderingRegistry.registerEntityRenderingHandler(VenomEntity.class, new VenomRenderer());
        RenderingRegistry.registerEntityRenderingHandler(JeepWranglerEntity.class, JeepWranglerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FordExplorerEntity.class, FordExplorerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(HelicopterEntity.class, HeliRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MuralEntity.class, MuralRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GoatEntity.class, GoatRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TranquilizerDartEntity.class, NullRenderer::new);
        
    }

    public void init() {
        BlockColors blockColors = mc.getBlockColors();
        blockColors.registerBlockColorHandler((state, access, pos, tintIndex) -> pos != null ? BiomeColorHelper.getGrassColorAtPos(access, pos) : 0xFFFFFF, MOSS);

        for (Map.Entry<TreeType, AncientLeavesBlock> entry : ANCIENT_LEAVES.entrySet()) {
            blockColors.registerBlockColorHandler((state, access, pos, tintIndex) -> pos == null ? ColorizerFoliage.getFoliageColorBasic() : BiomeColorHelper.getFoliageColorAtPos(access, pos), entry.getValue());
        }

        blockColors.registerBlockColorHandler((state, access, pos, tintIndex) -> pos == null ? ColorizerFoliage.getFoliageColorBasic() : BiomeColorHelper.getFoliageColorAtPos(access, pos), MOSS);
        if(JurassiCraftConfig.VEHICLES.tourRailBlockEnabled)
            blockColors.registerBlockColorHandler((state, access, pos, tintIndex) -> tintIndex == 1 ? ((TourRailBlock)state.getBlock()).getSpeedType().getColor() : -1, BlockHandler.TOUR_RAIL_SLOW, BlockHandler.TOUR_RAIL_MEDIUM, BlockHandler.TOUR_RAIL_FAST);

        ItemColors itemColors = mc.getItemColors();
        if(JurassiCraftConfig.VEHICLES.tourRailBlockEnabled)
            itemColors.registerItemColorHandler((stack, tintIndex) -> tintIndex == 1 ? ((TourRailBlock)((ItemBlock)stack.getItem()).getBlock()).getSpeedType().getColor() : -1, BlockHandler.TOUR_RAIL_SLOW, BlockHandler.TOUR_RAIL_MEDIUM, BlockHandler.TOUR_RAIL_FAST);

        for (Map.Entry<TreeType, AncientLeavesBlock> entry : ANCIENT_LEAVES.entrySet()) {
            itemColors.registerItemColorHandler((stack, tintIndex) -> ColorizerFoliage.getFoliageColorBasic(), entry.getValue());
        }

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            DinosaurSpawnEggItem item = (DinosaurSpawnEggItem) stack.getItem();
            Dinosaur dino = item.getDinosaur(stack);
            if (dino != null) {
                DinosaurMetadata metadata = dino.getMetadata();
                int mode = item.getMode(stack);
                if (mode == 0) {
                    mode = JurassiCraft.timerTicks % 64 > 32 ? 1 : 2;
                }
                if (mode == 1) {
                    return tintIndex == 0 ? metadata.getEggPrimaryColorMale() : metadata.getEggSecondaryColorMale();
                } else {
                    return tintIndex == 0 ? metadata.getEggPrimaryColorFemale() : metadata.getEggSecondaryColorFemale();
                }
            }
            return 0xFFFFFF;
        }, SPAWN_EGG);

        itemColors.registerItemColorHandler(((stack, tintIndex) -> tintIndex == 1 ? ((Dart)stack.getItem()).getDartColor(stack) : -1), DART_POISON_CYCASIN, DART_POISON_EXECUTIONER_CONCOCTION, DART_TIPPED_POTION, DART_TRANQUILIZER);
    }

    public void postInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(DNAExtractorBlockEntity.class, new DNAExtractorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DisplayBlockEntity.class, new DisplayBlockRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DNASequencerBlockEntity.class, new DNASequencerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(IncubatorBlockEntity.class, new IncubatorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(FeederBlockEntity.class, new FeederRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ElectricFencePoleBlockEntity.class, new ElectricFencePoleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CleaningStationBlockEntity.class, new CleaningStationRenderer());
//        ClientRegistry.bindTileEntitySpecialRenderer(CultivatorBlockEntity.class, new CultivatorRenderer());
    }

    public static void registerItemRenderer(Item item) {
        registerItemRenderer(item, item.getUnlocalizedName().substring("item.".length()));
    }

    public static void registerBlockRenderer(Block block) {
        registerBlockRenderer(block, block.getUnlocalizedName().substring("tile.".length()));
    }

    public static void registerItemRenderer(Item item, int meta, String path) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(JurassiCraft.MODID + ":" + path, "inventory"));
    }

    public static void registerItemRenderer(Item item, String path) {
        registerItemRenderer(item, 0, path);
    }

    public static void registerBlockRenderer(Block block, int meta, String path) {
        registerItemRenderer(Item.getItemFromBlock(block), meta, path);
    }

    public static void registerBlockRenderer(Block block, String path) {
        registerBlockRenderer(block, 0, path);
    }

    private static void registerRenderInfo(Dinosaur dinosaur, EntityAnimator<?> animator, float shadowSize) {
        registerRenderInfo(new DinosaurRenderInfo(dinosaur, animator, shadowSize));
    }

    private static void registerRenderInfo(DinosaurRenderInfo renderInfo) {
        renderInfos.put(renderInfo.getDinosaur(), renderInfo);
        DinosaurMetadata metadata = renderInfo.getDinosaur().getMetadata();
        RenderingRegistry.registerEntityRenderingHandler(metadata.getDinosaurClass(), renderInfo);
    }

    public DinosaurRenderInfo getRenderInfo(Dinosaur dino) {
        return renderInfos.get(dino);
    }
}
