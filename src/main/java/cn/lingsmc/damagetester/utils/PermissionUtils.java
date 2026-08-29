package cn.lingsmc.damagetester.utils;

import cn.lingsmc.damagetester.constants.MessageConstants;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Crsuh2er0
 * @apiNote
 * @since 2023/1/18
 */
public class PermissionUtils {
    private PermissionUtils() {
    }

    /**
     * @return true if the sender is not op
     * false if the sender is op
     */
    public static boolean nonAdminAuth(@NotNull CommandSender sender) {
        if (sender.isOp()) {
            return false;
        } else {
            sender.sendMessage(MessageConstants.NO_PERMISSION);
            return true;
        }
    }
}
