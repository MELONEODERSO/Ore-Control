package de.derfrzocker.ore.control.gui.screen;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.Feature;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.common.feature.placement.ActivationModifier;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigType;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.OreControlGuiManager;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.ore.control.gui.info.InfoLink;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static de.derfrzocker.ore.control.gui.info.InfoLinkData.of;

public class FeatureSelectionScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .paged()
                .identifier(Screens.FEATURE_SELECTION_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("feature_icons.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("feature_selection_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(Feature.class)
                        .data((setting, guiInfo) -> buildList(guiValuesHolder.oreControlManager(), guiValuesHolder.guiManager(), guiInfo))
                        .withMessageValue((setting, guiInfo, feature) -> new MessageValue("feature-key", feature.getKey().getKey()))
                        .withMessageValue((setting, guiInfo, feature) -> new MessageValue("feature-namespace", feature.getKey().getNamespace()))
                        .withMessageValue((setting, guiInfo, feature) -> new MessageValue("generator-settings", getGeneratorSetting(guiValuesHolder, guiInfo, feature)))
                        .withMessageValue((setting, guiInfo, feature) -> new MessageValue("placement-modifier-settings", getPlacementModifierSetting(guiValuesHolder, guiInfo, feature)))
                        .itemStack((setting, guiInfo, feature) -> ScreenUtil.getIcon(guiValuesHolder, setting, Screens.FEATURE_SELECTION_SCREEN, feature))
                        .withAction((clickAction, feature) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, feature) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).setFeature(feature))
                        .withAction((clickAction, feature) -> guiValuesHolder.guiManager().openScreen(Screens.FEATURE_SETTINGS_SCREEN, clickAction.getPlayer()))
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setBiome(null))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .addButtonContext(ScreenUtil.getInfoButton(guiValuesHolder,
                        of(InfoLink.INVENTORY_GUI_SCREENS_EXPLAINED, "Choose-A-Feature", "feature_selection_screen"),
                        of(InfoLink.COMMON_QUESTIONS, "why-are-there-multiple-features-with-the-same-ore")
                ))
                .build();
    }

    public static String getGeneratorSetting(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo, Feature feature) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        Optional<Config> optionalConfig;
        if (playerGuiData.getBiome() == null) {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), feature.getKey());
        } else {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), feature.getKey());
        }

        if (optionalConfig.isEmpty()) {
            guiValuesHolder.plugin().getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
            return "UNKNOWN";
        }

        Config config = optionalConfig.get();
        Configuration configuration = config.getFeature();

        return "§r§f%%translation:[feature-generators." + feature.generator().getKey().getNamespace() + "." + feature.generator().getKey().getKey() + ".name]%:%%new-line%" + guiValuesHolder.valueTraverser().traverse(configuration);
    }

    public static String getPlacementModifierSetting(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo, Feature feature) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        Optional<Config> optionalConfig;
        if (playerGuiData.getBiome() == null) {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), feature.getKey());
        } else {
            optionalConfig = guiValuesHolder.oreControlManager().getConfigManager().getGuiConfig(playerGuiData.getConfigInfo(), playerGuiData.getBiome(), feature.getKey());
        }

        if (optionalConfig.isEmpty()) {
            guiValuesHolder.plugin().getLogger().warning(String.format("No gui specific config found, it should always be possible to build one with default value, this is a bug!"));
            return "UNKNOWN";
        }

        Config config = optionalConfig.get();
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<FeaturePlacementModifier<?>, PlacementModifierConfiguration> entry : config.getPlacements().entrySet()) {
            if (entry.getKey().getKey().equals(ActivationModifier.KEY)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                stringBuilder.append("%%new-line%");
            }
            stringBuilder.append("§r§f");
            stringBuilder.append("%%translation:[placement-modifiers.");
            stringBuilder.append(entry.getKey().getKey().getNamespace());
            stringBuilder.append(".");
            stringBuilder.append(entry.getKey().getKey().getKey());
            stringBuilder.append(".name]%:");
            stringBuilder.append("%%new-line%");
            stringBuilder.append(guiValuesHolder.valueTraverser().traverse(entry.getValue()));
        }

        return stringBuilder.toString();
    }

    private static List<Feature> buildList(OreControlManager oreControlManager, OreControlGuiManager guiManager, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiManager.getPlayerGuiData((Player) guiInfo.getEntity());
        ConfigInfo configInfo = playerGuiData.getConfigInfo();

        if (playerGuiData.getBiome() != null) {
            return new LinkedList<>(playerGuiData.getBiome().getFeatures());
        } else {
            if (configInfo.getConfigType() == ConfigType.WORLD) {
                World world = Bukkit.getWorld(configInfo.getWorldName());
                if (world != null) {
                    Set<Feature> features = new LinkedHashSet<>();
                    for (Biome biome : oreControlManager.getBiomes(world)) {
                        features.addAll(biome.getFeatures());
                    }
                    return new LinkedList<>(features);
                }
            }

            return new LinkedList<>(oreControlManager.getRegistries().getFeatureRegistry().getValues().values());
        }
    }
}
