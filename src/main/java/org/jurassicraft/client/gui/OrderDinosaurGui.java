package org.jurassicraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.util.LangHelper;

import java.util.Locale;

@SideOnly(Side.CLIENT)
public class OrderDinosaurGui extends GuiScreen {
    private DinosaurEntity entity;

    public OrderDinosaurGui(DinosaurEntity entity) {
        this.entity = entity;
    }

    @Override
    public void initGui() {
        super.initGui();

        int i = 0;

        for (DinosaurEntity.Order order : DinosaurEntity.Order.values()) {
            GuiButton button = new GuiButton(i, this.width / 2 - 100, i * 30 + this.height / 4, I18n.translateToLocal("order." + order.name().toLowerCase(Locale.ENGLISH) + ".name"));
            button.enabled = order != this.entity.getOrder();

            this.buttonList.add(button);

            i++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.mc.fontRendererObj, new LangHelper("gui.select_order.name").build(), this.width / 2, this.height / 16, 0xFFFFFF);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        DinosaurEntity.Order order = DinosaurEntity.Order.values()[button.id];
        this.entity.setOrder(order);

        this.mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
