package me.giiena.pigeonchat.block;

import com.mojang.serialization.MapCodec;
import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import me.giiena.pigeonchat.inventory.AbstractMessengerMenu;
import me.giiena.pigeonchat.inventory.MenuProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BirdCage extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

    public BirdCage(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    @NonNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BirdCage::new);
    }

    @Override
    @NonNull
    protected InteractionResult useWithoutItem(@NonNull BlockState state,
                                               @NonNull Level level,
                                               @NonNull BlockPos pos,
                                               Player player,
                                               @NonNull BlockHitResult hitResult) {
        if (!player.isCrouching()) return InteractionResult.PASS;

        ItemStack cage = new ItemStack(this.asItem());
        if (level.getBlockEntity(pos) instanceof BirdCageEntity entity) {
            cage.applyComponents(DataComponentMap.builder().addAll(entity.components()).build());
        }

        level.removeBlock(pos, false);

        if (player.getMainHandItem().isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, cage);
        } else if (player.getOffhandItem().isEmpty()) {
            player.setItemInHand(InteractionHand.OFF_HAND, cage);
        } else if (!player.getInventory().add(cage)) {
            Block.popResource(level, pos, cage);
        }

        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    @NonNull
    protected InteractionResult useItemOn(@NonNull ItemStack stack,
                                          @NonNull BlockState state,
                                          @NonNull Level level,
                                          @NonNull BlockPos pos,
                                          @NonNull Player player,
                                          @NonNull InteractionHand hand,
                                          @NonNull BlockHitResult hitResult) {
        if (stack.isEmpty()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!this.isFrontClear(state, level, pos)) {
            player.sendOverlayMessage(Component.translatable(PigeonChatCommon.langKey("cage",
                    "overlay",
                    "blocked")).withColor(TextColor.RED));
            return InteractionResult.FAIL;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof BirdCageEntity cage) {
            MessengerAnimal messenger = cage.messenger();
            if (messenger != null && messenger.isDeliverable(stack)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    MenuProviders.openMessenger(serverPlayer,
                            cage,
                            AbstractMessengerMenu.collectValidTargets(serverPlayer),
                            hand);
                }
                return InteractionResult.SUCCESS;
            } else if (messenger == null) {
                player.sendOverlayMessage(Component.translatable(PigeonChatCommon.langKey("cage",
                        "overlay",
                        "empty")).withColor(TextColor.RED));
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    protected boolean isFrontClear(BlockState state, Level level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING))).isAir();
    }

    @Override
    @NonNull
    protected VoxelShape getShape(@NonNull BlockState state,
                                  @NonNull BlockGetter level,
                                  @NonNull BlockPos pos,
                                  @NonNull CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(@NonNull BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new BirdCageEntity(blockPos, blockState);
    }
}
