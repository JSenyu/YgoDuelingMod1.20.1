package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TrapYdmProperties extends YdmProperties
{
    public TrapType trapType;
    
    public TrapYdmProperties(YdmProperties p0, JsonObject j)
    {
        super(p0);
        readTrapProperties(j);
    }
    
    public TrapYdmProperties(YdmProperties p0)
    {
        super(p0);
        
        if(p0 instanceof TrapYdmProperties)
        {
            TrapYdmProperties p1 = (TrapYdmProperties) p0;
            trapType = p1.trapType;
        }
    }
    
    public TrapYdmProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        readTrapProperties(j);
    }
    
    public void readTrapProperties(JsonObject j)
    {
        trapType = TrapType.fromString(j.get(JsonKeys.TRAP_TYPE).getAsString());
    }
    
    @Override
    public void addCardType(List<Component> list)
    {
        list.add(Component.literal(getTrapType().name + " " + getType().name));
    }
    
    // --- Getters ---
    
    public TrapType getTrapType()
    {
        return trapType;
    }
}
