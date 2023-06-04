package dev.ratas.entitycount;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.entitycount.commands.EntityCountCommand;
import dev.ratas.entitycount.config.Messages;

import dev.ratas.slimedogcore.impl.SlimeDogCore;
import dev.ratas.slimedogcore.impl.utils.UpdateChecker;

public class EntityCount extends SlimeDogCore {
    private static final int SPIGOT_ID = 96546;
    private Messages messages;

    @Override
    public void pluginEnabled() {
        saveDefaultConfig();
        try {
            messages = new Messages(this);
        } catch (InvalidConfigurationException e) {
            shutDownWith(e);
            return;
        }
        getCommand("entitycount").setExecutor(new EntityCountCommand(this, getServer(), messages));
        if (getConfig().getBoolean("enable-metrics", true)) {
            new Metrics(this, 12888);
        }
        // update
        if (getConfig().getBoolean("check-for-updates", true)) {
            UpdateChecker.forSpigot(this, (response, version) -> {
                switch (response) {
                    case LATEST:
                        getLogger().info(messages.getRunningLatestVersion().getMessage().getRaw());
                        break;
                    case FOUND_NEW:
                        getLogger().info(messages.getNewVersionAvailable().createWith(version).getRaw());
                        break;
                    case UNAVAILABLE:
                        getLogger().info(messages.getUpdateInfoUnavailable().getMessage().getRaw());
                        break;
                }
            }, SPIGOT_ID).check();
        }
    }

    @Override
    public void pluginDisabled() {
    }

    private void shutDownWith(Throwable e) {
        getLogger().warning("Problem while running the plugin. Need to shut down: " + e.getMessage());
        e.printStackTrace();
        getServer().getPluginManager().disablePlugin(this);
    }

    public boolean reload() {
        reloadConfig();
        try {
            messages.reloadConfig();
        } catch (RuntimeException e) {
            shutDownWith(e);
            return false;
        }
        return true;
    }

}
