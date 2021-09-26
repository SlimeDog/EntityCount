package dev.ratas.entitycount;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.entitycount.commands.EntityCountCommand;
import dev.ratas.entitycount.config.Messages;

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
        if (getConfig().getBoolean("bstats-enabled", true)) {
            new Metrics(this, 12888);
        }
    }

    private void shutDownWith(Throwable e) {
        getLogger().warning("Problem while running the plugin. Need to shut down: " + e.getMessage());
        e.printStackTrace();
        getServer().getPluginManager().disablePlugin(this);
    }

}
