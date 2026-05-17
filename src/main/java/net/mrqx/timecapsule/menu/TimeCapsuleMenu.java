package net.mrqx.timecapsule.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.mrqx.timecapsule.block.BlockEntityTimeCapsule;
import net.mrqx.timecapsule.item.TimeCapsuleData;
import net.mrqx.timecapsule.item.TimeMode;
import net.mrqx.timecapsule.registry.TimeCapsuleItemRegistry;
import net.mrqx.timecapsule.registry.TimeCapsuleMenuRegistry;

import javax.annotation.Nullable;

public class TimeCapsuleMenu extends AbstractContainerMenu {
    private static final long TICKS_PER_SECOND = 20L;
    private static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    private static final long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;
    private static final long TICKS_PER_DAY = TICKS_PER_HOUR * 24;
    private static final long TICKS_PER_MONTH = TICKS_PER_DAY * 30;
    private static final long TICKS_PER_YEAR = TICKS_PER_DAY * 365;
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
    private static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
    private static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
    private static final long MILLIS_PER_MONTH = MILLIS_PER_DAY * 30;
    private static final long MILLIS_PER_YEAR = MILLIS_PER_DAY * 365;
    private final Player player;
    @Nullable
    private final InteractionHand hand;
    @Nullable
    private final BlockPos blockPos;
    private final SimpleContainer capsuleContainer = new SimpleContainer(TimeCapsuleData.SIZE);
    
    public TimeCapsuleMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, findHand(inventory.player), null);
    }
    
    public TimeCapsuleMenu(int containerId, Inventory inventory, InteractionHand hand) {
        this(containerId, inventory, hand, null);
    }
    
    public TimeCapsuleMenu(int containerId, Inventory inventory, BlockPos blockPos) {
        this(containerId, inventory, null, blockPos);
    }
    
    private TimeCapsuleMenu(int containerId, Inventory inventory, @Nullable InteractionHand hand, @Nullable BlockPos blockPos) {
        super(TimeCapsuleMenuRegistry.TIME_CAPSULE_MENU.get(), containerId);
        this.player = inventory.player;
        this.hand = hand;
        this.blockPos = blockPos;
        
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                this.addSlot(new Slot(this.capsuleContainer, col + row * 5,
                    145 + col * 18, 45 + row * 18
                ) {
                    @Override
                    public boolean mayPlace(ItemStack itemStack) {
                        return !itemStack.is(TimeCapsuleItemRegistry.TIME_CAPSULE.get());
                    }
                });
            }
        }
        
        int playerInvTop = 153;
        int xBase = 42;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9,
                    xBase + col * 18, playerInvTop + row * 18
                ));
            }
        }
        int hotbarTop = playerInvTop + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col,
                xBase + col * 18, hotbarTop
            ));
        }
    }
    
    @Nullable
    public static InteractionHand findHand(Player player) {
        for (InteractionHand h : InteractionHand.values()) {
            if (player.getItemInHand(h).is(TimeCapsuleItemRegistry.TIME_CAPSULE.get())) {
                return h;
            }
        }
        return null;
    }
    
    public static long convertToTicks(int years, int months, int days, int hours, int minutes, int seconds) {
        return years * TICKS_PER_YEAR
            + months * TICKS_PER_MONTH
            + days * TICKS_PER_DAY
            + hours * TICKS_PER_HOUR
            + minutes * TICKS_PER_MINUTE
            + seconds * TICKS_PER_SECOND;
    }
    
    public static long convertToMillis(int years, int months, int days, int hours, int minutes, int seconds) {
        return years * MILLIS_PER_YEAR
            + months * MILLIS_PER_MONTH
            + days * MILLIS_PER_DAY
            + hours * MILLIS_PER_HOUR
            + minutes * MILLIS_PER_MINUTE
            + seconds * MILLIS_PER_SECOND;
    }
    
    @Override
    public boolean stillValid(Player player) {
        if (this.blockPos != null) {
            return player.distanceToSqr(this.blockPos.getCenter()) <= 64.0
                && player.level().getBlockEntity(this.blockPos) instanceof BlockEntityTimeCapsule;
        }
        return this.hand != null && player.getItemInHand(this.hand).is(TimeCapsuleItemRegistry.TIME_CAPSULE.get());
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide()) {
            return;
        }
        for (int i = 0; i < this.capsuleContainer.getContainerSize(); i++) {
            ItemStack stack = this.capsuleContainer.removeItem(i, this.capsuleContainer.getMaxStackSize());
            if (!stack.isEmpty()) {
                if (!player.addItem(stack)) {
                    player.drop(stack, false);
                }
            }
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack stack;
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack slotStack = slot.getItem();
        stack = slotStack.copy();
        int capsuleSlots = TimeCapsuleData.SIZE;
        int totalSlots = capsuleSlots + 36;
        if (slotIndex < capsuleSlots) {
            if (!this.moveItemStackTo(slotStack, capsuleSlots, totalSlots, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(slotStack, 0, capsuleSlots, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (slotStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack;
    }
    
    public void seal(Component message, TimeMode timeMode, int years, int months, int days, int hours, int minutes, int seconds) {
        NonNullList<ItemStack> items = NonNullList.withSize(TimeCapsuleData.SIZE, ItemStack.EMPTY);
        for (int i = 0; i < TimeCapsuleData.SIZE; i++) {
            ItemStack item = this.capsuleContainer.getItem(i);
            if (!item.isEmpty()) {
                items.set(i, item.copy());
            }
            this.capsuleContainer.setItem(i, ItemStack.EMPTY);
        }
        
        long unlockTime = switch (timeMode) {
            case GAME_TICK ->
                this.player.level().getGameTime() + convertToTicks(years, months, days, hours, minutes, seconds);
            case REAL_TIME ->
                System.currentTimeMillis() + convertToMillis(years, months, days, hours, minutes, seconds);
        };
        
        TimeCapsuleData data = new TimeCapsuleData(items, message, timeMode, unlockTime);
        
        if (this.blockPos != null) {
            if (this.player.level().getBlockEntity(this.blockPos) instanceof BlockEntityTimeCapsule blockEntity) {
                blockEntity.setData(data);
            }
        } else {
            if (this.hand != null) {
                ItemStack stack = this.player.getItemInHand(this.hand);
                stack.set(TimeCapsuleItemRegistry.TIME_CAPSULE_DATA.get(), data);
            }
        }
        
        this.player.closeContainer();
    }
}
