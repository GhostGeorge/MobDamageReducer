package me.ghostgeorge.mobDamageReducer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MobDamageReducer extends JavaPlugin implements Listener {

    private boolean enabled = true; // Whether the plugin is enabled or not
    private DifficultyLevel targetDifficulty = DifficultyLevel.EASY; // Default difficulty level
    private final Map<EntityType, double[]> damageMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Registering the events
        Bukkit.getPluginManager().registerEvents(this, this);

        // Save default config if not present
        saveDefaultConfig();

        // Load settings from config
        enabled = getConfig().getBoolean("enabled", true);
        targetDifficulty = DifficultyLevel.valueOf(getConfig().getString("difficulty", "EASY").toUpperCase());

        // Define damage values for each mob type (EASY, NORMAL)
        damageMap.put(EntityType.ZOMBIE, new double[]{4, 6});
        damageMap.put(EntityType.ZOMBIE_VILLAGER, new double[]{4, 6});
        damageMap.put(EntityType.SKELETON, new double[]{2, 4});
        damageMap.put(EntityType.CREEPER, new double[]{24, 43});
        damageMap.put(EntityType.SPIDER, new double[]{2, 2});
        damageMap.put(EntityType.DROWNED, new double[]{4, 6});
        damageMap.put(EntityType.HUSK, new double[]{4, 6});
        damageMap.put(EntityType.STRAY, new double[]{2, 4});
        damageMap.put(EntityType.WITCH, new double[]{6, 6});
        damageMap.put(EntityType.PILLAGER, new double[]{4, 6});
        damageMap.put(EntityType.VINDICATOR, new double[]{9, 13});
        damageMap.put(EntityType.EVOKER, new double[]{6, 6});
        damageMap.put(EntityType.RAVAGER, new double[]{6, 10});
        damageMap.put(EntityType.PHANTOM, new double[]{2, 4});
        damageMap.put(EntityType.SLIME, new double[]{3, 4});
        damageMap.put(EntityType.MAGMA_CUBE, new double[]{3, 6});
        damageMap.put(EntityType.ENDERMAN, new double[]{4, 6});
        damageMap.put(EntityType.BLAZE, new double[]{4, 6});
        damageMap.put(EntityType.WITHER_SKELETON, new double[]{5, 8});
        damageMap.put(EntityType.GUARDIAN, new double[]{4, 6});
        damageMap.put(EntityType.ELDER_GUARDIAN, new double[]{6, 8});
        damageMap.put(EntityType.ZOGLIN, new double[]{6, 10});
        damageMap.put(EntityType.ZOMBIFIED_PIGLIN, new double[]{5, 7});
        damageMap.put(EntityType.WITHER, new double[]{5, 8});
        damageMap.put(EntityType.WARDEN, new double[]{16, 22});
        damageMap.put(EntityType.SHULKER, new double[]{4, 6});
        damageMap.put(EntityType.ILLUSIONER, new double[]{4, 6});

        getLogger().info("MobDamageReducer enabled with " + targetDifficulty + " damage level.");

        // Register TabCompleter for command autocompletion
        this.getCommand("mobdamagereducer").setTabCompleter(new MobDamageReducerTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic if needed
        getLogger().info("MobDamageReducer disabled");
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!enabled) return;

        EntityType entityType = event.getEntity().getType();
        if (damageMap.containsKey(entityType)) {
            double[] damageValues = damageMap.get(entityType);
            double damage = (targetDifficulty == DifficultyLevel.NORMAL) ? damageValues[1] : (targetDifficulty == DifficultyLevel.EASY) ? damageValues[0] : 0;
            event.setDamage(damage);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!enabled) {  // Check if the plugin is disabled
            sender.sendMessage("§cThe MobDamageReducer plugin is currently disabled.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /mobdamagereducer <easy|normal|invincible|on|off|help>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "easy":
                targetDifficulty = DifficultyLevel.EASY;
                sender.sendMessage("§aMob damage set to EASY.");
                break;
            case "normal":
                targetDifficulty = DifficultyLevel.NORMAL;
                sender.sendMessage("§aMob damage set to NORMAL.");
                break;
            case "invincible":
                targetDifficulty = DifficultyLevel.INVINCIBLE;
                sender.sendMessage("§aMob damage set to INVINCIBLE (0).");
                break;
            case "on":
                enabled = true;
                sender.sendMessage("§aMobDamageReducer is now ON.");
                break;
            case "off":
                enabled = false;
                sender.sendMessage("§cMobDamageReducer is now OFF.");
                break;
            case "help":
                sender.sendMessage("§a=== MobDamageReducer Help ===");
                sender.sendMessage("§7/mobdamagereducer easy - Set mob damage to EASY.");
                sender.sendMessage("§7/mobdamagereducer normal - Set mob damage to NORMAL.");
                sender.sendMessage("§7/mobdamagereducer invincible - Set mob damage to INVINCIBLE (0 damage).");
                sender.sendMessage("§7/mobdamagereducer on - Enable the MobDamageReducer plugin.");
                sender.sendMessage("§7/mobdamagereducer off - Disable the MobDamageReducer plugin.");
                sender.sendMessage("§7/mobdamagereducer help - Show this help message.");
                break;
            default:
                sender.sendMessage("§cInvalid option. Use: easy, normal, invincible, on, off, or help.");
        }
        return true;
    }

    private enum DifficultyLevel {
        EASY,
        NORMAL,
        INVINCIBLE
    }

    // TabCompleter for autocompletion
    public class MobDamageReducerTabCompleter implements TabCompleter {
        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                suggestions.add("easy");
                suggestions.add("normal");
                suggestions.add("invincible");
                suggestions.add("on");
                suggestions.add("off");
                suggestions.add("help");
            }
            return suggestions;
        }
    }
}
