package cn.lingsmc.damagetester.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Crsuh2er0
 * @apiNote
 * @since 2024/1/1
 */
public class StringUtils {
    private StringUtils() {
    }

    @NotNull
    @Contract(pure = true)
    public static String[] toLowerCase(@NotNull String[] strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = strings[i].toLowerCase();
        }
        return result;
    }
}
