package net.mrqx.timecapsule.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.mrqx.timecapsule.registry.TimeCapsuleBlockRegistry;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.concurrent.CompletableFuture;

public class TimeCapsuleDataMapProvider extends DataMapProvider {
    public TimeCapsuleDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }
    
    @Override
    protected void gather(HolderLookup.Provider provider) {
        Builder<Oxidizable, Block> oxidizableBlockBuilder = this.builder(NeoForgeDataMaps.OXIDIZABLES);
        Builder<Waxable, Block> waxableBlockBuilder = this.builder(NeoForgeDataMaps.WAXABLES);
        
        oxidizableBlockBuilder.add(TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK, new Oxidizable(TimeCapsuleBlockRegistry.EXPOSED_TIME_CAPSULE_BLOCK.get()), false);
        oxidizableBlockBuilder.add(TimeCapsuleBlockRegistry.EXPOSED_TIME_CAPSULE_BLOCK, new Oxidizable(TimeCapsuleBlockRegistry.WEATHERED_TIME_CAPSULE_BLOCK.get()), false);
        oxidizableBlockBuilder.add(TimeCapsuleBlockRegistry.WEATHERED_TIME_CAPSULE_BLOCK, new Oxidizable(TimeCapsuleBlockRegistry.OXIDIZED_TIME_CAPSULE_BLOCK.get()), false);
        waxableBlockBuilder.add(TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK, new Waxable(TimeCapsuleBlockRegistry.WAXED_TIME_CAPSULE_BLOCK.get()), false);
        waxableBlockBuilder.add(TimeCapsuleBlockRegistry.EXPOSED_TIME_CAPSULE_BLOCK, new Waxable(TimeCapsuleBlockRegistry.WAXED_EXPOSED_TIME_CAPSULE_BLOCK.get()), false);
        waxableBlockBuilder.add(TimeCapsuleBlockRegistry.WEATHERED_TIME_CAPSULE_BLOCK, new Waxable(TimeCapsuleBlockRegistry.WAXED_WEATHERED_TIME_CAPSULE_BLOCK.get()), false);
        waxableBlockBuilder.add(TimeCapsuleBlockRegistry.OXIDIZED_TIME_CAPSULE_BLOCK, new Waxable(TimeCapsuleBlockRegistry.WAXED_OXIDIZED_TIME_CAPSULE_BLOCK.get()), false);
    }
}
