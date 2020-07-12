package de.zekro.hcrevive.commands;

import de.zekro.hcrevive.deathregister.DeathRegister;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command executor for /hcrvFlushRegister command.
 */
public class FlushRegister implements CommandExecutor {

    private final DeathRegister deathRegister;

    /**
     * Initialize a new instance of {@link FlushRegister}.
     * @param deathRegister death register instance
     */
    public FlushRegister(DeathRegister deathRegister) {
        this.deathRegister = deathRegister;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to use this command.");
            return false;
        }

        this.deathRegister.flush();
        sender.sendMessage(ChatColor.GREEN + "Flushed all death register entries.");

        return true;
    }
}
