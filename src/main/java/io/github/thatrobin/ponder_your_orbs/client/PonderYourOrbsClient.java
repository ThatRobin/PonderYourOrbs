package io.github.thatrobin.ponder_your_orbs.client;

import io.github.thatrobin.ponder_your_orbs.PonderYourOrbs;
import io.github.thatrobin.ponder_your_orbs.items.OrbItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;

@Environment(EnvType.CLIENT)
public class PonderYourOrbsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if(stack.getItem() instanceof OrbItem orbItem) {
                if(orbItem.hasColor(stack)) {
                    if (tintIndex == 1) {
                        return orbItem.getColors(stack)[0];
                    } else if (tintIndex == 2) {
                        return orbItem.getColors(stack)[1];
                    }
                }
            }
            return 16773073;
        }, PonderYourOrbs.ORB_ITEM);
    }
}
