package me.giiena.pigeonchat.item;

import me.giiena.pigeonchat.component.CagedMessenger;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

public class BirdCageItem extends BlockItem implements PigeonChatItem {
    public BirdCageItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @NonNull
    public InteractionResult interactLivingEntity(@NonNull ItemStack stack,
                                                  @NonNull Player player,
                                                  @NonNull LivingEntity target,
                                                  @NonNull InteractionHand hand) {
        if (!(target instanceof MessengerAnimal messenger)) {
            return super.interactLivingEntity(stack, player, target, hand);
        }

        CagedMessenger data = new CagedMessenger(messenger);
        messenger.discard();

        ItemStack cage;
        if (stack.count() == 1) {
            cage = stack;
        } else {
            cage = stack.copyWithCount(1);
            stack.consume(1, player);
        }

        cage.set(PigeonChatComponents.CAGED_MESSENGER, data);
        if (stack.count() > 1 && !player.addItem(cage)) {
            player.drop(cage, false);
        }

        if (!player.level().isClientSide()) {
            player.level().playSound(null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.IRON_DOOR_OPEN,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f);
            player.level().playSound(null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.IRON_DOOR_CLOSE,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f);
        }

        return InteractionResult.SUCCESS;
    }
}
