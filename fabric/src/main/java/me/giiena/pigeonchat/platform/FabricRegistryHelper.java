package me.giiena.pigeonchat.platform;

import me.giiena.pigeonchat.platform.services.IRegistryHelper;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.world.item.CreativeModeTab;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return FabricCreativeModeTab.builder();
    }
}
