package io.github.thatrobin.ponder_your_orbs.loot_functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.thatrobin.ponder_your_orbs.PonderYourOrbs;
import io.github.thatrobin.ponder_your_orbs.items.CustomDyeableItem;
import io.github.thatrobin.ponder_your_orbs.items.OrbItem;
import io.github.thatrobin.ponder_your_orbs.utils.HSLColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class OrbRandomizerLootFunction extends ConditionalLootFunction implements CustomDyeableItem {

    OrbRandomizerLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    public LootFunctionType getType() {
        return PonderYourOrbs.RANDOMIZE_ORB;
    }

    public ItemStack process(ItemStack itemStack, LootContext context) {
        List<OriginLayer> layers = OriginLayers.getLayers().stream().toList();
        Random random = new Random();
        OriginLayer layer = layers.get(0);
        int bound = layers.size()-1;
        if(bound > 0) {
            int randInt = random.nextInt(bound);
            layer = layers.get(randInt);
        }
        NbtCompound compound = itemStack.getOrCreateNbt();
        NbtList list = new NbtList();
        NbtCompound targetCompound = new NbtCompound();
        targetCompound.putString("Layer", layer.getIdentifier().toString());
        list.add(targetCompound);
        compound.put("Targets", list);
        this.setColors(itemStack, generateColours(layer.getIdentifier().toString()));
        itemStack.setNbt(compound);
        return itemStack;
    }

    private Color generateRandomBrightColor(Random random) {
        float minimumBrightness = 0.6f;
        float h = random.nextFloat();
        float s = 0.75f;
        float b = minimumBrightness + ((1f - minimumBrightness) * random.nextFloat());
        return Color.getHSBColor(h, s, b);
    }

    public Color generateSecondaryColour(Color main) {
        HSLColor color = new HSLColor(main);
        float h = (color.getHue() + 15f) / 360f;
        float s = 0.75f;
        float b = ((color.getLuminance() + 10f)/100f);
        return Color.getHSBColor(h, s, b);
    }

    public Color[] generateColours(String code) {
        Random random = new Random(code.hashCode());
        Color[] cols = new Color[2];
        //cols[0] = generateColour(random);
        cols[0] = generateRandomBrightColor(random);
        cols[1] = generateSecondaryColour(cols[0]);
        return cols;
    }

    public static Builder<?> builder() {
        return builder(OrbRandomizerLootFunction::new);
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<OrbRandomizerLootFunction> {
        public Serializer() {
        }

        public OrbRandomizerLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new OrbRandomizerLootFunction(lootConditions);
        }
    }
}