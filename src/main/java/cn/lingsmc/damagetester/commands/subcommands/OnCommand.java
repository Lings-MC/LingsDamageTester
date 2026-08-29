package cn.lingsmc.damagetester.commands.subcommands;

import cn.lingsmc.damagetester.commands.SubCommand;
import cn.lingsmc.damagetester.constants.MessageConstants;
import cn.lingsmc.damagetester.manager.TestModeManager;
import cn.lingsmc.damagetester.utils.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 进入测试模式。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class OnCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageConstants.CONSOLE);
            return;
        }
        if (PermissionUtils.nonAdminAuth(sender)) {
            return;
        }
        Player player = (Player) sender;
        if (TestModeManager.isInTestMode(player.getUniqueId())) {
            sender.sendMessage(MessageConstants.ALREADY_IN_TEST_MODE);
            return;
        }
        TestModeManager.enable(player.getUniqueId());
        sender.sendMessage(MessageConstants.MODE_ENABLED);
    }
}
