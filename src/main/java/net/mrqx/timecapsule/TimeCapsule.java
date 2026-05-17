package net.mrqx.timecapsule;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.mrqx.timecapsule.menu.TimeCapsuleMenu;
import net.mrqx.timecapsule.network.TimeCapsuleSealPayload;
import net.mrqx.timecapsule.registry.TimeCapsuleBlockRegistry;
import net.mrqx.timecapsule.registry.TimeCapsuleItemRegistry;
import net.mrqx.timecapsule.registry.TimeCapsuleMenuRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(TimeCapsule.MODID)
@EventBusSubscriber
public final class TimeCapsule {
    public static final String MODID = "time_capsule";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String VERSION = FMLLoader.getCurrent().getLoadingModList().getModFileById(MODID).versionString();
    
    public static Identifier prefix(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
    
    public TimeCapsule(IEventBus modEventBus, ModContainer modContainer) {
        TimeCapsuleItemRegistry.ITEMS.register(modEventBus);
        TimeCapsuleItemRegistry.DATA_COMPONENTS.register(modEventBus);
        TimeCapsuleBlockRegistry.BLOCKS.register(modEventBus);
        TimeCapsuleBlockRegistry.BLOCK_ENTITIES.register(modEventBus);
        TimeCapsuleMenuRegistry.MENUS.register(modEventBus);
    }
    
    public static Component getMessageNotReady(RandomSource random) {
        return Component.translatable("message.time_capsule.not_ready." + random.nextInt(5)).withStyle(ChatFormatting.RED);
    }
    
    @SubscribeEvent
    public static void onRegisterPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToServer(
            TimeCapsuleSealPayload.TYPE,
            TimeCapsuleSealPayload.STREAM_CODEC,
            (payload, context) -> context.enqueueWork(() -> {
                if (context.player().containerMenu instanceof TimeCapsuleMenu menu) {
                    menu.seal(
                        payload.message(), payload.timeMode(),
                        payload.years(), payload.months(), payload.days(),
                        payload.hours(), payload.minutes(), payload.seconds()
                    );
                }
            })
        );
    }
    
    @SubscribeEvent
    public static void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            event.accept(TimeCapsuleBlockRegistry.TIME_CAPSULE_BLOCK);
            event.accept(TimeCapsuleBlockRegistry.EXPOSED_TIME_CAPSULE_BLOCK);
            event.accept(TimeCapsuleBlockRegistry.WEATHERED_TIME_CAPSULE_BLOCK);
            event.accept(TimeCapsuleBlockRegistry.OXIDIZED_TIME_CAPSULE_BLOCK);
            event.accept(TimeCapsuleBlockRegistry.WAXED_TIME_CAPSULE_BLOCK);
            event.accept(TimeCapsuleBlockRegistry.WAXED_EXPOSED_TIME_CAPSULE_BLOCK);
            event.accept(TimeCapsuleBlockRegistry.WAXED_WEATHERED_TIME_CAPSULE_BLOCK);
            event.accept(TimeCapsuleBlockRegistry.WAXED_OXIDIZED_TIME_CAPSULE_BLOCK);
        } else if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(TimeCapsuleItemRegistry.TIME_CAPSULE);
        }
    }
}
