package de.zekro.hcrevive.listeners;

import de.zekro.hcrevive.deathregister.DeathRegister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener class binding the {@link PlayerQuitEvent}.
 *
 * When a player quits the server and has an entry
 * int the death register, the entry is removed.
 */
public class QuitListener implements Listener {

    private final DeathRegister deathRegister;

    /**
     * Initialized a new {@link QuitListener} instance.
     * @param deathRegister death register instance
     */
    public QuitListener(DeathRegister deathRegister) {
        this.deathRegister = deathRegister;
    }

    /**
     * {@link PlayerQuitEvent} event listener.
     * @param event player quit event
     */
    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        this.deathRegister.remove(event.getPlayer());
    }
}
