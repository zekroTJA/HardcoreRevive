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
import java.util.Random;

public class SneakListener implements Listener {

    private final DeathRegister deathRegister;
    private final double sphericalRange;
    private final int sneaksToRevive;
    private final int sneaksFuzziness;

    private int neededSneaks = -1;

    public SneakListener(HardcoreRevive pluginInstance, DeathRegister deathRegister) {
        this.sphericalRange = pluginInstance.getConfig().getDouble("reviveRadius", 16);
        this.sneaksToRevive = pluginInstance.getConfig().getInt("sneaksToRevive", 10);
        this.sneaksFuzziness = pluginInstance.getConfig().getInt("sneaksToRevive", 5);
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

        if (neededSneaks == 0) {
            entries.stream()
                    .filter(p -> p.getPlayer() != event.getPlayer()) // TODO: Re-enable
                    .findFirst().ifPresent(e -> this.revivePlayer(e, event.getPlayer()));
            neededSneaks = -1;
            return;
        }

        if (neededSneaks == -1) {
            Random rand = new Random();
            int rv = rand.nextInt(sneaksFuzziness * 2);
            neededSneaks = sneaksToRevive - sneaksFuzziness + rv;
        }

        neededSneaks--;
    }

    private void revivePlayer(Entry entry, Player reviver) {
        Player player = entry.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(
                String.format("You got revived by %s!", reviver.getName()));
        player.getWorld().getPlayers().forEach(p ->
                p.sendMessage(
                        String.format("%s got revived by %s.", player.getName(), reviver.getName())));

        deathRegister.remove(entry);
    }
}
