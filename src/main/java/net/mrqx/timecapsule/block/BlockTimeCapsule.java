package net.mrqx.timecapsule.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.item.TimeCapsuleData;
import net.mrqx.timecapsule.menu.TimeCapsuleMenu;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BlockTimeCapsule extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final MapCodec<BlockTimeCapsule> CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state")
                .forGetter(BlockTimeCapsule::getWeatheringState), propertiesCodec())
            .apply(i, BlockTimeCapsule::new)
    );
    private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateHorizontalAxis(
        Shapes.or(
            Block.box(1.349, 0.323, 4.024742, 14.651, 8.301, 11.995),
            Block.box(14.399, 0.0, 3.174, 16.0, 9.151, 12.826),
            Block.box(0.0, 0.0, 3.174, 1.601, 9.151, 12.826)
        )
    );
    protected final WeatheringCopper.WeatherState weatheringState;
    
    public BlockTimeCapsule(WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(properties);
        this.weatheringState = weatherState;
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
        );
    }
    
    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }
    
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityTimeCapsule(pos, state);
    }
    
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof BlockEntityTimeCapsule blockEntity)) {
            return InteractionResult.PASS;
        }
        
        if (!blockEntity.isSealed()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            player.openMenu(new SimpleMenuProvider((id, inv, _) -> new TimeCapsuleMenu(id, inv, pos), Component.translatable("container.time_capsule")));
            player.swing(InteractionHand.MAIN_HAND);
            return InteractionResult.CONSUME;
            
        } else {
            TimeCapsuleData data = blockEntity.getData();
            if (data != null && data.isOpenable(level)) {
                if (!level.isClientSide()) {
                    for (ItemStack item : data.items()) {
                        if (!item.isEmpty()) {
                            if (!player.addItem(item)) {
                                player.drop(item, false);
                            }
                        }
                    }
                    if (!data.message().toString().isBlank()) {
                        player.sendSystemMessage(data.message());
                    }
                    blockEntity.clearData();
                }
                player.swing(InteractionHand.MAIN_HAND);
                return InteractionResult.CONSUME;
                
            } else {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(TimeCapsule.getMessageNotReady(player.getRandom()));
                }
                player.swing(InteractionHand.MAIN_HAND);
                return InteractionResult.FAIL;
            }
        }
    }
    
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!level.isClientSide()
            && blockEntity instanceof BlockEntityTimeCapsule timeCapsule
            && timeCapsule.isSealed()
            && player.isCreative()) {
            ItemStack itemStack = new ItemStack(this);
            itemStack.applyComponents(timeCapsule.collectComponents());
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
    
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof BlockEntityTimeCapsule timeCapsule && timeCapsule.isSealed()) {
            ItemStack stack = new ItemStack(this);
            stack.applyComponents(timeCapsule.collectComponents());
            return List.of(stack);
        }
        return super.getDrops(state, params);
    }
    
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }
    
    
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
    
    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
    }
    
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    
    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
    
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING).getAxis());
    }
}
