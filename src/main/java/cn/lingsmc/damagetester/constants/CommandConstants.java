package cn.lingsmc.damagetester.constants;

import cn.lingsmc.damagetester.commands.SubCommand;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * @author Crsuh2er0
 * @apiNote
 * @since 2023/1/18
 */
public class CommandConstants {
    public static final String ALIAS = "st";
    public static final String HELP = "help";
    public static final String RELOAD = "reload";
    @Getter
    private static final Map<String, SubCommand> COMMAND_MAP = Maps.newHashMap();

    private CommandConstants() {

    }
}

