package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.block.BirdCageEntity;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessengerCageMenu extends AbstractMessengerMenu {
    protected MessengerCageMenu(int containerID,
                                Inventory inventory,
                                BlockPos pos,
                                Player sender,
                                List<UUID> targets,
                                InteractionHand hand) {
        super(MenuTypes.CAGE,
                containerID,
                BirdCageEntity.resolve(inventory, pos),
                sender,
                targets,
                hand);
    }

    public static void open(ServerPlayer player,
                            MessengerMenuSource source,
                            List<UUID> targets,
                            InteractionHand hand) {
        if (!(source instanceof BirdCageEntity cage)) return;

        player.openMenu(new ExtendedMenuProvider<Data>() {
            @Override
            public AbstractContainerMenu createMenu(int containerId,
                                                    @NonNull Inventory inventory,
                                                    @NonNull Player player) {
                return new MessengerCageMenu(containerId,
                        inventory,
                        cage.getBlockPos(),
                        player,
                        targets,
                        hand);
            }

            @Override
            @NonNull
            public Component getDisplayName() {
                return TITLE;
            }

            @Override
            @NonNull
            public Data getScreenOpeningData(@NonNull ServerPlayer player) {
                return new Data(cage.getBlockPos(), targets);
            }
        });
    }

    public record Data(BlockPos pos, List<UUID> targets) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC =
                StreamCodec.composite(
                        BlockPos.STREAM_CODEC, Data::pos,
                        ByteBufCodecs.collection(ArrayList::new, UUIDUtil.STREAM_CODEC),
                        Data::targets,
                        Data::new);
    }
}
