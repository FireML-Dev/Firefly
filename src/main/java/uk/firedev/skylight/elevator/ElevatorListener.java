package uk.firedev.skylight.elevator;

import com.Zrips.CMI.Containers.CMIUser;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import org.checkerframework.checker.index.qual.PolyUpperBound;
import uk.firedev.skylight.Skylight;

import java.util.ArrayList;
import java.util.List;

public class ElevatorListener implements Listener {

    @EventHandler
    public void onStep(PlayerMoveEvent event) {
        if (event.getTo().getBlock() == event.getFrom().getBlock()) {
            return;
        }
        Location stepLoc = event.getTo().clone();
        stepLoc.add(0D, -1D, 0D);
        new Elevator(stepLoc).handleBossBar(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location stepLoc = event.getTo().clone().add(0D, -1D, 0D);
        Player player = event.getPlayer();
        Elevator elevator = new Elevator(stepLoc);

        // Make elevators compatible with CMI
        if (elevator.isElevator() && Skylight.getInstance().isPluginEnabled("CMI")) {
            CMIUser cmiUser = new CMIUser(player);
            cmiUser.setLastTeleportLocation(event.getFrom());
        }

        Skylight.getScheduler().runTaskLater(() -> elevator.handleBossBar(player), 5L);
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Location stepLoc = event.getFrom().add(0D, -1D, 0D);
        Elevator elevator = new Elevator(stepLoc);
        if (elevator.isElevator()) {
            Elevator next = elevator.getNext();
            ElevatorManager.getInstance().teleportPlayer(player, next);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location stepLoc = event.getPlayer().getLocation().add(0D, -1D, 0D);
        Elevator elevator = new Elevator(stepLoc);
        if (elevator.isElevator()) {
            Elevator previous = elevator.getPrevious();
            ElevatorManager.getInstance().teleportPlayer(player, previous);
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Elevator.hideBossBar(event.getPlayer());
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Location stepLoc = event.getPlayer().getLocation().add(0D, -1D, 0D);
        new Elevator(stepLoc).handleBossBar(event.getPlayer());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!ElevatorManager.getInstance().isElevatorBlock(event.getItemInHand())) {
            return;
        }
        new Elevator(event.getBlock().getLocation()).setElevator(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Elevator elevator = new Elevator(event.getBlock().getLocation());
        if (!elevator.isElevator()) {
            return;
        }
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ElevatorManager.getInstance().getElevatorBlock());
        elevator.setElevator(false);
    }

    // BLOCK HANDLING

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        List<Elevator> elevators = event.getBlocks().stream().map(Elevator::new).filter(Elevator::isElevator).toList();
        if (!elevators.isEmpty()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        List<Elevator> elevators = event.getBlocks().stream().map(Elevator::new).filter(Elevator::isElevator).toList();
        if (!elevators.isEmpty()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFall(EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.FALLING_BLOCK) {
            return;
        }
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplodes(BlockExplodeEvent event) {
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
        new ArrayList<>(event.blockList()).forEach(block -> {
            Elevator nElevator = new Elevator(block);
            if (nElevator.isElevator()) {
                event.blockList().remove(block);
            }
        });
    }

    @EventHandler
    public void onEntityExplodes(EntityExplodeEvent event) {
        new ArrayList<>(event.blockList()).forEach(block -> {
            Elevator nElevator = new Elevator(block);
            if (nElevator.isElevator()) {
                event.blockList().remove(block);
            }
        });
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent event) {
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrime(TNTPrimeEvent event) {
        Elevator elevator = new Elevator(event.getBlock());
        if (elevator.isElevator()) {
            event.setCancelled(true);
        }
    }

}
