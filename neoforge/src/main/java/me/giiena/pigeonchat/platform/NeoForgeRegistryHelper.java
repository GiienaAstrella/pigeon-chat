package me.giiena.pigeonchat.platform;

import me.giiena.pigeonchat.platform.services.IRegistryHelper;
import net.minecraft.world.item.CreativeModeTab;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return CreativeModeTab.builder();
    }
}
