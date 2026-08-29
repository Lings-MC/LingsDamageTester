package cn.lingsmc.lingsdamagetester.commands.subcommands;

import cn.lingsmc.lingsdamagetester.commands.SubCommand;
import cn.lingsmc.lingsdamagetester.constants.MessageConstants;
import cn.lingsmc.lingsdamagetester.manager.TestModeManager;
import cn.lingsmc.lingsdamagetester.utils.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 切换测试模式。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class ToggleCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageConstants.CONSOLE);
            return;
        }
        if (PermissionUtils.nonAdminAuth(sender)) {
            return;
        }
        boolean enabled = TestModeManager.toggle(((Player) sender).getUniqueId());
        sender.sendMessage(enabled ? MessageConstants.MODE_ENABLED : MessageConstants.MODE_DISABLED);
    }
}
