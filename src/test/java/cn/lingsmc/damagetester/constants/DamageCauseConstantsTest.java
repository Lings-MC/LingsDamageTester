package cn.lingsmc.damagetester.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DamageCauseConstants 伤害原因中文名映射测试。
 *
 * @author 16870
 * @since 2026/8/29
 */
class DamageCauseConstantsTest {

    @Test
    @DisplayName("常用伤害原因有中文映射")
    void commonCausesAreMapped() {
        assertEquals("摔落", DamageCauseConstants.causeName("FALL"));
        assertEquals("近战攻击", DamageCauseConstants.causeName("ENTITY_ATTACK"));
        assertEquals("弹射物", DamageCauseConstants.causeName("PROJECTILE"));
        assertEquals("岩浆", DamageCauseConstants.causeName("LAVA"));
        assertEquals("燃烧", DamageCauseConstants.causeName("FIRE_TICK"));
        assertEquals("溺水", DamageCauseConstants.causeName("DROWNING"));
        assertEquals("虚空", DamageCauseConstants.causeName("VOID"));
        assertEquals("饥饿", DamageCauseConstants.causeName("STARVATION"));
    }

    @Test
    @DisplayName("未收录的原因回退为枚举名")
    void unknownCauseFallsBack() {
        assertEquals("NOT_MAPPED", DamageCauseConstants.causeName("NOT_MAPPED"));
    }
}
