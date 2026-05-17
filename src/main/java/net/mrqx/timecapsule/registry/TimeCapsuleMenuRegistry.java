package net.mrqx.timecapsule.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.menu.TimeCapsuleMenu;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TimeCapsuleMenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, TimeCapsule.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<TimeCapsuleMenu>> TIME_CAPSULE_MENU =
        MENUS.register("time_capsule", () -> new MenuType<>(TimeCapsuleMenu::new, FeatureFlagSet.of()));
}
