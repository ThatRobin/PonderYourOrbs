package io.github.thatrobin.ponder_your_orbs.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.awt.*;

public interface CustomDyeableItem {
    String PRIMARY_COLOR_KEY = "primary_color";
    String SECONDARY_COLOR_KEY = "secondary_color";
    String DISPLAY_KEY = "display";
    int DEFAULT_COLOR = 16773073;

    default boolean hasColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
        return nbtCompound != null && nbtCompound.contains(PRIMARY_COLOR_KEY, NbtElement.NUMBER_TYPE) && nbtCompound.contains(SECONDARY_COLOR_KEY, NbtElement.NUMBER_TYPE);
    }

    default int[] getColors(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
        if (nbtCompound != null && nbtCompound.contains(PRIMARY_COLOR_KEY, NbtElement.NUMBER_TYPE) && nbtCompound.contains(SECONDARY_COLOR_KEY, NbtElement.NUMBER_TYPE)) {
            return new int[] { nbtCompound.getInt(PRIMARY_COLOR_KEY), nbtCompound.getInt(SECONDARY_COLOR_KEY) };
        }
        return new int[] { DEFAULT_COLOR, DEFAULT_COLOR };
    }

    default void setColors(ItemStack stack, Color[] colors) {
        stack.getOrCreateSubNbt(DISPLAY_KEY).putInt(PRIMARY_COLOR_KEY, colors[0].getRGB());
        stack.getOrCreateSubNbt(DISPLAY_KEY).putInt(SECONDARY_COLOR_KEY, colors[1].getRGB());
    }

}
