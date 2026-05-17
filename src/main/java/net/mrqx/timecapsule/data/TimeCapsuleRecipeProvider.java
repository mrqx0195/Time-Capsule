package net.mrqx.timecapsule.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.mrqx.timecapsule.registry.TimeCapsuleBlockRegistry;
import net.mrqx.timecapsule.registry.TimeCapsuleItemRegistry;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class TimeCapsuleRecipeProvider extends RecipeProvider {
    protected TimeCapsuleRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }
    
    @Override
    protected void buildRecipes() {
        this.shaped(RecipeCategory.DECORATIONS, TimeCapsuleItemRegistry.TIME_CAPSULE)
            .pattern("CTC")
            .pattern("RBR")
            .pattern("CRC")
            .define('B', Tags.Items.BARRELS)
            .define('C', Tags.Items.INGOTS_COPPER)
            .define('T', Items.CLOCK)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .unlockedBy("has_clock", this.has(Items.CLOCK))
            .save(this.output);
        this.shaped(RecipeCategory.DECORATIONS, TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK)
            .pattern("CCC")
            .pattern("CTC")
            .pattern("CCC")
            .define('C', Tags.Items.INGOTS_COPPER)
            .define('T', TimeCapsuleItemRegistry.TIME_CAPSULE)
            .unlockedBy("has_time_capsule", this.has(TimeCapsuleItemRegistry.TIME_CAPSULE))
            .save(this.output);
    }
    
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }
        
        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new TimeCapsuleRecipeProvider(registries, output);
        }
        
        @Override
        public String getName() {
            return "TimeCapsule Recipes";
        }
    }
}
