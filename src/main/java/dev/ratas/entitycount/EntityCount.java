package dev.ratas.entitycount;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.entitycount.commands.EntityCountCommand;
import dev.ratas.entitycount.config.Messages;
import dev.ratas.entitycount.update.UpdateChecker;

public class EntityCount extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Messages messages;
        try {
            messages = new Messages(this);
        } catch (InvalidConfigurationException e) {
            shutDownWith(e);
            return;
        }
        getCommand("entitycount").setExecutor(new EntityCountCommand(getServer(), messages));
        if (getConfig().getBoolean("enable-metrics", true)) {
            new Metrics(this, 12888);
        }
        // update
        if (getConfig().getBoolean("check-for-updates", true)) {
            new UpdateChecker(this, (response, version) -> {
                switch (response) {
                    case LATEST:
                        getLogger().info(messages.getRunningLatestVersion());
                        break;
                    case FOUND_NEW:
                        getLogger().info(messages.getNewVersionAvailable(version));
                        break;
                    case UNAVAILABLE:
                        getLogger().info(messages.getUpdateInfoUnavailable());
                        break;
                }
            }).check();
        }
    }

    private void shutDownWith(Throwable e) {
        getLogger().warning("Problem while running the plugin. Need to shut down: " + e.getMessage());
        e.printStackTrace();
        getServer().getPluginManager().disablePlugin(this);
    }

}
