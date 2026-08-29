package cn.lingsmc.lingsdamagetester.commands.subcommands;

import cn.lingsmc.lingsdamagetester.commands.SubCommand;
import cn.lingsmc.lingsdamagetester.constants.MessageConstants;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Crsuh2er0
 * @apiNote
 * @since 2023/1/18
 */
public class HelpCommand implements SubCommand {
    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Arrays.asList(MessageConstants.getHELP_MESSAGE()).forEach(sender::sendMessage);
    }
}
