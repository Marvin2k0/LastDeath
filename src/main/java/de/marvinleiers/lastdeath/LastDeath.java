package de.marvinleiers.lastdeath;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.HashMap;

public final class LastDeath extends JavaPlugin implements Listener
{
    private HashMap<Player, Location> lastDeath = new HashMap<>();

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        onTick();
    }

    private void onTick()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player player : lastDeath.keySet())
                {
                    DecimalFormat format = new DecimalFormat("##.##");
                    Location last = lastDeath.get(player);
                    Location now = player.getLocation().clone();
                    now.setY(last.getY());

                    float angle = player.getLocation().getDirection().angle(now.subtract(last).toVector());

                    String color = "§c";

                    if (angle >= 2.8)
                        color = "§a";

                    String distance = format.format(last.distance(player.getLocation()));
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color + "Letzter Tod: " + distance + " Blöcke"));

                    if (last.distance(player.getLocation()) <= 1)
                        lastDeath.remove(player);
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Location loc = player.getLocation();

        player.sendMessage("§cDu bist bei §e" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " §cgestorben");
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                lastDeath.put(player, loc);
            }
        }.runTaskLater(this, 20);
    }
}
