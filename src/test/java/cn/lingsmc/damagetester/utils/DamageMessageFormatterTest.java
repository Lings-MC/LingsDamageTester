package cn.lingsmc.damagetester.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DamageMessageFormatter 动作条文案格式化测试。
 *
 * @author 16870
 * @since 2026/8/29
 */
class DamageMessageFormatterTest {

    @Test
    @DisplayName("小伤害（<2.0）绿色且带来源")
    void smallDamageIsGreen() {
        assertEquals("§a1.5 §7伤害 §7(摔落)", DamageMessageFormatter.format(1.5D, "FALL", true));
    }

    @Test
    @DisplayName("中伤害（[2.0,10.0)）黄色")
    void mediumDamageIsYellow() {
        assertEquals("§e7.4 §7伤害 §7(近战攻击)", DamageMessageFormatter.format(7.4D, "ENTITY_ATTACK", true));
    }

    @Test
    @DisplayName("大伤害（>=10.0）红色")
    void highDamageIsRed() {
        assertEquals("§c12.3 §7伤害 §7(岩浆)", DamageMessageFormatter.format(12.3D, "LAVA", true));
    }

    @Test
    @DisplayName("着色阈值边界：2.0 归中伤，10.0 归大伤")
    void colorThresholdBoundaries() {
        assertTrue(DamageMessageFormatter.format(2.0D, "FALL", false).startsWith("§e2.0"));
        assertTrue(DamageMessageFormatter.format(10.0D, "FALL", false).startsWith("§c10.0"));
        assertTrue(DamageMessageFormatter.format(1.9D, "FALL", false).startsWith("§a1.9"));
        assertTrue(DamageMessageFormatter.format(9.9D, "FALL", false).startsWith("§e9.9"));
    }

    @Test
    @DisplayName("showCause 为 false 时不显示来源")
    void noCauseWhenDisabled() {
        assertEquals("§a0.0 §7伤害", DamageMessageFormatter.format(0.0D, "FALL", false));
    }

    @Test
    @DisplayName("未收录的伤害原因回退显示枚举名")
    void unknownCauseFallsBackToEnumName() {
        assertEquals("§e3.0 §7伤害 §7(CUSTOM_X)", DamageMessageFormatter.format(3.0D, "CUSTOM_X", true));
    }
}
