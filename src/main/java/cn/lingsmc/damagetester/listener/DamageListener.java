package cn.lingsmc.damagetester.listener;

import cn.lingsmc.damagetester.DamageTester;
import cn.lingsmc.damagetester.manager.TestModeManager;
import cn.lingsmc.damagetester.utils.ConfigUtils;
import cn.lingsmc.damagetester.utils.DamageMessageFormatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.function.Function;

/**
 * 测试模式核心监听：显示伤害数值、防致死并自动回满血。
 * 满血值获取通过构造器注入，隔离 Bukkit 注册表常量，便于单元测试。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class DamageListener implements Listener {
    /** 满血值提供者；返回 null 表示无法获取（跳过回血） */
    private final Function<Player, Double> maxHealthProvider;

    public DamageListener() {
        this.maxHealthProvider = player -> {
            AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
            return attribute == null ? null : attribute.getValue();
        };
    }

    /**
     * 供单元测试注入固定满血值，避免触碰 Bukkit 注册表。
     */
    DamageListener(Function<Player, Double> maxHealthProvider) {
        this.maxHealthProvider = maxHealthProvider;
    }

    /**
     * 注册监听器（模板静态注册模式）。
     */
    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new DamageListener(), DamageTester.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!TestModeManager.isInTestMode(player.getUniqueId())) {
            return;
        }
        double damage = event.getFinalDamage();
        // 原版在事件结束后结算伤害：血量撑不住本次伤害则取消事件防止死亡
        if (player.getHealth() - damage <= 0.0D) {
            event.setCancelled(true);
        }
        showDamage(player, damage, event.getCause().name());
        scheduleHeal(player);
    }

    private void showDamage(Player player, double damage, String causeKey) {
        String message = DamageMessageFormatter.format(damage, causeKey, ConfigUtils.showCause());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private void scheduleHeal(Player player) {
        // 下一 tick 恢复满血：事件处理器内直接改血量会被原版随后的伤害结算覆盖
        Bukkit.getScheduler().runTask(DamageTester.getInstance(), () -> healToFull(player));
    }

    /**
     * 恢复满血；玩家离线或属性缺失时安全跳过。
     */
    private void healToFull(Player player) {
        if (!player.isOnline()) {
            return;
        }
        Double maxHealth = maxHealthProvider.apply(player);
        if (maxHealth == null) {
            Bukkit.getLogger().warning("[DamageTester] 玩家 " + player.getName() + " 缺少最大血量属性，跳过回血.");
            return;
        }
        player.setHealth(maxHealth);
    }
}
