package cn.lingsmc.lingsdamagetester.listener;

import cn.lingsmc.lingsdamagetester.LingsDamageTester;
import cn.lingsmc.lingsdamagetester.constants.DamageCauseConstants;
import cn.lingsmc.lingsdamagetester.log.DamageLogWriter;
import cn.lingsmc.lingsdamagetester.manager.TestModeManager;
import cn.lingsmc.lingsdamagetester.utils.ConfigUtils;
import cn.lingsmc.lingsdamagetester.utils.DamageMessageFormatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

/**
 * 测试模式核心监听：读取伤害后统一取消事件、显示伤害数值、补播受击音效并写入测试日志。
 * 伤害被取消后血量不会下降，无需回血逻辑。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class DamageListener implements Listener {
    /** 统一补播的原版玩家受击音效键（事件取消后原版不再播放，改用字符串键规避注册表常量） */
    private static final String HURT_SOUND_KEY = "entity.player.hurt";
    /** 伤害日志中无来源实体时的占位文案 */
    private static final String ENVIRONMENT_DAMAGER = "环境";
    /** 弹射物发射器为方块时的展示文案 */
    private static final String BLOCK_SHOOTER_NAME = "发射器";

    private final DamageLogWriter damageLogWriter;

    public DamageListener(DamageLogWriter damageLogWriter) {
        this.damageLogWriter = damageLogWriter;
    }

    /**
     * 注册监听器（模板静态注册模式）。
     */
    public static void initialize(DamageLogWriter damageLogWriter) {
        Bukkit.getPluginManager().registerEvents(new DamageListener(damageLogWriter), LingsDamageTester.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!TestModeManager.isInTestMode(player.getUniqueId())) {
            return;
        }
        double damage = event.getFinalDamage();
        String causeKey = event.getCause().name();
        // 读取到最终伤害后统一取消：不再真实扣血，致死伤害也走同一反馈路径
        event.setCancelled(true);
        showDamage(player, damage, causeKey);
        playHurtFeedback(player);
        writeDamageLog(player, damage, causeKey, event);
    }

    private void showDamage(Player player, double damage, String causeKey) {
        String message = DamageMessageFormatter.format(damage, causeKey, ConfigUtils.showCause());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private void playHurtFeedback(Player player) {
        // 取消事件后原版不再播放受击音效，对本人补播一次，保证每次受击反馈一致
        player.playSound(player, HURT_SOUND_KEY, 1.0F, 1.0F);
    }

    private void writeDamageLog(Player player, double damage, String causeKey, EntityDamageEvent event) {
        if (!ConfigUtils.damageLog()) {
            return;
        }
        damageLogWriter.write(player.getName(), damagerName(event), damage, DamageCauseConstants.causeName(causeKey));
    }

    /**
     * 解析伤害来源展示名：弹射物溯源到发射者，无来源实体时记为「环境」。
     */
    private static String damagerName(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return ENVIRONMENT_DAMAGER;
        }
        var damager = ((EntityDamageByEntityEvent) event).getDamager();
        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof org.bukkit.entity.Entity shooterEntity) {
                return shooterEntity.getName();
            }
            if (shooter instanceof BlockProjectileSource) {
                return BLOCK_SHOOTER_NAME;
            }
        }
        return damager.getName();
    }
}
