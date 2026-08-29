package cn.lingsmc.damagetester.utils;

import cn.lingsmc.damagetester.constants.DamageCauseConstants;
import cn.lingsmc.damagetester.constants.MessageConstants;

/**
 * 伤害数值动作条文案格式化（纯逻辑）。
 * 数值按伤害大小着色：小于 2.0 绿色、小于 10.0 黄色、10.0 及以上红色。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class DamageMessageFormatter {
    /** 中伤害着色下界（含此值起变黄） */
    public static final double MEDIUM_DAMAGE_THRESHOLD = 2.0D;
    /** 大伤害着色下界（含此值起变红） */
    public static final double HIGH_DAMAGE_THRESHOLD = 10.0D;
    private static final String DAMAGE_NUMBER_FORMAT = "%.1f";

    private DamageMessageFormatter() {
    }

    /**
     * 格式化动作条伤害提示。
     *
     * @param damage    最终伤害数值
     * @param causeKey  EntityDamageEvent.DamageCause 的枚举名
     * @param showCause 是否显示来源类型
     * @return 如 "§e7.5 §7伤害 §7(摔落)"
     */
    public static String format(double damage, String causeKey, boolean showCause) {
        StringBuilder builder = new StringBuilder();
        builder.append(damageColor(damage))
                .append(String.format(DAMAGE_NUMBER_FORMAT, damage))
                .append(' ').append(MessageConstants.DAMAGE_LABEL);
        if (showCause) {
            builder.append(String.format(MessageConstants.DAMAGE_CAUSE_FORMAT, DamageCauseConstants.causeName(causeKey)));
        }
        return builder.toString();
    }

    private static String damageColor(double damage) {
        if (damage < MEDIUM_DAMAGE_THRESHOLD) {
            return MessageConstants.COLOR_SUCCESS;
        }
        if (damage < HIGH_DAMAGE_THRESHOLD) {
            return MessageConstants.COLOR_WARNING;
        }
        return MessageConstants.COLOR_ERROR;
    }
}
