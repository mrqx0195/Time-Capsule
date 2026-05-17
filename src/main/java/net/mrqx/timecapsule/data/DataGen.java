package net.mrqx.timecapsule.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public final class DataGen {
    @SubscribeEvent
    public static void dataGenClient(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        
        generator.addProvider(true, new TimeCapsuleModelProvider(packOutput));
    }
    
    @SubscribeEvent
    public static void dataGenServer(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        generator.addProvider(true, new TimeCapsuleDataMapProvider(packOutput, lookupProvider));
        generator.addProvider(true, new TimeCapsuleRecipeProvider.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new TimeCapsuleBlockTagsTagProvider(packOutput, lookupProvider));
        generator.addProvider(true, new TimeCapsuleLootTableProvider(packOutput, lookupProvider));
    }
}
