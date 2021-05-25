package jodelle.powermining.listeners;

import jodelle.powermining.PowerMining;
import jodelle.powermining.lib.PowerUtils;
import jodelle.powermining.lib.Reference;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ClickPlayerListener implements Listener {
    public PowerMining plugin;
    public boolean useDurabilityPerBlock;


    public ClickPlayerListener(PowerMining plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        useDurabilityPerBlock = plugin.getConfig().getBoolean("useDurabilityPerBlock");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getItemInHand();
        Material handItemType = handItem.getType();
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        if (player == null && !(player instanceof Player))
            return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if (event.getAction() == Action.LEFT_CLICK_AIR)
            return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR)
            return;
        if (player.isSneaking())
            return;
        if (!PowerUtils.checkUsePermission(player, handItemType))
            return;
        if (!PowerUtils.isPowerTool(handItem))
            return;

        Block block = event.getClickedBlock();
        String playerName = player.getName();

        PlayerInteractListener pil = plugin.getPlayerInteractHandler().getListener();
        BlockFace blockFace = pil.getBlockFaceByPlayerName(playerName);

        short curDur = handItem.getDurability();
        short maxDur = handItem.getType().getMaxDurability();

        for (Block e : PowerUtils.getSurroundingBlocksFarm(blockFace, block, Reference.RADIUS)) {
            Material blockMat = e.getType();
            Location blockLoc = e.getLocation();
            console.sendMessage(ChatColor.RED + "[JodellePowerMining] block: " + e.getType().toString());

            boolean usePlow = false;
            boolean usePath = false;

            if (usePlow = PowerUtils.validatePlow(handItem.getType(), blockMat)) ;
            else if (usePath = PowerUtils.validatePath(handItem.getType(), blockMat)) ;

            if (usePlow) {
                console.sendMessage(ChatColor.RED + "[JodellePowerMining] Tilling: " + e.getType().toString());

                e.setType(Material.FARMLAND);
                // Reduce durability for each block
                if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    PowerUtils.reduceDurability(handItem);
                }
                continue;
            }

            if (usePath) {
                e.setType(Material.GRASS_PATH);

                // Reduce durability for each block
                if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    PowerUtils.reduceDurability(handItem);
                }
                continue;
            }
        }

    }
}
