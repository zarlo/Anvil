package org.anvilpowered.anvil.core.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.Plugin;
import org.anvilpowered.anvil.core.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.core.nukkit.listeners.NukkitPlayerListener;
import org.anvilpowered.anvil.core.nukkit.module.NukkitModule;
import org.anvilpowered.anvil.nukkit.module.ApiNukkitModule;

public class AnvilCoreNukkit extends PluginBase implements Plugin<AnvilCoreNukkit> {

    protected Environment environment;

    public AnvilCoreNukkit() {
        Anvil.environmentBuilder()
            .setName(AnvilCorePluginInfo.id)
            .addModules(new NukkitModule(), new ApiNukkitModule())
            .whenReady(e -> environment = e)
            .register(this);
    }

    @Override
    public void onEnable() {
        Anvil.completeInitialization();
        Server.getInstance().getPluginManager().registerEvents(environment.getInjector().getInstance(NukkitPlayerListener.class), this);
    }

    @Override
    public AnvilCoreNukkit getPluginContainer() {
        return this;
    }
}
