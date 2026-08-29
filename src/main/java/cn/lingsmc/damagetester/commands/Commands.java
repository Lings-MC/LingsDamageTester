package cn.lingsmc.damagetester.commands;

import cn.lingsmc.damagetester.commands.subcommands.HelpCommand;
import cn.lingsmc.damagetester.commands.subcommands.ReloadCommand;
import cn.lingsmc.damagetester.constants.MessageConstants;
import cn.lingsmc.damagetester.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static cn.lingsmc.damagetester.constants.CommandConstants.*;
import static cn.lingsmc.damagetester.constants.MessageConstants.UNKNOWN_COMMAND;

/**
 * @author Zoyn, Crsuh2er0
 * @apiNote
 * @since 2023/1/18
 */
public class Commands implements CommandExecutor, TabCompleter {
    /**
     * 初始化指令
     */
    public Commands() {
        registerCommand(HELP, new HelpCommand());
        registerCommand(RELOAD, new ReloadCommand());
    }

    private void registerCommand(String commandName, SubCommand subCommand) {
        if (getCOMMAND_MAP().containsKey(commandName)) {
            Bukkit.getLogger().warning("!");
            return;
        }
        getCOMMAND_MAP().put(commandName, subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Arrays.asList(MessageConstants.getROOT_MESSAGE()).forEach(sender::sendMessage);
            return false;
        }
        if (!getCOMMAND_MAP().containsKey(args[0])) {
            sender.sendMessage(UNKNOWN_COMMAND);
            return false;
        }

        // 第一个参数是 args[0]
        SubCommand subCommand = getCOMMAND_MAP().get(args[0]);
        final String[] finalArgs = StringUtils.toLowerCase(args);
        subCommand.execute(sender, finalArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        final String[] finalArgs = StringUtils.toLowerCase(args);
        if (args.length == 1) {
            list = new ArrayList<>(getCOMMAND_MAP().keySet());
            list.removeIf(s -> !s.startsWith(finalArgs[0]));
        } else {
            SubCommand subCommand = getCOMMAND_MAP().get(finalArgs[0]);
            if (Objects.nonNull(subCommand)) {
                list = subCommand.tabComplete(finalArgs);
            }
        }
        return list;
    }
}
