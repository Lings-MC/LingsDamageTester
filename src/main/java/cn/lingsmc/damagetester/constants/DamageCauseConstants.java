package cn.lingsmc.damagetester.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 伤害原因（EntityDamageEvent.DamageCause 枚举名）到中文展示名的映射。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class DamageCauseConstants {
    private static final Map<String, String> CAUSE_NAMES = new HashMap<>();

    static {
        CAUSE_NAMES.put("ENTITY_ATTACK", "近战攻击");
        CAUSE_NAMES.put("ENTITY_SWEEP_ATTACK", "横扫攻击");
        CAUSE_NAMES.put("PROJECTILE", "弹射物");
        CAUSE_NAMES.put("FALL", "摔落");
        CAUSE_NAMES.put("FIRE", "火焰");
        CAUSE_NAMES.put("FIRE_TICK", "燃烧");
        CAUSE_NAMES.put("LAVA", "岩浆");
        CAUSE_NAMES.put("DROWNING", "溺水");
        CAUSE_NAMES.put("BLOCK_EXPLOSION", "方块爆炸");
        CAUSE_NAMES.put("ENTITY_EXPLOSION", "实体爆炸");
        CAUSE_NAMES.put("VOID", "虚空");
        CAUSE_NAMES.put("STARVATION", "饥饿");
        CAUSE_NAMES.put("MAGIC", "魔法");
        CAUSE_NAMES.put("WITHER", "凋零");
        CAUSE_NAMES.put("POISON", "中毒");
        CAUSE_NAMES.put("CONTACT", "仙人掌");
        CAUSE_NAMES.put("SUFFOCATION", "窒息");
        CAUSE_NAMES.put("LIGHTNING", "雷击");
        CAUSE_NAMES.put("THORNS", "荆棘");
        CAUSE_NAMES.put("FALLING_BLOCK", "落下的方块");
        CAUSE_NAMES.put("CRAMMING", "挤压");
        CAUSE_NAMES.put("HOT_FLOOR", "岩浆块");
        CAUSE_NAMES.put("DRAGON_BREATH", "龙息");
        CAUSE_NAMES.put("SONIC_BOOM", "音波");
        CAUSE_NAMES.put("KILL", "清除指令");
        CAUSE_NAMES.put("FLY_INTO_WALL", "撞墙");
    }

    private DamageCauseConstants() {
    }

    /**
     * @return 伤害原因的中文展示名；未收录的原因回退为枚举名
     */
    public static String causeName(String causeKey) {
        return CAUSE_NAMES.getOrDefault(causeKey, causeKey);
    }
}
