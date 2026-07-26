package me.giiena.pigeonchat.component;

import com.mojang.serialization.Codec;
import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.function.Consumer;

public record CagedMessenger(CompoundTag messenger) implements TooltipProvider {
    public static final Codec<CagedMessenger> CODEC =
            CompoundTag.CODEC.xmap(CagedMessenger::new, CagedMessenger::messenger);
    public static final StreamCodec<RegistryFriendlyByteBuf, CagedMessenger> STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.<RegistryFriendlyByteBuf>cast().map(CagedMessenger::new, CagedMessenger::messenger);

    public CagedMessenger(MessengerAnimal messenger) {
        this(serialize(messenger));
    }

    private static CompoundTag serialize(MessengerAnimal messenger) {
        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(messenger.problemPath(), Constants.LOG)) {
            TagValueOutput messengerData = TagValueOutput.createWithContext(reporter,
                    messenger.registryAccess());
            messengerData.putString("id",
                    BuiltInRegistries.ENTITY_TYPE.getKey(messenger.getType()).toString());
            messenger.saveWithoutId(messengerData);
            return messengerData.buildResult();
        }
    }

    public Component name() {
        Optional<Component> customName = this.messenger.read("CustomName",
                ComponentSerialization.CODEC);
        if (customName.isPresent()) return customName.get();

        Optional<String> id = this.messenger.getString("id");
        Optional<EntityType<?>> entityType = id.flatMap(s -> {
            Identifier i = Identifier.tryParse(s);
            return i == null ? Optional.empty() : BuiltInRegistries.ENTITY_TYPE.getOptional(i);
        });
        return entityType.map(EntityType::getDescription).
                orElseGet(() -> Component.translatable(PigeonChatCommon.langKey("caged",
                        "tooltip",
                        "unknown")));
    }

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext tooltipContext,
                             Consumer<Component> consumer,
                             @NonNull TooltipFlag tooltipFlag,
                             @NonNull DataComponentGetter dataComponentGetter) {
        consumer.accept(Component.translatable(PigeonChatCommon.langKey("caged", "tooltip"),
                this.name()));
    }
}
