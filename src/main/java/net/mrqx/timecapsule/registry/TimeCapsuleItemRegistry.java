package net.mrqx.timecapsule.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.item.BlockItemTimeCapsule;
import net.mrqx.timecapsule.item.ItemTimeCapsule;
import net.mrqx.timecapsule.item.TimeCapsuleData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TimeCapsuleItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TimeCapsule.MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TimeCapsule.MODID);
    
    public static final DeferredItem<Item> TIME_CAPSULE = ITEMS.registerItem("time_capsule", ItemTimeCapsule::new);
    
    public static final DeferredItem<BlockItem> WAXED_OXIDIZED_TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("waxed_oxidized_time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.WAXED_OXIDIZED_TIME_CAPSULE_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> WAXED_WEATHERED_TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("waxed_weathered_time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.WAXED_WEATHERED_TIME_CAPSULE_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> WAXED_EXPOSED_TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("waxed_exposed_time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.WAXED_EXPOSED_TIME_CAPSULE_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> WAXED_TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("waxed_time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.WAXED_TIME_CAPSULE_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> OXIDIZED_TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("oxidized_time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.OXIDIZED_TIME_CAPSULE_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> WEATHERED_TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("weathered_time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.WEATHERED_TIME_CAPSULE_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> EXPOSED_TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("exposed_time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.EXPOSED_TIME_CAPSULE_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> TIME_CAPSULE_BLOCK_ITEM = ITEMS.registerItem("time_capsule_block", p ->
        new BlockItemTimeCapsule(TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK.get(), p));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TimeCapsuleData>> TIME_CAPSULE_DATA =
        DATA_COMPONENTS.register("time_capsule_data", () -> DataComponentType.<TimeCapsuleData>builder()
            .persistent(TimeCapsuleData.CODEC)
            .networkSynchronized(TimeCapsuleData.STREAM_CODEC)
            .build()
        );
}
