package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.MessageValue;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.derfrzocker.ore.control.OreControlMessages.*;

public class CreateCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(sender))
            return false;

        if (args.length != 1) {
            CREATE_NOT_ENOUGH_ARGS.sendMessage(sender);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(OreControl.getInstance(), () -> {
            final String configName = args[0];

            final OreControlService service = OreControl.getService();

            final World world = Bukkit.getWorld(configName);

            final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(configName);

            if (optionalWorldOreConfig.isPresent() || world != null) {
                CREATE_NAME_ALREADY_EXIST.sendMessage(sender, new MessageValue("config_name", configName));
                return;
            }

            service.createWorldOreConfigTemplate(configName);

            CREATE_SUCCESS.sendMessage(sender, new MessageValue("config_name", configName));

        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}