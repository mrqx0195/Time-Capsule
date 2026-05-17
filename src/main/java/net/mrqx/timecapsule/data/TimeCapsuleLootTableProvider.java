package net.mrqx.timecapsule.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.mrqx.timecapsule.block.BlockTimeCapsule;
import net.mrqx.timecapsule.registry.TimeCapsuleBlockRegistry;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TimeCapsuleLootTableProvider extends LootTableProvider {
    public TimeCapsuleLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output,
            Set.of(),
            List.of(new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)),
            registries);
    }
    
    @Override
    protected void validate(WritableRegistry<LootTable> tables, ValidationContextSource validationContext, ProblemReporter.Collector problems) {
    }
    
    public static class BlockLoot extends BlockLootSubProvider {
        public static final Set<DeferredBlock<BlockTimeCapsule>> TIME_CAPSULE_BLOCKS = Set.of(
            TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK,
            TimeCapsuleBlockRegistry.EXPOSED_TIME_CAPSULE_BLOCK,
            TimeCapsuleBlockRegistry.WEATHERED_TIME_CAPSULE_BLOCK,
            TimeCapsuleBlockRegistry.OXIDIZED_TIME_CAPSULE_BLOCK,
            TimeCapsuleBlockRegistry.WAXED_TIME_CAPSULE_BLOCK,
            TimeCapsuleBlockRegistry.WAXED_EXPOSED_TIME_CAPSULE_BLOCK,
            TimeCapsuleBlockRegistry.WAXED_WEATHERED_TIME_CAPSULE_BLOCK,
            TimeCapsuleBlockRegistry.WAXED_OXIDIZED_TIME_CAPSULE_BLOCK
        );
        private static final Set<Item> EXPLOSION_RESISTANT = TIME_CAPSULE_BLOCKS.stream()
            .map(ItemLike::asItem)
            .collect(Collectors.toSet());
        
        protected BlockLoot(HolderLookup.Provider registries) {
            super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
        }
        
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return TIME_CAPSULE_BLOCKS.stream()
                .map(DeferredHolder::get)
                .map(block -> (Block) block)
                .collect(Collectors.toSet());
        }
        
        @Override
        protected void generate() {
            this.add(TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
            this.add(TimeCapsuleBlockRegistry.EXPOSED_TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
            this.add(TimeCapsuleBlockRegistry.WEATHERED_TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
            this.add(TimeCapsuleBlockRegistry.OXIDIZED_TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
            this.add(TimeCapsuleBlockRegistry.WAXED_TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
            this.add(TimeCapsuleBlockRegistry.WAXED_EXPOSED_TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
            this.add(TimeCapsuleBlockRegistry.WAXED_WEATHERED_TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
            this.add(TimeCapsuleBlockRegistry.WAXED_OXIDIZED_TIME_CAPSULE_BLOCK.get(), this::createShulkerBoxDrop);
        }
    }
}
