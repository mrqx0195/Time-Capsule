package net.mrqx.timecapsule.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.registry.TimeCapsuleBlockRegistry;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class TimeCapsuleBlockTagsTagProvider extends BlockTagsProvider {
    public TimeCapsuleBlockTagsTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, TimeCapsule.MODID);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider registries) {
        TimeCapsuleBlockRegistry.BLOCKS.getEntries().forEach(holder -> {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(holder.get());
            this.tag(BlockTags.NEEDS_STONE_TOOL).add(holder.get());
        });
    }
}
