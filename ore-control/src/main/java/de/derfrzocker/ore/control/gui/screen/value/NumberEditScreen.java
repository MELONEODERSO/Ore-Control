package de.derfrzocker.ore.control.gui.screen.value;

import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.gui.builders.SingleInventoryGuiBuilder;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class NumberEditScreen {

    private static final String DEFAULT_ICON = "default-icon";

    public static InventoryGui getFixedDoubleToIntegerGui(GuiValuesHolder guiValuesHolder) {
        return getGui(guiValuesHolder,
                playerGuiData -> ((FixedDoubleToIntegerValue) playerGuiData.getToEditValue()).getValue(),
                (playerGuiData, number) -> {
                    if (!(playerGuiData.getToEditValue() instanceof FixedDoubleToIntegerValue value)) {
                        guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedDoubleToIntegerValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
                        return;
                    }
                    value.setValue(value.getValue() + number.doubleValue());
                })
                .identifier(Screens.VALUE_FIXED_DOUBLE_TO_INTEGER_SCREEN)
                .withSetting(guiValuesHolder.settingFunction().apply("value/fixed_double_to_integer_screen.yml"))
                .build();
    }

    public static InventoryGui getFixedFloatGui(GuiValuesHolder guiValuesHolder) {
        return getGui(guiValuesHolder,
                playerGuiData -> ((FixedFloatValue) playerGuiData.getToEditValue()).getValue(),
                (playerGuiData, number) -> {
                    if (!(playerGuiData.getToEditValue() instanceof FixedFloatValue value)) {
                        guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", FixedFloatValue.class, playerGuiData.getToEditValue() != null ? playerGuiData.getToEditValue().getClass() : "null"));
                        return;
                    }
                    value.setValue(value.getValue() + number.floatValue());
                })
                .identifier(Screens.VALUE_FIXED_FLOAT_SCREEN)
                .withSetting(guiValuesHolder.settingFunction().apply("value/fixed_float_screen.yml"))
                .build();
    }

    private static SingleInventoryGuiBuilder getGui(GuiValuesHolder guiValuesHolder, Function<PlayerGuiData, Number> numberSupplier, BiConsumer<PlayerGuiData, Number> numberConsumer) {
        return Builders
                .single()
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("feature_icons.yml"))
                .addListButton(Builders
                        .listButton()
                        .identifier("values")
                        .withMessageValue(((setting, guiInfo, value) -> new MessageValue("value", value)))
                        .withAction((clickAction, value) -> clickAction.getClickEvent().setCancelled(true))
                        .withAction((clickAction, value) -> numberConsumer.accept(guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()), NumberConversions.toDouble(value)))
                        .withAction((clickAction, value) -> guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer()).apply(guiValuesHolder.plugin(), guiValuesHolder.oreControlManager()))
                        .withAction((clickAction, value) -> clickAction.getInventoryGui().updatedSoft())
                )
                .addButtonContext(Builders
                        .buttonContext()
                        .identifier(DEFAULT_ICON)
                        .button(Builders
                                .button()
                                .identifier(DEFAULT_ICON)
                                .withMessageValue((setting, guiInfo) -> new MessageValue("feature-name", guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).getFeature().getKey()))
                                .withMessageValue((setting, guiInfo) -> new MessageValue("setting-name", guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).getSettingWrapper().getSetting().name()))
                                .withMessageValue((setting, guiInfo) -> new MessageValue("current-value", numberSupplier.apply(guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()))))
                                .itemStack((setting, guiInfo) -> ScreenUtil.getIcon(guiValuesHolder, setting, null, guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).getFeature()))
                                .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        )
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).setPreviousToEditValue())
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()));
    }
}