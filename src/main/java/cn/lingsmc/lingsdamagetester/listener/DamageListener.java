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
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.util.function.Function;

/**
 * 测试模式核心监听：读取伤害后统一取消事件、显示伤害数值、补播受击音效、写入测试日志并自动回满血。
 * 满血值获取与日志写入通过构造器注入，隔离 Bukkit 注册表常量，便于单元测试。
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

    /** 满血值提供者；返回 null 表示无法获取（跳过回血） */
    private final Function<Player, Double> maxHealthProvider;
    private final DamageLogWriter damageLogWriter;

    public DamageListener(DamageLogWriter damageLogWriter) {
        this.damageLogWriter = damageLogWriter;
        this.maxHealthProvider = player -> {
            AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
            return attribute == null ? null : attribute.getValue();
        };
    }

    /**
     * 供单元测试注入日志目录与固定满血值，避免触碰 Bukkit 注册表。
     */
    DamageListener(DamageLogWriter damageLogWriter, Function<Player, Double> maxHealthProvider) {
        this.damageLogWriter = damageLogWriter;
        this.maxHealthProvider = maxHealthProvider;
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
        scheduleHeal(player);
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

    private void scheduleHeal(Player player) {
        // 下一 tick 恢复满血：兼顾进入测试模式时已有缺失血量的场景
        Bukkit.getScheduler().runTask(LingsDamageTester.getInstance(), () -> healToFull(player));
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

    /**
     * 恢复满血；玩家离线或属性缺失时安全跳过。
     */
    private void healToFull(Player player) {
        if (!player.isOnline()) {
            return;
        }
        Double maxHealth = maxHealthProvider.apply(player);
        if (maxHealth == null) {
            Bukkit.getLogger().warning("[LingsDamageTester] 玩家 " + player.getName() + " 缺少最大血量属性，跳过回血.");
            return;
        }
        player.setHealth(maxHealth);
    }
}
