package it.ilsommo.nexuslike_list_chat_integration;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Nexus_LikeListChatIntegration extends JavaPlugin {

    private Map<UUID, Long> lastRewardMap;

    @Override
    public void onEnable() {
        System.out.println("List Chat Integration has been enabled");
        this.saveDefaultConfig();
        lastRewardMap = new HashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    TextComponent message = new TextComponent("Se ti stai divertendo su questo Server votalo sulle seguenti liste per farlo crescere!:\n");
                    message.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

                    List<String> links = getConfig().getStringList("links");
                    for (String linkString : links) {
                        TextComponent link = new TextComponent(linkString);
                        link.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, linkString));
                        message.addExtra(link);
                        message.addExtra("\n"); // Aggiungi uno spazio tra i link
                    }
                    TextComponent confirmation = new TextComponent("[Conferma]");
                    confirmation.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                    confirmation.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/confirmclick"));
                    message.addExtra(confirmation);
                    player.spigot().sendMessage(message);
                }
            }
        }.runTaskTimer(this, 0L, 20 * 60 * 20L);  // 20 Minutes
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("confirmclick")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                giveGold(player);
            }
        }
        return true;
    }

    private void giveGold(Player player) {
        if(player.getGameMode().equals(GameMode.SURVIVAL)){
            long lastRewardTime = lastRewardMap.getOrDefault(player.getUniqueId(), 0L);
            if (System.currentTimeMillis() - lastRewardTime < 24 * 60 * 60 * 1000) {
                player.sendMessage("Hai già ricevuto la tua ricompensa di oggi per il voto. Riprova domani :)");
                return;
            }
            lastRewardMap.put(player.getUniqueId(), System.currentTimeMillis());
            player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
        }
    }

    @Override
    public void onDisable() {
        System.out.println("List Chat Integration has been disabled");
    }
}
