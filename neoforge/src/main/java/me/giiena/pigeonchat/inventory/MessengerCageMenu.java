package me.giiena.pigeonchat.inventory;

import me.giiena.pigeonchat.block.BirdCageEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

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
        if(!(source instanceof BirdCageEntity cage)) return;

        MenuProvider provider = new MenuProvider() {
            @Override
            @NonNull
            public Component getDisplayName() {
                return TITLE;
            }

            @Override
            public AbstractContainerMenu createMenu(int containerID,
                                                    @NonNull Inventory inventory,
                                                    @NonNull Player player) {
                return new MessengerCageMenu(containerID,
                        inventory,
                        cage.getBlockPos(),
                        player,
                        targets,
                        hand);
            }
        };

        player.openMenu(provider, buf -> {
            buf.writeBlockPos(cage.getBlockPos());
            buf.writeCollection(targets, UUIDUtil.STREAM_CODEC);
        });
    }
}
