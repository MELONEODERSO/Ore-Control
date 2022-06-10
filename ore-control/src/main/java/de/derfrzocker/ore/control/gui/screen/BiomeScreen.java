/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class BiomeScreen {

    private final static String IDENTIFIER = "biome_screen";

    public static InventoryGui getGui(Plugin plugin, OreControlManager oreControlManager, LanguageManager languageManager, OreControlGuiManager guiManager, Function<String, ConfigSetting> settingFunction) {
        return Builders
                .paged()
                .identifier(IDENTIFIER)
                .languageManager(languageManager)
                .withSetting(settingFunction.apply("design.yml"))
                .withSetting(settingFunction.apply("biome_icons.yml"))
                .withSetting(settingFunction.apply("biome_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(Biome.class)
                        .data((setting, guiInfo) -> buildList(oreControlManager, guiManager, guiInfo))
                        .withMessageValue((setting, guiInfo, biome) -> new MessageValue("biome-key", biome.getKey().getKey()))
                        .withMessageValue((setting, guiInfo, biome) -> new MessageValue("biome-namespace", biome.getKey().getNamespace()))
                        .itemStack((setting, guiInfo, biome) -> {
                            String key = "icons." + biome.getKey().getNamespace() + "." + biome.getKey().getKey();
                            ItemStack icon = setting.get(IDENTIFIER, key + ".item-stack", null);
                            if (icon == null) {
                                icon = setting.get(IDENTIFIER, "default-icon.item-stack", new ItemStack(Material.STONE)).clone();
                                String type = setting.get(IDENTIFIER, key + ".type", null);
                                if (type == null) {
                                    plugin.getLogger().info(String.format("No item stack or type found for biome '%s' using default item stack", biome.getKey()));
                                } else {
                                    try {
                                        Material material = Material.valueOf(type.toUpperCase());
                                        icon.setType(material);
                                    } catch (IllegalArgumentException e) {
                                        plugin.getLogger().warning(String.format("Material '%s' for biome '%s' not found", type, biome.getKey()));
                                    }
                                }
                            } else {
                                icon = icon.clone();
                            }
                            return icon;
                        })
                        .withAction((clickAction, biome) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, biome) -> guiManager.getPlayerGuiData(clickAction.getPlayer()).setBiome(biome))
                        .withAction((clickAction, biome) -> guiManager.openFeatureSelectionScreen(clickAction.getPlayer()))
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier("back")
                        .button(Builders
                                .button()
                                .identifier("back")
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                .withAction(clickAction -> guiManager.openConfigInfoScreen(clickAction.getPlayer()))
                        )
                )
                .build();
    }

    private static List<Biome> buildList(OreControlManager oreControlManager, OreControlGuiManager guiManager, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
        ConfigInfo configInfo = playerGuiData.getConfigInfo();

        if (configInfo.getConfigType() == ConfigType.WORLD) {
            World world = Bukkit.getWorld(configInfo.getWorldName());
            if (world != null) {
                return new LinkedList<>(oreControlManager.getBiomes(world));
            }
        }

        return new LinkedList<>(oreControlManager.getRegistries().getBiomeRegistry().getValues().values());
    }
}