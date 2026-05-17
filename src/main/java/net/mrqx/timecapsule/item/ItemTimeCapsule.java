package net.mrqx.timecapsule.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.menu.TimeCapsuleMenu;
import net.mrqx.timecapsule.registry.TimeCapsuleItemRegistry;

import java.util.function.Consumer;

public class ItemTimeCapsule extends Item {
    public ItemTimeCapsule(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return ItemTimeCapsule.useTimeCapsule(level, player, hand);
    }
    
    public static InteractionResult useTimeCapsule(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TimeCapsuleData data = stack.get(TimeCapsuleItemRegistry.TIME_CAPSULE_DATA.get());
        if (data == null) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            player.openMenu(new SimpleMenuProvider((id, inv, _) -> new TimeCapsuleMenu(id, inv, hand), Component.translatable("container.time_capsule")));
            player.swing(hand);
            return InteractionResult.CONSUME;
            
        } else {
            if (data.isOpenable(level)) {
                if (!level.isClientSide()) {
                    for (ItemStack item : data.items()) {
                        if (!item.isEmpty()) {
                            if (!player.addItem(item)) {
                                player.drop(item, false);
                            }
                        }
                    }
                    if (!data.message().toString().isBlank()) {
                        player.sendSystemMessage(data.message());
                    }
                    stack.remove(TimeCapsuleItemRegistry.TIME_CAPSULE_DATA.get());
                }
                return InteractionResult.CONSUME;
            } else {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(TimeCapsule.getMessageNotReady(player.getRandom()));
                }
                return InteractionResult.FAIL;
            }
        }
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
