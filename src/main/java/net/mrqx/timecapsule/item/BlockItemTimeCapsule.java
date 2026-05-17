package net.mrqx.timecapsule.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.mrqx.timecapsule.registry.TimeCapsuleItemRegistry;

import java.util.function.Consumer;

public class BlockItemTimeCapsule extends BlockItem {
    public BlockItemTimeCapsule(Block block, Properties properties) {
        super(block, properties.useBlockDescriptionPrefix());
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        TimeCapsuleData data = itemStack.get(TimeCapsuleItemRegistry.TIME_CAPSULE_DATA.get());
        if (data != null) {
            data.addToTooltip(context, builder);
        }
    }
}
