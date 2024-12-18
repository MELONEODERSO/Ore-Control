package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerValue;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WeightedListIntegerScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        InventoryGui subGui = getSubGui(guiValuesHolder);
        return Builders
                .paged()
                .identifier(Screens.VALUE_WEIGHTED_LIST_INTEGER_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/weighted_list_integer_screen.yml"))
                .addDefaultNextButton()
                .addDefaultPreviousButton()
                .pageContent(Builders
                        .pageContent(DistributionHolder.class)
                        .data((setting, guiInfo) -> getData(guiValuesHolder, guiInfo))
                        .withMessageValue((setting, guiInfo, distributionHolder) -> {
                            String data = guiValuesHolder.valueTraverser().traverse(distributionHolder.data, TraversKey.ofValueSetting("data"));
                            String weight = guiValuesHolder.valueTraverser().traverse(distributionHolder.weight, TraversKey.ofValueSetting("weight")); // TODO: 5/3/23 Was "data" check if this was a bug 
                            return new MessageValue("value-settings", data + "%%new-line%" + weight);
                        })
                        .itemStack((setting, guiInfo, distributionHolder) -> setting.get("value.weighted_list_sub_integer_screen", "default-icon", new ItemStack(Material.STONE)).clone())
                        .withAction((clickAction, distributionHolder) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, distributionHolder) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).addData("weighted_list_integer_data", distributionHolder))
                        .withAction((clickAction, distributionHolder) -> guiValuesHolder.guiManager().openScreen(subGui, clickAction.getPlayer()))
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static InventoryGui getSubGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier("value.weighted_list_sub_integer_screen")
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("value/weighted_list_sub_integer_screen.yml"))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "data", "weighted_list_integer_data", DistributionHolder.class, DistributionHolder::data))
                .addButtonContext(ScreenUtil.getPassthroughButton(guiValuesHolder, "weight", "weighted_list_integer_data", DistributionHolder.class, DistributionHolder::weight))
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("weighted_list_integer_data"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static List<DistributionHolder> getData(GuiValuesHolder guiValuesHolder, GuiInfo guiInfo) {
        PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
        if (!(playerGuiData.getToEditValue() instanceof WeightedListIntegerValue value)) {
            guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", WeightedListIntegerValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
            return Collections.emptyList();
        }

        List<DistributionHolder> list = new LinkedList<>();
        for (Map.Entry<IntegerValue, IntegerValue> entry : value.getDistribution().entrySet()) {
            list.add(new DistributionHolder(entry.getKey(), entry.getValue()));
        }

        return list;
    }

    private record DistributionHolder(IntegerValue data, IntegerValue weight) {
    }
}
