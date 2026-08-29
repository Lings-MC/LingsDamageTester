package cn.lingsmc.lingsdamagetester.listener;

import cn.lingsmc.lingsdamagetester.LingsDamageTester;
import cn.lingsmc.lingsdamagetester.constants.ConfigConstants;
import cn.lingsmc.lingsdamagetester.manager.TestModeManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DamageListener 交互契约测试（Mockito 直测，MockBukkit 暂不支持 26.2）。
 * 验证：致死取消事件、下一 tick 回血调度、动作条输出、非目标零交互。
 *
 * @author 16870
 * @since 2026/8/29
 */
class DamageListenerTest {
    private static final UUID PLAYER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final double FULL_HEALTH = 20.0D;

    private MockedStatic<Bukkit> bukkitStatic;
    private MockedStatic<LingsDamageTester> pluginStatic;
    private BukkitScheduler scheduler;
    private Player player;
    private Player.Spigot playerSpigot;
    private EntityDamageEvent event;
    private DamageListener listener;
    private Runnable pendingHealTask;

    @BeforeEach
    void setUp() {
        bukkitStatic = mockStatic(Bukkit.class);
        pluginStatic = mockStatic(LingsDamageTester.class);

        LingsDamageTester plugin = mock(LingsDamageTester.class);
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getBoolean(ConfigConstants.SHOW_CAUSE, true)).thenReturn(true);
        when(plugin.getConfig()).thenReturn(config);
        pluginStatic.when(LingsDamageTester::getInstance).thenReturn(plugin);

        scheduler = mock(BukkitScheduler.class);
        bukkitStatic.when(Bukkit::getScheduler).thenReturn(scheduler);
        when(scheduler.runTask(any(Plugin.class), any(Runnable.class))).thenAnswer(invocation -> {
            pendingHealTask = invocation.getArgument(1);
            return mock(BukkitTask.class);
        });

        player = mock(Player.class);
        playerSpigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(playerSpigot);
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        when(player.isOnline()).thenReturn(true);

        event = mock(EntityDamageEvent.class);
        when(event.getEntity()).thenReturn(player);
        when(event.getCause()).thenReturn(EntityDamageEvent.DamageCause.FALL);

        // 注入固定满血值，隔离 Bukkit 注册表常量
        listener = new DamageListener(p -> FULL_HEALTH);
    }

    @AfterEach
    void tearDown() {
        TestModeManager.clear(PLAYER_UUID);
        bukkitStatic.close();
        pluginStatic.close();
    }

    private void enterTestMode() {
        TestModeManager.enable(PLAYER_UUID);
        when(player.getHealth()).thenReturn(FULL_HEALTH);
        when(event.getFinalDamage()).thenReturn(7.0D);
    }

    @Test
    @DisplayName("非玩家实体受伤完全忽略")
    void nonPlayerEntityIgnored() {
        EntityDamageEvent zombieEvent = mock(EntityDamageEvent.class);
        when(zombieEvent.getEntity()).thenReturn(mock(Zombie.class));

        listener.onDamage(zombieEvent);

        verify(zombieEvent, never()).setCancelled(anyBoolean());
        verify(scheduler, never()).runTask(any(Plugin.class), any(Runnable.class));
        verify(playerSpigot, never()).sendMessage(any(ChatMessageType.class), any(BaseComponent[].class));
    }

    @Test
    @DisplayName("不在测试模式的玩家受伤完全忽略")
    void playerNotInTestModeIgnored() {
        when(event.getFinalDamage()).thenReturn(7.0D);

        listener.onDamage(event);

        verify(event, never()).setCancelled(anyBoolean());
        verify(scheduler, never()).runTask(any(Plugin.class), any(Runnable.class));
        verify(playerSpigot, never()).sendMessage(any(ChatMessageType.class), any(BaseComponent[].class));
    }

    @Test
    @DisplayName("非致死伤害：不取消事件、动作条提示、下一 tick 回满血")
    void nonLethalDamageHealsNextTick() {
        enterTestMode();

        listener.onDamage(event);

        verify(event, never()).setCancelled(anyBoolean());
        verify(playerSpigot).sendMessage(eq(ChatMessageType.ACTION_BAR), any(BaseComponent[].class));
        assertNotNull(pendingHealTask);
        pendingHealTask.run();
        verify(player).setHealth(FULL_HEALTH);
    }

    @Test
    @DisplayName("致死伤害：取消事件防止死亡并回满血")
    void lethalDamageCancelledAndHealed() {
        enterTestMode();
        when(event.getFinalDamage()).thenReturn(25.0D);

        listener.onDamage(event);

        verify(event).setCancelled(true);
        assertNotNull(pendingHealTask);
        pendingHealTask.run();
        verify(player).setHealth(FULL_HEALTH);
    }

    @Test
    @DisplayName("伤害恰好等于当前血量也视为致死")
    void damageEqualToHealthIsLethal() {
        enterTestMode();
        when(event.getFinalDamage()).thenReturn(FULL_HEALTH);

        listener.onDamage(event);

        verify(event).setCancelled(true);
    }

    @Test
    @DisplayName("玩家离线后回血任务安全跳过")
    void healSkippedWhenOffline() {
        enterTestMode();
        when(player.isOnline()).thenReturn(false);

        listener.onDamage(event);
        pendingHealTask.run();

        verify(player, never()).setHealth(anyDouble());
    }
}
