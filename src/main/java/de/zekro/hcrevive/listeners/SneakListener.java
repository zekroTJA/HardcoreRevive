package de.zekro.hcrevive.listeners;

import de.zekro.hcrevive.HardcoreRevive;
import de.zekro.hcrevive.deathregister.DeathRegister;
import de.zekro.hcrevive.deathregister.Entry;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;
import java.util.Random;

/**
 * Listener class binding the {@link PlayerToggleSneakEvent}.
 *
 * Revives a player when the specified amount +- a random
 * fuzziness amount of sneak toggles was registered in the
 * range of a registered player death.
 */
public class SneakListener implements Listener {

    private final DeathRegister deathRegister;
    private final double sphericalRange;
    private final int sneaksToRevive;
    private final int sneaksFuzziness;
    private final boolean reviveAtDeathPosition;

    private int neededSneaks = -1;

    /**
     * Initializes a new {@link SneakListener}.
     * @param pluginInstance The plugin instance
     * @param deathRegister The death register instance
     */
    public SneakListener(HardcoreRevive pluginInstance, DeathRegister deathRegister) {
        this.sphericalRange = pluginInstance.getConfig().getDouble("reviveRadius", 16);
        this.sneaksToRevive = pluginInstance.getConfig().getInt("sneaksToRevive", 10);
        this.sneaksFuzziness = pluginInstance.getConfig().getInt("sneaksToRevive", 5);
        this.reviveAtDeathPosition = pluginInstance.getConfig().getBoolean("reviveAtDeathPosition", true);
        this.deathRegister = deathRegister;
    }

    /**
     * {@link PlayerToggleSneakEvent} event listener.
     * @param event player toggle sneak event
     */
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        // Only continue if started sneaking.
        if (!event.isSneaking())
            return;

        // Check if registered deaths are in the specified range.
        List<Entry> entries = deathRegister.get(event.getPlayer().getLocation(), this.sphericalRange);
        if (entries.size() == 0)
            return;

        // When neededSneaks is 0, the first player in the results
        // list will be revived when the player is not the invoking
        // player itself.
        // After that, neededSneaks is set to -1.
        if (neededSneaks == 0) {
            entries.stream()
                    .filter(p -> p.getPlayer() != event.getPlayer()) // TODO: Re-enable
                    .findFirst().ifPresent(e -> this.revivePlayer(e, event.getPlayer()));
            neededSneaks = -1;
            return;
        }

        // When neededSneaks is -1 ('uninitialized state'), initialize
        // neededSneaks with sneaksToRevive and a random fuzziness deviation.
        if (neededSneaks == -1) {
            Random rand = new Random();
            int rv = rand.nextInt(this.sneaksFuzziness * 2);
            neededSneaks = this.sneaksToRevive - this.sneaksFuzziness + rv;
        }

        // Count down the toggled sneak.
        neededSneaks--;
    }

    /**
     * Revive the {@link Player} by the given {@link Entry} instance.
     * @param entry Death Entry
     * @param reviver Player invoking the revive
     */
    private void revivePlayer(Entry entry, Player reviver) {
        Player player = entry.getPlayer();

        Location respawnLocation;

        if (this.reviveAtDeathPosition) {
            respawnLocation = entry.getLocation();
        }
        else {
            respawnLocation = player.getBedSpawnLocation();
            if (respawnLocation == null)
                respawnLocation = player.getWorld().getSpawnLocation();
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(respawnLocation);

        player.sendMessage(
                String.format("You got revived by %s!", reviver.getName()));
        player.getWorld().getPlayers().forEach(p ->
                p.sendMessage(
                        String.format("%s got revived by %s.", player.getName(), reviver.getName())));

        deathRegister.remove(entry);
    }
}
