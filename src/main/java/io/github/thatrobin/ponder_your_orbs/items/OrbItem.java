package io.github.thatrobin.ponder_your_orbs.items;

import io.github.apace100.origins.content.OrbOfOriginItem;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModItems;
import io.github.thatrobin.ponder_your_orbs.utils.HSLColor;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Random;

public class OrbItem extends OrbOfOriginItem implements CustomDyeableItem {

    @Override
    public Text getName(ItemStack stack) {
        NbtCompound compound = stack.getOrCreateNbt();
        NbtList list = (NbtList) compound.get("Targets");
        if (list != null) {
            for (NbtElement nbtElement : list) {
                NbtCompound layerNbt = (NbtCompound) nbtElement;
                if(layerNbt.contains("Layer")) {
                    try {
                        OriginLayer layer = OriginLayers.getLayer(Identifier.tryParse(layerNbt.getString("Layer")));
                        return Text.translatable(this.getTranslationKey()).append(Text.translatable(layer.getOrCreateTranslationKey()));
                    } catch (Exception ignored) {
                        return Text.translatable("item.ponder_your_orbs.orb_deactivated");
                    }
                }
            }
        }
        return Text.translatable(this.getTranslationKey(), "Origin");
    }

    public ItemStack getLayerStack(Identifier layerID) {
        ItemStack stack = new ItemStack(this);
        NbtCompound compound = stack.getOrCreateNbt();
        NbtList list = new NbtList();
        NbtCompound targetCompound = new NbtCompound();
        targetCompound.putString("Layer", layerID.toString());
        list.add(targetCompound);
        compound.put("Targets", list);
        this.setColors(stack, generateColours(layerID.toString()));
        stack.setNbt(compound);
        return stack;
    }

    public Color generateColour(Random random) {
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        return new Color(r, g, b);
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

}
