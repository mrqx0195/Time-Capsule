package net.mrqx.timecapsule.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record TimeCapsuleData(NonNullList<ItemStack> items, Component message, TimeMode timeMode, long unlockTime) {
    public static final int SIZE = 25;
    
    public static final Codec<TimeCapsuleData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.OPTIONAL_CODEC.listOf()
                .fieldOf("items")
                .forGetter(d -> {
                    List<ItemStack> list = new ArrayList<>(d.items);
                    int lastNonEmpty = -1;
                    for (int i = 0; i < list.size(); i++) {
                        if (!list.get(i).isEmpty()) {
                            lastNonEmpty = i;
                        }
                    }
                    return lastNonEmpty >= 0
                        ? list.subList(0, lastNonEmpty + 1)
                        : List.of();
                }),
            ComponentSerialization.CODEC
                .fieldOf("message")
                .forGetter(TimeCapsuleData::message),
            TimeMode.CODEC
                .fieldOf("time_mode")
                .forGetter(TimeCapsuleData::timeMode),
            Codec.LONG
                .fieldOf("unlock_time")
                .forGetter(TimeCapsuleData::unlockTime)
        ).apply(instance, (items, message, timeMode, unlockTime) -> {
            NonNullList<ItemStack> nonNullItems = NonNullList.withSize(SIZE, ItemStack.EMPTY);
            for (int i = 0; i < Math.min(SIZE, items.size()); i++) {
                nonNullItems.set(i, items.get(i).copy());
            }
            return new TimeCapsuleData(nonNullItems, message, timeMode, unlockTime);
        })
    );
    
    public static final StreamCodec<RegistryFriendlyByteBuf, TimeCapsuleData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TimeCapsuleData decode(RegistryFriendlyByteBuf input) {
            NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
            for (int i = 0; i < SIZE; i++) {
                items.set(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(input));
            }
            Component message = ComponentSerialization.STREAM_CODEC.decode(input);
            TimeMode timeMode = TimeMode.values()[input.readByte()];
            long unlockTime = input.readLong();
            return new TimeCapsuleData(items, message, timeMode, unlockTime);
        }
        
        @Override
        public void encode(RegistryFriendlyByteBuf output, TimeCapsuleData value) {
            for (ItemStack stack : value.items) {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(output, stack);
            }
            ComponentSerialization.STREAM_CODEC.encode(output, value.message);
            output.writeByte(value.timeMode.ordinal());
            output.writeLong(value.unlockTime);
        }
    };
    
    public static Component formatRemainingMillis(long remainingMillis) {
        long totalSeconds = remainingMillis / 1000;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return Component.translatable("tooltip.time_capsule.remaining_time",
            days, hours, minutes, seconds).withStyle(ChatFormatting.GRAY);
    }
    
    public static Component formatRemainingTicks(long remainingTicks) {
        long totalSeconds = remainingTicks / 20;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return Component.translatable("tooltip.time_capsule.remaining_time",
            days, hours, minutes, seconds).withStyle(ChatFormatting.GRAY);
    }
    
    public boolean isOpenable(@Nullable Level level) {
        long currentTime = switch (timeMode) {
            case GAME_TICK -> level != null ? level.getGameTime() : 0;
            case REAL_TIME -> System.currentTimeMillis();
        };
        return currentTime >= unlockTime;
    }
    
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip) {
        long currentTime;
        try (Level level = context.level()) {
            currentTime = switch (timeMode) {
                case GAME_TICK -> level != null ? level.getGameTime() : 0;
                case REAL_TIME -> System.currentTimeMillis();
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long remaining = unlockTime - currentTime;
        
        if (remaining > 0) {
            tooltip.accept(Component.translatable("tooltip.time_capsule.sealed").withStyle(ChatFormatting.GRAY));
            switch (timeMode) {
                case GAME_TICK -> tooltip.accept(formatRemainingTicks(remaining));
                case REAL_TIME -> tooltip.accept(formatRemainingMillis(remaining));
            }
        } else {
            tooltip.accept(Component.translatable("tooltip.time_capsule.ready").withStyle(ChatFormatting.GREEN));
        }
    }
}
