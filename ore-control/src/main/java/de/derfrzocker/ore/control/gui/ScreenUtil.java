package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;
import de.derfrzocker.ore.control.gui.info.InfoLinkData;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.gui.builders.ButtonContextBuilder;
import de.derfrzocker.spigot.utils.language.Language;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public final class ScreenUtil {

    private ScreenUtil() {
    }

    public static ItemStack getIcon(GuiValuesHolder guiValuesHolder, Setting setting, String identifier, Keyed keyed) {
        String key = "icons." + keyed.getKey().getNamespace() + "." + keyed.getKey().getKey();
        ItemStack icon = setting.get(identifier, key + ".item-stack", null);
        if (icon == null) {
            icon = setting.get(identifier, "default-icon.item-stack", new ItemStack(Material.STONE)).clone();
            String type = setting.get(identifier, key + ".type", null);
            if (type == null) {
                guiValuesHolder.plugin().getLogger().info(String.format("No item stack or type found for '%s' using default item stack", keyed.getKey()));
            } else {
                try {
                    Material material = Material.valueOf(type.toUpperCase());
                    icon.setType(material);
                } catch (IllegalArgumentException e) {
                    guiValuesHolder.plugin().getLogger().warning(String.format("Material '%s' for '%s' not found", type, keyed.getKey()));
                }
            }
        } else {
            icon = icon.clone();
        }
        return icon;
    }

    public static ButtonContextBuilder getBackButton(OreControlGuiManager guiManager) {
        return Builders
                .buttonContext()
                .identifier("back")
                .button(Builders
                        .button()
                        .identifier("back")
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> guiManager.getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory().onBack(clickAction.getPlayer()))
                        .withAction(clickAction -> guiManager.openScreen(guiManager.getPlayerGuiData(clickAction.getPlayer()).pollFirstInventory(), clickAction.getPlayer()))
                );
    }

    public static <T extends Value<?, ?, ?>> ButtonContextBuilder getPassthroughButton(GuiValuesHolder guiValuesHolder, String identifier, Class<T> valueClass, Function<T, Value<?, ?, ?>> toEditFunction) {
        return Builders
                .buttonContext()
                .identifier(identifier)
                .button(Builders
                        .button()
                        .identifier(identifier)
                        .withMessageValue((setting, guiInfo) -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
                            Value<?, ?, ?> currently = guiData.getToEditValue();
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            return new MessageValue("value-settings", guiValuesHolder.valueTraverser().traverse(toEdit, TraversKey.ofValueType(toEdit.getValueType().getKey())));
                        })
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                            Value<?, ?, ?> currently = guiData.getToEditValue();
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, guiData.getToEditValue() != null ? guiData.getToEditValue().getClass() : "null"));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            guiData.setToEditValue(toEdit);
                            guiValuesHolder.guiManager().openValueScreen(clickAction.getPlayer(), toEdit);
                        })
                );
    }

    public static <T> ButtonContextBuilder getPassthroughButton(GuiValuesHolder guiValuesHolder, String identifier, String dataKey, Class<T> valueClass, Function<T, Value<?, ?, ?>> toEditFunction) {
        return Builders
                .buttonContext()
                .identifier(identifier)
                .button(Builders
                        .button()
                        .identifier(identifier)
                        .withMessageValue((setting, guiInfo) -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity());
                            Object currently = guiData.getData(dataKey);
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, currently.getClass()));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            return new MessageValue("value-settings", guiValuesHolder.valueTraverser().traverse(toEdit, TraversKey.ofValueType(toEdit.getValueType().getKey())));
                        })
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> {
                            PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                            Object currently = guiData.getData(dataKey);
                            if (!valueClass.isAssignableFrom(currently.getClass())) {
                                guiValuesHolder.plugin().getLogger().warning(String.format("Expected a value of type '%s' but got one of type '%s', this is a bug!", valueClass, currently.getClass()));
                            }
                            Value<?, ?, ?> toEdit = toEditFunction.apply((T) currently);
                            guiData.setToEditValue(toEdit);
                            guiValuesHolder.guiManager().openValueScreen(clickAction.getPlayer(), toEdit);
                        })
                );
    }

    public static ButtonContextBuilder getInfoButton(GuiValuesHolder guiValuesHolder, InfoLinkData... infoLinkData) {
        return Builders
                .buttonContext()
                .identifier("info")
                .button(Builders
                        .button()
                        .identifier("info")
                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                        .withAction(clickAction -> {
                            guiValuesHolder.stats().incrementUsefulLinksClicksCount();

                            Language language = guiValuesHolder.languageManager().getLanguage(clickAction.getPlayer());
                            PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                            for (String header : MessageUtil.format(language, language.getSetting().get("useful-links.header", "HEADER STRING NOT PRESENT"))) {
                                clickAction.getPlayer().sendMessage(header);
                            }
                            String prefixString = MessageUtil.formatToString(language, language.getSetting().get("useful-links.prefix", "PREFIX STRING NOT PRESENT"), MessageUtil.StringSeparator.SPACE);
                            BaseComponent[] prefix = TextComponent.fromLegacyText(prefixString);

                            String hoverString = MessageUtil.formatToString(language, language.getSetting().get("useful-links.hover", "HOVER STRING NOT PRESENT"), MessageUtil.StringSeparator.SPACE);
                            BaseComponent[] hoverComponent = TextComponent.fromLegacyText(hoverString);
                            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverComponent));

                            for (InfoLinkData infoLink : infoLinkData) {
                                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, infoLink.url(playerGuiData));
                                String questionString = MessageUtil.formatToString(language, language.getSetting().get("useful-links.questions." + infoLink.infoLink(), "NO QUESTIONS FOUND FOR " + infoLink.infoLink()), MessageUtil.StringSeparator.SPACE, infoLink.messageValues(playerGuiData));
                                BaseComponent[] questions = TextComponent.fromLegacyText(questionString);

                                for (BaseComponent component : questions) {
                                    component.setClickEvent(clickEvent);
                                    component.setHoverEvent(hoverEvent);
                                }

                                BaseComponent[] messages = new BaseComponent[prefix.length + questions.length];
                                System.arraycopy(prefix, 0, messages, 0, prefix.length);
                                System.arraycopy(questions, 0, messages, prefix.length, questions.length);

                                clickAction.getPlayer().spigot().sendMessage(messages);
                            }

                            for (String footer : MessageUtil.format(language, language.getSetting().get("useful-links.footer", "FOOTER STRING NOT PRESENT"))) {
                                clickAction.getPlayer().sendMessage(footer);
                            }
                            clickAction.getPlayer().closeInventory();
                        })
                );
    }
}
