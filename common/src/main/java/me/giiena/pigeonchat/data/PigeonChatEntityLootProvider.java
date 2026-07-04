package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.entity.EntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public final class PigeonChatEntityLootProvider {
    public static void generate(BiConsumer<EntityType<?>, LootTable.Builder> add) {
        add.accept(EntityTypes.PIGEON, LootTable.lootTable()
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.FEATHER))));
    }
}
