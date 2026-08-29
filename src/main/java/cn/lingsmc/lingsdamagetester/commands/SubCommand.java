package cn.lingsmc.lingsdamagetester.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * 表示一个子命令
 *
 * @author Zoyn
 * @since 2023-1-14
 */
public interface SubCommand {
    /**
     * 执行命令
     *
     * @param sender 发送者
     * @param args   参数
     */
    void execute(CommandSender sender, String[] args);

    /**
     * 执行tab补全
     *
     * @param args 参数
     * @return 可以补全的指令
     */
    default List<String> tabComplete(String[] args) {
        return Collections.emptyList();
    }
}