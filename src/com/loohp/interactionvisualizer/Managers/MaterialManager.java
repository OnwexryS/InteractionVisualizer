package com.loohp.interactionvisualizer.Managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.Utils.MaterialUtils;

import net.md_5.bungee.api.ChatColor;

public class MaterialManager {

	public static FileConfiguration config;
	public static File file;

	public static void setup() {
		if (!InteractionVisualizer.plugin.getDataFolder().exists()) {
			InteractionVisualizer.plugin.getDataFolder().mkdir();
		}
		file = new File(InteractionVisualizer.plugin.getDataFolder(), "material.yml");
		if (!file.exists()) {
			try {
				InputStream in = InteractionVisualizer.plugin.getClass().getClassLoader().getResourceAsStream("material.yml");
	            Files.copy(in, file.toPath());
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The material.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create the material.yml file");
			}
		}
        
        config = YamlConfiguration.loadConfiguration(file);
        MaterialUtils.setup();
        saveConfig();
	}

	public static FileConfiguration getMaterialConfig() {
		return config;
	}

	public static void saveConfig() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(file);
		MaterialUtils.setup();
	}
}