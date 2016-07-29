package org.jurassicraft.client.proxy;

import net.ilexiconn.llibrary.client.lang.LanguageHandler;
import net.ilexiconn.llibrary.server.util.WebUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.event.ClientEventHandler;
import org.jurassicraft.client.gui.CleaningStationGui;
import org.jurassicraft.client.gui.CultivateGui;
import org.jurassicraft.client.gui.CultivateProcessGui;
import org.jurassicraft.client.gui.DNACombinatorHybridizerGui;
import org.jurassicraft.client.gui.DNAExtractorGui;
import org.jurassicraft.client.gui.DNASequencerGui;
import org.jurassicraft.client.gui.DNASynthesizerGui;
import org.jurassicraft.client.gui.EmbryoCalcificationMachineGui;
import org.jurassicraft.client.gui.EmbryonicMachineGui;
import org.jurassicraft.client.gui.FeederGui;
import org.jurassicraft.client.gui.FieldGuideGui;
import org.jurassicraft.client.gui.FossilGrinderGui;
import org.jurassicraft.client.gui.IncubatorGui;
import org.jurassicraft.client.gui.OrderDinosaurGui;
import org.jurassicraft.client.gui.SelectDinoGui;
import org.jurassicraft.client.render.RenderingHandler;
import org.jurassicraft.server.block.entity.CleaningStationBlockEntity;
import org.jurassicraft.server.block.entity.CultivatorBlockEntity;
import org.jurassicraft.server.block.entity.DNACombinatorHybridizerBlockEntity;
import org.jurassicraft.server.block.entity.DNAExtractorBlockEntity;
import org.jurassicraft.server.block.entity.DNASequencerBlockEntity;
import org.jurassicraft.server.block.entity.DNASynthesizerBlockEntity;
import org.jurassicraft.server.block.entity.EmbryoCalcificationMachineBlockEntity;
import org.jurassicraft.server.block.entity.EmbryonicMachineBlockEntity;
import org.jurassicraft.server.block.entity.FeederBlockEntity;
import org.jurassicraft.server.block.entity.FossilGrinderBlockEntity;
import org.jurassicraft.server.block.entity.IncubatorBlockEntity;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.VenomEntity;
import org.jurassicraft.server.entity.particle.VenomParticle;
import org.jurassicraft.server.entity.vehicle.CarEntity;
import org.jurassicraft.server.proxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
    private static final Minecraft MC = Minecraft.getMinecraft();

    public static final List<UUID> PATRONS = new ArrayList<>();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        try {
            LanguageHandler.INSTANCE.loadRemoteLocalization(JurassiCraft.MODID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClientEventHandler eventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        RenderingHandler.INSTANCE.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        RenderingHandler.INSTANCE.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        RenderingHandler.INSTANCE.postInit();

        new Thread(() ->
        {
            List<String> patrons = WebUtils.readPastebinAsList("fgJQkCMa");

            if (patrons != null) {
                for (String patron : patrons) {
                    PATRONS.add(UUID.fromString(patron));
                }
            }
        }).start();
    }

    @Override
    public EntityPlayer getPlayer() {
        return MC.thePlayer;
    }

    @Override
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ? this.getPlayer() : super.getPlayerEntityFromContext(ctx));
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity != null) {
            if (tileEntity instanceof CleaningStationBlockEntity && id == GUI_CLEANING_STATION_ID) {
                return new CleaningStationGui(player.inventory, (CleaningStationBlockEntity) tileEntity);
            } else if (tileEntity instanceof FossilGrinderBlockEntity && id == GUI_FOSSIL_GRINDER_ID) {
                return new FossilGrinderGui(player.inventory, (FossilGrinderBlockEntity) tileEntity);
            } else if (tileEntity instanceof DNASequencerBlockEntity && id == GUI_DNA_SEQUENCER_ID) {
                return new DNASequencerGui(player.inventory, (DNASequencerBlockEntity) tileEntity);
            } else if (tileEntity instanceof EmbryonicMachineBlockEntity && id == GUI_EMBRYONIC_MACHINE_ID) {
                return new EmbryonicMachineGui(player.inventory, (EmbryonicMachineBlockEntity) tileEntity);
            } else if (tileEntity instanceof EmbryoCalcificationMachineBlockEntity && id == GUI_EMBRYO_CALCIFICATION_MACHINE_ID) {
                return new EmbryoCalcificationMachineGui(player.inventory, (EmbryoCalcificationMachineBlockEntity) tileEntity);
            } else if (tileEntity instanceof DNASynthesizerBlockEntity && id == GUI_DNA_SYNTHESIZER_ID) {
                return new DNASynthesizerGui(player.inventory, (DNASynthesizerBlockEntity) tileEntity);
            } else if (tileEntity instanceof IncubatorBlockEntity && id == GUI_INCUBATOR_ID) {
                return new IncubatorGui(player.inventory, (IncubatorBlockEntity) tileEntity);
            } else if (tileEntity instanceof DNACombinatorHybridizerBlockEntity && id == GUI_DNA_COMBINATOR_HYBRIDIZER_ID) {
                return new DNACombinatorHybridizerGui(player.inventory, (DNACombinatorHybridizerBlockEntity) tileEntity);
            } else if (tileEntity instanceof DNAExtractorBlockEntity && id == GUI_DNA_EXTRACTOR_ID) {
                return new DNAExtractorGui(player.inventory, (DNAExtractorBlockEntity) tileEntity);
            } else if (tileEntity instanceof CultivatorBlockEntity && id == GUI_CULTIVATOR_ID) {
                CultivatorBlockEntity cultivator = (CultivatorBlockEntity) tileEntity;

                if (cultivator.isProcessing(0)) {
                    return new CultivateProcessGui(cultivator);
                } else {
                    return new CultivateGui(player.inventory, cultivator);
                }
            } else if (tileEntity instanceof FeederBlockEntity && id == GUI_FEEDER_ID) {
                return new FeederGui(player.inventory, (FeederBlockEntity) tileEntity);
            }
        }

        return null;
    }

    @Override
    public void openSelectDino(BlockPos pos, EnumFacing facing, EnumHand hand) {
        MC.displayGuiScreen(new SelectDinoGui(pos, facing, hand));
    }

    @Override
    public void openOrderGui(DinosaurEntity entity) {
        MC.displayGuiScreen(new OrderDinosaurGui(entity));
    }

    @Override
    public void openFieldGuide(DinosaurEntity entity, DinosaurEntity.FieldGuideInfo fieldGuideInfo) {
        MC.displayGuiScreen(new FieldGuideGui(entity, fieldGuideInfo));
    }

    public static void playSound(ISound sound) {
        MC.getSoundHandler().playSound(sound);
    }

    public static void stopSound(ISound sound) {
        MC.getSoundHandler().stopSound(sound);
    }

    public static void playSound(CarEntity entity) {
        playSound(entity.sound);
    }

    public static void stopSound(CarEntity entity) {
        stopSound(entity.sound);
    }

    public static void spawnVenomParticles(VenomEntity entity) {
        ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;

        float size = 0.35F;

        for (int i = 0; i < 16; ++i) {
            particleManager.addEffect(new VenomParticle(entity.worldObj, size * Math.random() - size / 2, size * Math.random() - size / 2, size * Math.random() - size / 2, 0.0F, 0.0F, 0.0F, 1.0F, entity));
        }
    }
}
