package de.zekro.hcrevive.listeners;

import de.zekro.hcrevive.HardcoreRevive;
import de.zekro.hcrevive.deathregister.DeathRegister;
import de.zekro.hcrevive.deathregister.Entry;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

public class SneakListener implements Listener {

    private final DeathRegister deathRegister;
    private final double sphericalRange;

    public SneakListener(HardcoreRevive pluginInstance, DeathRegister deathRegister) {
        this.sphericalRange = pluginInstance.getConfig().getDouble("reviveRadius", 16);
        this.deathRegister = deathRegister;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        // Only continue if started sneaking.
        if (!event.isSneaking())
            return;

        List<Entry> entries = deathRegister.get(event.getPlayer().getLocation(), this.sphericalRange);
        if (entries.size() == 0)
            return;

        entries.stream()
                .filter(p -> p.getPlayer() != event.getPlayer())
                .forEach(e -> this.revivePlayer(e.getPlayer(), event.getPlayer()));
    }

    private void revivePlayer(Player player, Player reviver) {
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(
                String.format("You got revived by %s!", reviver.getName()));
        player.getWorld().getPlayers().forEach(p ->
                p.sendMessage(
                        String.format("%s got revived by %s.", player.getName(), reviver.getName())));
    }
}
