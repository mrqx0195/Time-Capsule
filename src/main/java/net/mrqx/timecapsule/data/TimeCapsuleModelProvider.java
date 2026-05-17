package net.mrqx.timecapsule.data;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.registry.TimeCapsuleItemRegistry;

import java.util.stream.Stream;

public class TimeCapsuleModelProvider extends ModelProvider {
    public TimeCapsuleModelProvider(PackOutput output) {
        super(output, TimeCapsule.MODID);
    }
    
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(TimeCapsuleItemRegistry.TIME_CAPSULE.get(), ModelTemplates.FLAT_ITEM);
    }
    
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }
    
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }
}
