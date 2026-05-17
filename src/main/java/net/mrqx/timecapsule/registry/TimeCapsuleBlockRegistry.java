package net.mrqx.timecapsule.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.block.BlockEntityTimeCapsule;
import net.mrqx.timecapsule.block.BlockTimeCapsule;
import net.mrqx.timecapsule.block.BlockWeatheringTimeCapsule;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TimeCapsuleBlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TimeCapsule.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TimeCapsule.MODID);
    
    public static final DeferredBlock<BlockTimeCapsule> TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("time_capsule_block",
        p -> new BlockWeatheringTimeCapsule(WeatheringCopper.WeatherState.UNAFFECTED, p),
        () -> BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(3.0F, 6.0F)
            .instrument(NoteBlockInstrument.TRUMPET)
            .sound(SoundType.COPPER)
            .noOcclusion()
    );
    public static final DeferredBlock<BlockTimeCapsule> EXPOSED_TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("exposed_time_capsule_block",
        p -> new BlockWeatheringTimeCapsule(WeatheringCopper.WeatherState.EXPOSED, p),
        () -> BlockBehaviour.Properties.ofFullCopy(TIME_CAPSULE_BLOCK.get())
            .instrument(NoteBlockInstrument.TRUMPET_EXPOSED)
            .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
    );
    public static final DeferredBlock<BlockTimeCapsule> WEATHERED_TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("weathered_time_capsule_block",
        p -> new BlockWeatheringTimeCapsule(WeatheringCopper.WeatherState.WEATHERED, p),
        () -> BlockBehaviour.Properties.ofFullCopy(TIME_CAPSULE_BLOCK.get())
            .instrument(NoteBlockInstrument.TRUMPET_WEATHERED)
            .mapColor(MapColor.WARPED_STEM)
    );
    public static final DeferredBlock<BlockTimeCapsule> OXIDIZED_TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("oxidized_time_capsule_block",
        p -> new BlockWeatheringTimeCapsule(WeatheringCopper.WeatherState.OXIDIZED, p),
        () -> BlockBehaviour.Properties.ofFullCopy(TIME_CAPSULE_BLOCK.get())
            .instrument(NoteBlockInstrument.TRUMPET_OXIDIZED)
            .mapColor(MapColor.WARPED_NYLIUM)
    );
    
    public static final DeferredBlock<BlockTimeCapsule> WAXED_TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("waxed_time_capsule_block",
        p -> new BlockTimeCapsule(WeatheringCopper.WeatherState.UNAFFECTED, p),
        () -> BlockBehaviour.Properties.ofFullCopy(TIME_CAPSULE_BLOCK.get())
    );
    public static final DeferredBlock<BlockTimeCapsule> WAXED_EXPOSED_TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("waxed_exposed_time_capsule_block",
        p -> new BlockTimeCapsule(WeatheringCopper.WeatherState.EXPOSED, p),
        () -> BlockBehaviour.Properties.ofFullCopy(EXPOSED_TIME_CAPSULE_BLOCK.get())
    );
    public static final DeferredBlock<BlockTimeCapsule> WAXED_WEATHERED_TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("waxed_weathered_time_capsule_block",
        p -> new BlockTimeCapsule(WeatheringCopper.WeatherState.WEATHERED, p),
        () -> BlockBehaviour.Properties.ofFullCopy(WEATHERED_TIME_CAPSULE_BLOCK.get())
    );
    public static final DeferredBlock<BlockTimeCapsule> WAXED_OXIDIZED_TIME_CAPSULE_BLOCK = BLOCKS.registerBlock("waxed_oxidized_time_capsule_block",
        p -> new BlockTimeCapsule(WeatheringCopper.WeatherState.OXIDIZED, p),
        () -> BlockBehaviour.Properties.ofFullCopy(OXIDIZED_TIME_CAPSULE_BLOCK.get())
    );
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityTimeCapsule>> TIME_CAPSULE_BLOCK_ENTITY = BLOCK_ENTITIES.register("time_capsule_block",
        () -> new BlockEntityType<>(BlockEntityTimeCapsule::new,
            TIME_CAPSULE_BLOCK.get(),
            EXPOSED_TIME_CAPSULE_BLOCK.get(),
            WEATHERED_TIME_CAPSULE_BLOCK.get(),
            OXIDIZED_TIME_CAPSULE_BLOCK.get(),
            WAXED_TIME_CAPSULE_BLOCK.get(),
            WAXED_EXPOSED_TIME_CAPSULE_BLOCK.get(),
            WAXED_WEATHERED_TIME_CAPSULE_BLOCK.get(),
            WAXED_OXIDIZED_TIME_CAPSULE_BLOCK.get()
        ));
}
