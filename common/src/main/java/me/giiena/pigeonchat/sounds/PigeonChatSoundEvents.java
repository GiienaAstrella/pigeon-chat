package me.giiena.pigeonchat.sounds;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PigeonChatSoundEvents {
    private static final Map<ResourceKey<SoundEvent>, SoundEvent> SOUND_EVENTS = new HashMap<>();

    public static final SoundEvent PIGEON_AMBIENT = registerFromVanilla("entity.pigeon.ambient",
            "entity.parrot.ambient");
    public static final SoundEvent PIGEON_DEATH = registerFromVanilla("entity.pigeon.death",
            "entity.parrot.death");
    public static final SoundEvent PIGEON_FLY = registerFromVanilla("entity.pigeon.fly",
            "entity.parrot.fly");
    public static final SoundEvent PIGEON_HURT = registerFromVanilla("entity.pigeon.hurt",
            "entity.parrot.hurt");
    public static final SoundEvent PIGEON_STEP = registerFromVanilla("entity.pigeon.step",
            "entity.parrot.step");

    public static void init(BiConsumer<ResourceKey<SoundEvent>, SoundEvent> registry) {
        SOUND_EVENTS.forEach(registry);
    }

    @SuppressWarnings("unused")
    private static SoundEvent register(String id) {
        Identifier ident = PigeonChatCommon.identifier(id);
        return register(ident, ident);
    }

    private static SoundEvent registerFromVanilla(String id, String soundID) {
        Identifier ident = PigeonChatCommon.identifier(id);
        Identifier soundIdent = Identifier.withDefaultNamespace(soundID);
        return register(ident, soundIdent);
    }

    private static SoundEvent register(Identifier id, Identifier soundID) {
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundID);
        ResourceKey<SoundEvent> key = PigeonChatCommon.resourceKey(Registries.SOUND_EVENT, id);
        SOUND_EVENTS.put(key, soundEvent);
        return soundEvent;
    }
}
