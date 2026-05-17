package net.mrqx.timecapsule.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.mrqx.timecapsule.item.TimeCapsuleData;
import net.mrqx.timecapsule.item.TimeMode;
import net.mrqx.timecapsule.registry.TimeCapsuleBlockRegistry;
import net.mrqx.timecapsule.registry.TimeCapsuleItemRegistry;

import javax.annotation.Nullable;

public class BlockEntityTimeCapsule extends BlockEntity {
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_TIME_MODE = "time_mode";
    private static final String TAG_UNLOCK_TIME = "unlock_time";
    
    @Nullable
    private TimeCapsuleData data;
    
    public BlockEntityTimeCapsule(BlockPos pos, BlockState state) {
        super(TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK_ENTITY.get(), pos, state);
    }
    
    public boolean isSealed() {
        return this.data != null;
    }
    
    public boolean isOpenable(Level level) {
        return this.data != null && this.data.isOpenable(level);
    }
    
    @Nullable
    public TimeCapsuleData getData() {
        return this.data;
    }
    
    public void setData(TimeCapsuleData data) {
        this.data = data;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    public void clearData() {
        this.data = null;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (this.data != null) {
            builder.set(TimeCapsuleItemRegistry.TIME_CAPSULE_DATA.get(), this.data);
        }
    }
    
    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        this.data = input.get(TimeCapsuleItemRegistry.TIME_CAPSULE_DATA.get());
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.data != null) {
            ContainerHelper.saveAllItems(output, this.data.items(), false);
            output.store(TAG_MESSAGE, ComponentSerialization.CODEC, this.data.message());
            output.putString(TAG_TIME_MODE, this.data.timeMode().name());
            output.putLong(TAG_UNLOCK_TIME, this.data.unlockTime());
        }
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        NonNullList<ItemStack> items = NonNullList.withSize(TimeCapsuleData.SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        Component message = input.read(TAG_MESSAGE, ComponentSerialization.CODEC).orElse(Component.empty());
        TimeMode timeMode = TimeMode.valueOf(input.getStringOr(TAG_TIME_MODE, TimeMode.REAL_TIME.name()));
        long unlockTime = input.getLongOr(TAG_UNLOCK_TIME, 0);
        if (!message.toString().isBlank() && unlockTime != 0) {
            this.data = new TimeCapsuleData(items, message, timeMode, unlockTime);
        }
    }
}
