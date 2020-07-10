package de.zekro.hcrevive.deathregister;

import de.zekro.hcrevive.HardcoreRevive;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeathRegister {

    private HardcoreRevive pliginInstance;
    private ArrayList<Entry> register;

    public static final int TIMER_DELAY = 60 * 20;

    public DeathRegister(HardcoreRevive pluginInstance) {
        this.pliginInstance = pluginInstance;
        this.register = new ArrayList<>();

        this.pliginInstance.getServer().getScheduler()
                .runTaskTimer(this.pliginInstance, this::cleanupTimer, TIMER_DELAY, TIMER_DELAY);
    }

    public void register(Player player, long expiresIn) {
        long expires = expiresIn < 1 ? 0 : player.getWorld().getFullTime() + expiresIn;
        Entry entry = new Entry(player, expires);
        this.register.add(entry);
    }

    public List<Entry> get(Location location, double sphericalRadius) {
        return this.register.stream()
                .filter(e -> !e.isExpired())
                .filter(e -> this.isInRange(e.getLocation(), location, sphericalRadius))
                .collect(Collectors.toList());
    }

    public void remove(Entry entry) {
        this.register.remove(entry);
    }

    private boolean isInRange(Location loc1, Location loc2, double sphericalRadius) {
        return loc1.distance(loc2) <= sphericalRadius;
    }

    private void cleanupTimer() {
        this.register.forEach(e -> {
            if (e.isExpired())
                this.remove(e);
        });
    }
}
