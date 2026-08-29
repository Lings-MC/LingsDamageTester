package cn.lingsmc.lingsdamagetester.commands.subcommands;

import cn.lingsmc.lingsdamagetester.constants.MessageConstants;
import cn.lingsmc.lingsdamagetester.manager.TestModeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 测试模式开关命令（on/off/toggle/status）单元测试。
 *
 * @author 16870
 * @since 2026/8/29
 */
class ModeCommandsTest {
    private static final UUID PLAYER_UUID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final String[] NO_ARGS = new String[0];

    private Player opPlayer;
    private CommandSender console;
    private Player nonOpPlayer;

    @BeforeEach
    void setUp() {
        TestModeManager.clear(PLAYER_UUID);
        opPlayer = mock(Player.class);
        when(opPlayer.getUniqueId()).thenReturn(PLAYER_UUID);
        when(opPlayer.isOp()).thenReturn(true);
        nonOpPlayer = mock(Player.class);
        when(nonOpPlayer.getUniqueId()).thenReturn(PLAYER_UUID);
        when(nonOpPlayer.isOp()).thenReturn(false);
        console = mock(CommandSender.class);
    }

    @AfterEach
    void tearDown() {
        TestModeManager.clear(PLAYER_UUID);
    }

    @Test
    @DisplayName("on: 控制台执行被拒绝")
    void consoleBlockedForOn() {
        new OnCommand().execute(console, NO_ARGS);

        verify(console).sendMessage(MessageConstants.CONSOLE);
        assertFalse(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("on: 非 Op 玩家被拒绝")
    void nonOpRejectedForOn() {
        new OnCommand().execute(nonOpPlayer, NO_ARGS);

        verify(nonOpPlayer).sendMessage(MessageConstants.NO_PERMISSION);
        assertFalse(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("on: 未开启时进入测试模式")
    void onEnablesTestMode() {
        new OnCommand().execute(opPlayer, NO_ARGS);

        verify(opPlayer).sendMessage(MessageConstants.MODE_ENABLED);
        assertTrue(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("on: 已开启时提示并保持开启")
    void onWhenAlreadyEnabled() {
        TestModeManager.enable(PLAYER_UUID);

        new OnCommand().execute(opPlayer, NO_ARGS);

        verify(opPlayer).sendMessage(MessageConstants.ALREADY_IN_TEST_MODE);
        assertTrue(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("off: 开启状态下退出测试模式")
    void offDisablesTestMode() {
        TestModeManager.enable(PLAYER_UUID);

        new OffCommand().execute(opPlayer, NO_ARGS);

        verify(opPlayer).sendMessage(MessageConstants.MODE_DISABLED);
        assertFalse(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("off: 未开启时提示并保持关闭")
    void offWhenNotInTestMode() {
        new OffCommand().execute(opPlayer, NO_ARGS);

        verify(opPlayer).sendMessage(MessageConstants.NOT_IN_TEST_MODE);
        assertFalse(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("toggle: 关→开→关，分别提示开启/关闭")
    void toggleTurnsOnAndOff() {
        new ToggleCommand().execute(opPlayer, NO_ARGS);

        verify(opPlayer).sendMessage(MessageConstants.MODE_ENABLED);
        assertTrue(TestModeManager.isInTestMode(PLAYER_UUID));

        new ToggleCommand().execute(opPlayer, NO_ARGS);

        verify(opPlayer).sendMessage(MessageConstants.MODE_DISABLED);
        assertFalse(TestModeManager.isInTestMode(PLAYER_UUID));
    }

    @Test
    @DisplayName("status: 反映当前状态")
    void statusReflectsState() {
        new StatusCommand().execute(opPlayer, NO_ARGS);
        verify(opPlayer).sendMessage(MessageConstants.STATUS_OFF);

        TestModeManager.enable(PLAYER_UUID);
        new StatusCommand().execute(opPlayer, NO_ARGS);
        verify(opPlayer).sendMessage(MessageConstants.STATUS_ON);
    }
}
