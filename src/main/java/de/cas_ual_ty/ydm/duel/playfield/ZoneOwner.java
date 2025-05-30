package de.cas_ual_ty.ydm.duel.playfield;

import de.cas_ual_ty.ydm.duel.PlayerRole;

public enum ZoneOwner
{
    PLAYER1(PlayerRole.PLAYER1), PLAYER2(PlayerRole.PLAYER2), NONE(null);
    
    public static final ZoneOwner[] PLAYERS = {PLAYER1, PLAYER2};
    public static final ZoneOwner[] VALUES = ZoneOwner.values();
    
    public static ZoneOwner getFromIndex(byte index)
    {
        return ZoneOwner.VALUES[index];
    }
    
    static
    {
        byte index = 0;
        for(ZoneOwner zoneOwner : ZoneOwner.VALUES)
        {
            zoneOwner.index = index++;
        }
    }
    
    private byte index;
    
    public final PlayerRole player;
    
    ZoneOwner(PlayerRole player)
    {
        this.player = player;
    }
    
    public PlayerRole getPlayer()
    {
        return player;
    }
    
    public boolean isPlayer()
    {
        return this == PLAYER1 || this == PLAYER2;
    }
    
    public boolean hasAccess(PlayerRole player)
    {
        return getPlayer() == null || player == getPlayer();
    }
    
    public byte getIndex()
    {
        return index;
    }
    
    public ZoneOwner opponent()
    {
        return switch (this) {
            case PLAYER1 -> PLAYER2;
            case PLAYER2 -> PLAYER1;
            default -> NONE;
        };
    }
    
    public static ZoneOwner fromPlayerRole(PlayerRole player)
    {
        if(player == PLAYER1.getPlayer())
        {
            return PLAYER1;
        }
        else if(player == PLAYER2.getPlayer())
        {
            return PLAYER2;
        }
        else
        {
            return NONE;
        }
    }
}
