package cn.lingsmc.lingsdamagetester.listener;

import cn.lingsmc.lingsdamagetester.LingsDamageTester;
import cn.lingsmc.lingsdamagetester.constants.ConfigConstants;
import cn.lingsmc.lingsdamagetester.log.DamageLogWriter;
import cn.lingsmc.lingsdamagetester.manager.TestModeManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
 * 验证：读取伤害后统一取消事件、受击音效补播、动作条输出、伤害日志写入；v1.3 起不再回血。
 *
 * @author 16870
 * @since 2026/8/29
 */
class DamageListenerTest {
    private static final UUID PLAYER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String PLAYER_NAME = "测试玩家";
    private static final String HURT_SOUND_KEY = "entity.player.hurt";

    @TempDir
    Path tempDir;

    private MockedStatic<LingsDamageTester> pluginStatic;
    private Player player;
    private Player.Spigot playerSpigot;
    private EntityDamageEvent event;
    private DamageListener listener;

    @BeforeEach
    void setUp() {
        pluginStatic = mockStatic(LingsDamageTester.class);

        LingsDamageTester plugin = mock(LingsDamageTester.class);
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getBoolean(ConfigConstants.SHOW_CAUSE, true)).thenReturn(true);
        when(config.getBoolean(ConfigConstants.DAMAGE_LOG, true)).thenReturn(true);
        when(plugin.getConfig()).thenReturn(config);
        pluginStatic.when(LingsDamageTester::getInstance).thenReturn(plugin);

        player = mock(Player.class);
        playerSpigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(playerSpigot);
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(player.isOnline()).thenReturn(true);

        event = mock(EntityDamageEvent.class);
        when(event.getEntity()).thenReturn(player);
        when(event.getCause()).thenReturn(EntityDamageEvent.DamageCause.FALL);

        listener = new DamageListener(new DamageLogWriter(tempDir));
    }

    @AfterEach
    void tearDown() {
        TestModeManager.clear(PLAYER_UUID);
        pluginStatic.close();
    }

    private void enterTestMode() {
        TestModeManager.enable(PLAYER_UUID);
        when(player.getHealth()).thenReturn(20.0D);
        when(event.getFinalDamage()).thenReturn(7.0D);
    }

    private String readLog() throws IOException {
        List<String> lines = Files.readAllLines(tempDir.resolve("damage-" + LocalDate.now() + ".log"));
        assertTrue(lines.size() >= 1, "日志为空");
        return String.join("\n", lines);
    }

    @Test
    @DisplayName("监听注册在 MONITOR 优先级且忽略已取消事件（晚于其它插件的 HIGHEST 处理）")
    void listensAtMonitorPriority() throws NoSuchMethodException {
        EventHandler handler = DamageListener.class.getMethod("onDamage", EntityDamageEvent.class)
                .getAnnotation(EventHandler.class);

        assertNotNull(handler);
        assertEquals(EventPriority.MONITOR, handler.priority());
        assertTrue(handler.ignoreCancelled());
    }

    @Test
    @DisplayName("非玩家实体受伤完全忽略")
    void nonPlayerEntityIgnored() {
        EntityDamageEvent zombieEvent = mock(EntityDamageEvent.class);
        when(zombieEvent.getEntity()).thenReturn(mock(Zombie.class));

        listener.onDamage(zombieEvent);

        verify(zombieEvent, never()).setCancelled(anyBoolean());
        verify(playerSpigot, never()).sendMessage(any(ChatMessageType.class), any(BaseComponent[].class));
    }

    @Test
    @DisplayName("不在测试模式的玩家受伤完全忽略")
    void playerNotInTestModeIgnored() {
        when(event.getFinalDamage()).thenReturn(7.0D);

        listener.onDamage(event);

        verify(event, never()).setCancelled(anyBoolean());
        verify(playerSpigot, never()).sendMessage(any(ChatMessageType.class), any(BaseComponent[].class));
    }

    @Test
    @DisplayName("读取伤害后统一取消事件、补播受击音效、动作条提示")
    void damageCancelledWithFeedback() {
        enterTestMode();

        listener.onDamage(event);

        verify(event).setCancelled(true);
        verify(player).playSound(eq(player), eq(HURT_SOUND_KEY), eq(1.0F), eq(1.0F));
        verify(playerSpigot).sendMessage(eq(ChatMessageType.ACTION_BAR), any(BaseComponent[].class));
    }

    @Test
    @DisplayName("致死伤害同样取消（不再区分致死与否，也不回血）")
    void lethalDamageAlsoCancelled() {
        enterTestMode();
        when(event.getFinalDamage()).thenReturn(25.0D);

        listener.onDamage(event);

        verify(event).setCancelled(true);
        verify(player, never()).setHealth(anyDouble());
    }

    @Test
    @DisplayName("实体攻击写入伤害日志：来源 对 玩家 伤害（原因）")
    void byEntityDamageLogged() throws IOException {
        TestModeManager.enable(PLAYER_UUID);
        when(event.getFinalDamage()).thenReturn(7.5D);
        EntityDamageByEntityEvent byEntityEvent = mock(EntityDamageByEntityEvent.class);
        when(byEntityEvent.getEntity()).thenReturn(player);
        when(byEntityEvent.getCause()).thenReturn(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        when(byEntityEvent.getFinalDamage()).thenReturn(7.5D);
        Entity damager = mock(Zombie.class);
        when(damager.getName()).thenReturn("僵尸");
        when(byEntityEvent.getDamager()).thenReturn(damager);

        listener.onDamage(byEntityEvent);

        assertTrue(readLog().contains("僵尸 对 测试玩家 造成了 7.5 伤害（近战攻击）"), "实际日志: " + readLog());
    }

    @Test
    @DisplayName("环境伤害（无来源实体）记录为「环境」")
    void environmentalDamageLogged() throws IOException {
        enterTestMode();
        when(event.getFinalDamage()).thenReturn(7.5D);

        listener.onDamage(event);

        assertTrue(readLog().contains("环境 对 测试玩家 造成了 7.5 伤害（摔落）"), "实际日志: " + readLog());
    }
}
