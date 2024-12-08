package uk.firedev.firefly.modules.elevator;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import uk.firedev.firefly.Firefly;

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

        Bukkit.getScheduler().runTaskLater(Firefly.getInstance(), () -> elevator.handleBossBar(player), 5L);
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Location stepLoc = event.getFrom().add(0D, -1D, 0D);
        Elevator elevator = new Elevator(stepLoc);
        if (elevator.isElevator()) {
            Elevator next = elevator.getNext();
            ElevatorModule.getInstance().teleportPlayer(player, next);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            return;
        }
        Player player = event.getPlayer();
        Location stepLoc = event.getPlayer().getLocation().add(0D, -1D, 0D);
        Elevator elevator = new Elevator(stepLoc);
        if (elevator.isElevator()) {
            Elevator previous = elevator.getPrevious();
            ElevatorModule.getInstance().teleportPlayer(player, previous);
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
        if (!ElevatorModule.getInstance().isElevatorBlock(event.getItemInHand())) {
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
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ElevatorModule.getInstance().getElevatorBlock());
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
