package net.mrqx.timecapsule.client;

import net.mrqx.timecapsule.client.screen.TimeCapsuleScreen;
import net.mrqx.timecapsule.registry.TimeCapsuleMenuRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(Dist.CLIENT)
public final class ClientHandler {
    @SubscribeEvent
    public static void onRegisterMenuScreensEvent(RegisterMenuScreensEvent event) {
        event.register(TimeCapsuleMenuRegistry.TIME_CAPSULE_MENU.get(), TimeCapsuleScreen::new);
    }
}
