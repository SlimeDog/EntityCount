package dev.ratas.entitycount.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;

import dev.ratas.slimedogcore.api.messaging.factory.SDCDoubleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCVoidContextMessageFactory;
import dev.ratas.slimedogcore.impl.SlimeDogCore;
import dev.ratas.slimedogcore.impl.messaging.MessagesBase;
import dev.ratas.slimedogcore.impl.messaging.factory.MsgUtil;

public class Messages extends MessagesBase {
    private static final String NAME = "messages.yml";
    private SDCSingleContextMessageFactory<Integer> header;
    private SDCDoubleContextMessageFactory<EntityType, Integer> item;
    private SDCSingleContextMessageFactory<String> noWorldFound;
    private SDCSingleContextMessageFactory<String> noEntityTypeFound;
    private SDCVoidContextMessageFactory runningLatestVersion;
    private SDCSingleContextMessageFactory<String> newVersionAvailable;
    private SDCVoidContextMessageFactory updateInfoUnavailable;
    private SDCVoidContextMessageFactory reloadSuccessful;
    private SDCVoidContextMessageFactory reloadFailed;

    public Messages(SlimeDogCore plugin) throws InvalidConfigurationException {
        super(plugin.getCustomConfigManager().getConfig(NAME));
        load();
    }

    private void load() {
        header = MsgUtil.singleContext("{total}", total -> String.valueOf(total),
                getRawMessage("header", "Found a total of {total} mobs:"));
        item = MsgUtil.doubleContext("{type}", type -> type.name(), "{amount}", amount -> String.valueOf(amount),
                getRawMessage("item", "{type}: {amount}"));
        noWorldFound = MsgUtil.singleContext("{name}", name -> name,
                getRawMessage("no-world-found", "World not found: {name}"));
        noEntityTypeFound = MsgUtil.singleContext("{name}", name -> name,
                getRawMessage("no-entity-type-found", "Entity type not found: {name}"));
        runningLatestVersion = MsgUtil
                .voidContext(getRawMessage("update-running-latest-version", "Running latest version of EntityCount"));
        newVersionAvailable = MsgUtil.singleContext("{version}", version -> version,
                getRawMessage("update-new-version-available", "A new version of EntityCount is available: {version}"));
        updateInfoUnavailable = MsgUtil
                .voidContext(getRawMessage("update-info-unavailable", "Update info is not available at this time"));
        reloadSuccessful = MsgUtil.voidContext(getRawMessage("reloaded", "Successfully reloaded"));
        reloadFailed = MsgUtil.voidContext(getRawMessage("reload-failed", "Failed to reload. Shutting down plugin."));
    }

    public void reload() {
        super.reload();
        load();
    }

    public SDCSingleContextMessageFactory<Integer> getHeader() {
        return header;
    }

    public SDCDoubleContextMessageFactory<EntityType, Integer> getItem() {
        return item;
    }

    public SDCSingleContextMessageFactory<String> getNoWorldFound() {
        return noWorldFound;
    }

    public SDCSingleContextMessageFactory<String> getNoEntityTypeFound() {
        return noEntityTypeFound;
    }

    public SDCVoidContextMessageFactory getRunningLatestVersion() {
        return runningLatestVersion;
    }

    public SDCSingleContextMessageFactory<String> getNewVersionAvailable() {
        return newVersionAvailable;
    }

    public SDCVoidContextMessageFactory getUpdateInfoUnavailable() {
        return updateInfoUnavailable;
    }

    public SDCVoidContextMessageFactory getRelaoded() {
        return reloadSuccessful;
    }

    public SDCVoidContextMessageFactory getReloadFailed() {
        return reloadFailed;
    }

}
