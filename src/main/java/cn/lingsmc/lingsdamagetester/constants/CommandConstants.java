package cn.lingsmc.lingsdamagetester.constants;

import cn.lingsmc.lingsdamagetester.commands.SubCommand;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * 根命令别名与子命令名常量，以及子命令注册表。
 *
 * @author Crsuh2er0, 16870
 * @since 2023/1/18
 */
public class CommandConstants {
    public static final String ALIAS = "dt";
    public static final String HELP = "help";
    public static final String RELOAD = "reload";
    public static final String ON = "on";
    public static final String OFF = "off";
    public static final String TOGGLE = "toggle";
    public static final String STATUS = "status";
    @Getter
    private static final Map<String, SubCommand> COMMAND_MAP = Maps.newHashMap();

    private CommandConstants() {

    }
}
