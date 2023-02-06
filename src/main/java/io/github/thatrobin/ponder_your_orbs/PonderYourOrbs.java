package io.github.thatrobin.ponder_your_orbs;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.util.OriginsConfigSerializer;
import io.github.apace100.origins.util.OriginsJsonConfigSerializer;
import io.github.thatrobin.ponder_your_orbs.items.OrbItem;
import io.github.thatrobin.ponder_your_orbs.loot_functions.OrbRandomizerLootFunction;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;

import java.util.List;

public class PonderYourOrbs implements ModInitializer {

    public static final OrbItem ORB_ITEM = new OrbItem();

    public static ServerConfig config;
    private static ConfigSerializer<ServerConfig> configSerializer;

    public static final LootFunctionType RANDOMIZE_ORB = register("randomize_orb", new OrbRandomizerLootFunction.Serializer());

    private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, identifier(id), new LootFunctionType(jsonSerializer));
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(ServerConfig.class,
                (definition, configClass) -> {
                    configSerializer = new OriginsJsonConfigSerializer<>(definition, configClass,
                            new OriginsConfigSerializer<>(definition, configClass));
                    return configSerializer;
                });
        config = AutoConfig.getConfigHolder(ServerConfig.class).getConfig();
        config.addToConfig();

        Registry.register(Registries.ITEM, new Identifier("ponder_your_orbs", "orb"), ORB_ITEM);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, var) -> {
            ItemGroups.TOOLS.updateEntries(ItemGroups.enabledFeatures, ItemGroups.operatorEnabled);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroupEntries) -> {
            List<OriginLayer> layers = OriginLayers.getLayers().stream().toList();
            for (int i = layers.size(); i-- > 0; ) {
                itemGroupEntries.add(ORB_ITEM.getLayerStack(layers.get(i).getIdentifier()));
            }
        });

        LootTableEvents.MODIFY.register((resourceManager, manager, identifier, builder, source) -> {
            if(LootTables.END_CITY_TREASURE_CHEST.equals(identifier)) {
                if(config.spawnInEndCity()) {
                    LootPool.Builder poolBuilder = LootPool.builder();
                    poolBuilder.with(ItemEntry.builder(ORB_ITEM)).conditionally(RandomChanceLootCondition.builder(0.1f)).apply(OrbRandomizerLootFunction.builder().build());
                    builder.pool(poolBuilder);
                }
            }
        });
    }

    public static Identifier identifier(String id) {
        return new Identifier("ponder_your_orbs", id);
    }

    @Config(name = "ponder_your_orbs_server")
    public static class ServerConfig implements ConfigData {

        public JsonObject ponder_your_orbs = new JsonObject();

        public boolean spawnInEndCity() {
            return ponder_your_orbs.get("spawn_in_end_city").getAsBoolean();
        }

        public void addToConfig() {
            if(!ponder_your_orbs.has("spawn_in_end_city")) {
                ponder_your_orbs.addProperty("spawn_in_end_city", Boolean.TRUE);
            }
        }
    }
}
