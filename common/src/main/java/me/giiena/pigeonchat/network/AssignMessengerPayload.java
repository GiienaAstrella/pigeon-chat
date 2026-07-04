package me.giiena.pigeonchat.network;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.inventory.AbstractMessengerMenu;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record AssignMessengerPayload(UUID target)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AssignMessengerPayload> TYPE =
            new CustomPacketPayload.Type<>(PigeonChatCommon.identifier("assign_messenger"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AssignMessengerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, AssignMessengerPayload::target,
                    AssignMessengerPayload::new);

    @Override
    @NonNull
    public Type<AssignMessengerPayload> type() {
        return TYPE;
    }

    public void handle(Player sender) {
        if (sender instanceof ServerPlayer serverSender) {
            if (sender.containerMenu instanceof AbstractMessengerMenu menu) {
                ServerPlayer target =
                        serverSender.level().getServer().getPlayerList().getPlayer(this.target);
                if (target == null) return;
                menu.assignMessenger(target);
                serverSender.closeContainer();
            }
        }
    }
}
