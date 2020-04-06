package com.loohp.interactionvisualizer.Blocks;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.Utils.EntityCreator;
import com.loohp.interactionvisualizer.Utils.PacketSending;

public class StonecutterDisplay implements Listener {
	
	public static HashMap<Block, HashMap<String, Object>> openedStonecutter = new HashMap<Block, HashMap<String, Object>>();

	@EventHandler
	public void onUseStonecutter(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		if (event.getView().getTopInventory() == null) {
			return;
		}
		if (!(event.getView().getTopInventory() instanceof StonecutterInventory)) {
			return;
		}
		if (!player.getTargetBlockExact(7, FluidCollisionMode.NEVER).getType().equals(Material.STONECUTTER)) {
			return;
		}
		
		if (event.getRawSlot() >= 0 && event.getRawSlot() <= 1) {
			PacketSending.sendHandMovement(InteractionVisualizer.getOnlinePlayers(), (Player) event.getWhoClicked());
		}
	}
	
	@EventHandler
	public void onDragStonecutter(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		if (event.getView().getTopInventory() == null) {
			return;
		}
		if (!(event.getView().getTopInventory() instanceof StonecutterInventory)) {
			return;
		}
		if (!player.getTargetBlockExact(7, FluidCollisionMode.NEVER).getType().equals(Material.STONECUTTER)) {
			return;
		}
		
		for (int slot : event.getRawSlots()) {
			if (slot >= 0 && slot <= 1) {
				PacketSending.sendHandMovement(InteractionVisualizer.getOnlinePlayers(), (Player) event.getWhoClicked());
				break;
			}
		}
	}
	
	@EventHandler
	public void onCloseStonecutter(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		if (event.getView().getTopInventory() == null) {
			return;
		}
		if (!(event.getView().getTopInventory() instanceof StonecutterInventory)) {
			return;
		}
		if (!player.getTargetBlockExact(7, FluidCollisionMode.NEVER).getType().equals(Material.STONECUTTER)) {
			return;
		}
		
		Block block = player.getTargetBlockExact(7, FluidCollisionMode.NEVER);
		
		if (!openedStonecutter.containsKey(block)) {
			return;
		}
		
		HashMap<String, Object> map = openedStonecutter.get(block);
		if (!map.get("Player").equals((Player) event.getPlayer())) {
			return;
		}
		
		if (map.get("Item") instanceof Item) {
			Entity entity = (Entity) map.get("Item");
			PacketSending.removeItem(InteractionVisualizer.getOnlinePlayers(), (Item) entity);
		}
		openedStonecutter.remove(block);
	}
	
	public static int run() {		
		return new BukkitRunnable() {
			public void run() {
				
				for (Player player : InteractionVisualizer.getOnlinePlayers()) {
					if (player.getGameMode().equals(GameMode.SPECTATOR)) {
						continue;
					}
					if (player.getOpenInventory() == null) {
						continue;
					}
					if (player.getOpenInventory().getTopInventory() == null) {
						continue;
					}
					if (!(player.getOpenInventory().getTopInventory() instanceof StonecutterInventory)) {
						continue;
					}
					if (!player.getTargetBlockExact(7, FluidCollisionMode.NEVER).getType().equals(Material.STONECUTTER)) {
						continue;
					}
					
					InventoryView view = player.getOpenInventory();
					Block block = player.getTargetBlockExact(7, FluidCollisionMode.NEVER);
					Location loc = block.getLocation();
					if (!openedStonecutter.containsKey(block)) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("Player", player);
						map.put("Item", "N/A");
						openedStonecutter.put(block, map);
					}
					HashMap<String, Object> map = openedStonecutter.get(block);
					
					if (!map.get("Player").equals(player)) {
						continue;
					}
					
					ItemStack input = view.getItem(0);
					if (input != null) {
						if (input.getType().equals(Material.AIR)) {
							input = null;
						}
					}
					ItemStack output = view.getItem(1);
					if (output != null) {
						if (output.getType().equals(Material.AIR)) {
							output = null;
						}
					}
					
					ItemStack itemstack = null;
					if (output == null) {
						if (input != null) {
							itemstack = input;
						}
					} else {
						itemstack = output;
					}
					
					if (itemstack != null) {
						ItemStack itempar = itemstack.clone();
						int taskid = new BukkitRunnable() {
							public void run() {
								player.getWorld().spawnParticle(Particle.ITEM_CRACK, loc.clone().add(0.5, 0.7, 0.5), 25, 0.1, 0.1, 0.1, 0.1, itempar);
							}
						}.runTaskTimerAsynchronously(InteractionVisualizer.plugin, 0, 1).getTaskId();
						new BukkitRunnable() {
							public void run() {
								Bukkit.getScheduler().cancelTask(taskid);
							}
						}.runTaskLaterAsynchronously(InteractionVisualizer.plugin, 4);
					}
					
					Item item = null;
					if (map.get("Item") instanceof String) {
						if (itemstack != null) {
							item = (Item) EntityCreator.create(loc.clone().add(0.5, 0.75, 0.5), EntityType.DROPPED_ITEM);
							item.setItemStack(itemstack);
							item.setVelocity(new Vector(0, 0, 0));
							item.setPickupDelay(32767);
							item.setGravity(false);
							map.put("Item", item);
							PacketSending.sendItemSpawn(InteractionVisualizer.itemDrop, item);
							PacketSending.updateItem(InteractionVisualizer.getOnlinePlayers(), item);
						} else {
							map.put("Item", "N/A");
						}
					} else {
						item = (Item) map.get("Item");
						if (itemstack != null) {
							if (!item.getItemStack().equals(itemstack)) {
								item.setItemStack(itemstack);
								PacketSending.updateItem(InteractionVisualizer.getOnlinePlayers(), item);
							}
							item.setPickupDelay(32767);
							item.setGravity(false);
						} else {
							map.put("Item", "N/A");
							PacketSending.removeItem(InteractionVisualizer.getOnlinePlayers(), item);
							item.remove();
						}
					}
				}
				
			}
		}.runTaskTimer(InteractionVisualizer.plugin, 0, 5).getTaskId();
	}
}
