package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SpellYdmProperties extends YdmProperties
{
    public SpellType spellType;
    
    public SpellYdmProperties(YdmProperties p0, JsonObject j)
    {
        super(p0);
        readSpellProperties(j);
    }
    
    public SpellYdmProperties(YdmProperties p0)
    {
        super(p0);
        
        if(p0 instanceof SpellYdmProperties)
        {
            SpellYdmProperties p1 = (SpellYdmProperties) p0;
            spellType = p1.spellType;
        }
    }
    
    public SpellYdmProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        readSpellProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        writeSpellProperties(j);
    }
    
    public void readSpellProperties(JsonObject j)
    {
        spellType = SpellType.fromString(j.get(JsonKeys.SPELL_TYPE).getAsString());
    }
    
    public void writeSpellProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.SPELL_TYPE, spellType.name);
    }
    
    @Override
    public void addCardType(List<Component> list)
    {
        list.add(Component.literal(getSpellType().name + " " + getType().name));
    }
    
    // --- Getters ---
    
    public SpellType getSpellType()
    {
        return spellType;
    }
}
