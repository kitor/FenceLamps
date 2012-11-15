package pl.kitor.FenceLamps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class FenceLamps extends JavaPlugin {

    private static PluginDescriptionFile package_description = null;
    private FPListener listener;

    public void enablePlugin() {
        getServer().getPluginManager().enablePlugin(this);
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("fencelamps")) {
            if (args.length != 0) {
                switch (args[0]) {
                    case "reload":
                        this.reloadConfig();
                        this.doConfig();
                        sender.sendMessage("Config file reloaded");
                        return true;
                    default:
                        break;
                }
            }
        }
        sender.sendMessage("Usage:");
        return false;
    }

    private void doConfig() {
        this.saveDefaultConfig();
        listener.MAX_V = this.getConfig().getInt("MAX_V");
        listener.LAMP_DATA = (byte) this.getConfig().getInt("LAMP_DATA");
        listener.LAMP_OFF = this.getConfig().getInt("LAMP_OFF");
        listener.LAMP_ON = this.getConfig().getInt("LAMP_ON");
        if (this.getConfig().getInt("FENCE_TYPE") == 0) {
            listener.FENCE = 85;                                                // normal fence
        } else {
            listener.FENCE = 113;                                               // redstonebrick fence
        }
        getLogger().info("Config file loaded");
    }

    @Override
    public void onEnable() {
        package_description = getDescription();
        listener = new FPListener(this);
        this.doConfig();
        getLogger().info("enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("disabled");
    }
}
