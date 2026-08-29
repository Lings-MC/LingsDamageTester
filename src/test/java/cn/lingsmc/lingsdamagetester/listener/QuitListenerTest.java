package cn.lingsmc.lingsdamagetester.listener;

import cn.lingsmc.lingsdamagetester.manager.TestModeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * QuitListener 退出清理测试。
 *
 * @author 16870
 * @since 2026/8/29
 */
class QuitListenerTest {
    private static final UUID PLAYER_UUID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @AfterEach
    void tearDown() {
        TestModeManager.clear(PLAYER_UUID);
    }

    @Test
    @DisplayName("玩家退出后测试模式被清除")
    void quitClearsTestMode() {
        TestModeManager.enable(PLAYER_UUID);
        assertTrue(TestModeManager.isInTestMode(PLAYER_UUID));

        new QuitListener().onQuit(quitEvent());

        assertFalse(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("不在测试模式的玩家退出无副作用")
    void quitWithoutTestModeIsNoop() {
        new QuitListener().onQuit(quitEvent());

        assertFalse(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    private PlayerQuitEvent quitEvent() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        PlayerQuitEvent event = mock(PlayerQuitEvent.class);
        when(event.getPlayer()).thenReturn(player);
        return event;
    }
}
