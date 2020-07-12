package de.zekro.hcrevive.util;

import org.bukkit.World;
import org.bukkit.World.Environment;

/**
 * Some utility functions for {@link org.bukkit.World} objects.
 */
public class WorldUtil {

    /**
     * Returns the name of the world by {@link Environment}
     * which is either 'Overworld', 'Nether' or
     * 'The End'. If the environment is neither of them,
     * it returns the worlds name.
     * @param world the world object
     * @return the name
     */
    public static String getName(World world) {
        Environment env = world.getEnvironment();

        switch (env) {
            case NORMAL:
                return "Overworld";

            case NETHER:
                return "Nether";

            case THE_END:
                return "The End";

            default:
                return world.getName();
        }
    }
}
