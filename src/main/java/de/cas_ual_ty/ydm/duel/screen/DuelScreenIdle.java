package de.cas_ual_ty.ydm.duel.screen;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.clientutil.ScreenUtil;
import de.cas_ual_ty.ydm.clientutil.widget.ReadyCheckboxWidget;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.screen.widget.RoleButtonWidget;
import de.cas_ual_ty.ydm.duel.screen.widget.RoleOccupantsWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

public class DuelScreenIdle<E extends DuelContainer> extends DuelContainerScreen<E>
{
    protected AbstractButton player1Button;
    protected AbstractButton player2Button;
    protected AbstractButton spectatorButton;
    
    public DuelScreenIdle(E screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        int x = width / 2;
        int y = height / 2;
        
        //        this.initChat(width, height, y, margin - 4*2, 3 * 32);
        initDefaultChat(width, height);
        
        addRenderableWidget(player1Button = new RoleButtonWidget(x - 100, y - 40, 100, 20, Component.translatable("container." + YDM.MOD_ID + ".duel.player_1"), this::roleButtonClicked, () -> getDuelManager().player1 == null && getPlayerRole() != PlayerRole.PLAYER1, PlayerRole.PLAYER1));
        addRenderableWidget(player2Button = new RoleButtonWidget(x - 100, y - 10, 100, 20, Component.translatable("container." + YDM.MOD_ID + ".duel.player_2"), this::roleButtonClicked, () -> getDuelManager().player2 == null && getPlayerRole() != PlayerRole.PLAYER2, PlayerRole.PLAYER2));
        addRenderableWidget(spectatorButton = new RoleButtonWidget(x - 100, y + 20, 100, 20, Component.translatable("container." + YDM.MOD_ID + ".duel.spectators"), this::roleButtonClicked, () -> getPlayerRole() != PlayerRole.SPECTATOR, PlayerRole.SPECTATOR));
        addRenderableWidget(new RoleOccupantsWidget(x, y - 40, 80, 20, this::getRoleDescription, PlayerRole.PLAYER1));
        addRenderableWidget(new RoleOccupantsWidget(x, y - 10, 80, 20, this::getRoleDescription, PlayerRole.PLAYER2));
        addRenderableWidget(new RoleOccupantsWidget(x, y + 20, 100, 20, this::getRoleDescription, PlayerRole.SPECTATOR));
        addRenderableWidget(new ReadyCheckboxWidget(x + 80, y - 40, 20, 20,  () -> getDuelManager().player1Ready, () -> getPlayerRole() == PlayerRole.PLAYER1 && getDuelManager().player2 != null, (button) -> ready1ButtonClicked()));
        addRenderableWidget(new ReadyCheckboxWidget(x + 80, y - 10, 20, 20,  () -> getDuelManager().player2Ready, () -> getPlayerRole() == PlayerRole.PLAYER2 && getDuelManager().player1 != null, (button) -> ready2ButtonClicked()));
    }
    
    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY)
    {
        gui.drawString(font, "Waiting for players...", 8, 6, 0x404040, false);
    }
    
    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(gui, partialTicks, mouseX, mouseY);
        
        ScreenUtil.white();
        gui.blit(DuelContainerScreen.DUEL_BACKGROUND_GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
    
    public Component getRoleDescription(PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            return Component.literal(getDuelManager().player1 != null ? getDuelManager().player1.getScoreboardName() : "");
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return Component.literal(getDuelManager().player2 != null ? getDuelManager().player2.getScoreboardName() : "");
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            int size = getDuelManager().spectators.size();
            
            if(getPlayerRole() == PlayerRole.SPECTATOR)
            {
                if(size == 1)
                {
                    return Component.literal(ClientProxy.getPlayer().getScoreboardName());
                }
                else
                {
                    return Component.literal(ClientProxy.getPlayer().getScoreboardName() + " + " + (size - 1));
                }
            }
            else
            {
                return Component.literal("" + size);
            }
        }
        
        return Component.empty();
    }
    
    protected void roleButtonClicked(Button button)
    {
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.SelectRole(getHeader(), ((RoleButtonWidget) button).role));
    }
    
    protected void ready1ButtonClicked()
    {
        if(player1Button != null && player2Button != null && getPlayerRole() == PlayerRole.PLAYER1)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(getHeader(), !getDuelManager().player1Ready));
        }
    }
    
    protected void ready2ButtonClicked()
    {
        if(player1Button != null && player2Button != null && getPlayerRole() == PlayerRole.PLAYER2)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestReady(getHeader(), !getDuelManager().player2Ready));
        }
    }
}
