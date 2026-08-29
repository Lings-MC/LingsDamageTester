package cn.lingsmc.lingsdamagetester.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestModeManager 状态逻辑单元测试（纯逻辑，无 Bukkit 依赖）。
 *
 * @author 16870
 * @since 2026/8/29
 */
class TestModeManagerTest {
    private UUID uuidA;
    private UUID uuidB;

    @BeforeEach
    void setUp() {
        uuidA = UUID.randomUUID();
        uuidB = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        TestModeManager.clear(uuidA);
        TestModeManager.clear(uuidB);
    }

    @Test
    @DisplayName("默认不在测试模式")
    void defaultNotInTestMode() {
        assertFalse(TestModeManager.isInTestMode(uuidA));
    }

    @Test
    @DisplayName("enable 后处于测试模式，且不影响其他玩家")
    void enablePutsPlayerInTestMode() {
        TestModeManager.enable(uuidA);
        assertTrue(TestModeManager.isInTestMode(uuidA));
        assertFalse(TestModeManager.isInTestMode(uuidB));
    }

    @Test
    @DisplayName("disable 后退出测试模式，重复 disable 安全")
    void disableRemovesTestMode() {
        TestModeManager.enable(uuidA);
        TestModeManager.disable(uuidA);
        assertFalse(TestModeManager.isInTestMode(uuidA));
        TestModeManager.disable(uuidA);
        assertFalse(TestModeManager.isInTestMode(uuidA));
    }

    @Test
    @DisplayName("toggle: 关→开返回 true，开→关返回 false")
    void toggleReturnsNewState() {
        assertTrue(TestModeManager.toggle(uuidA));
        assertTrue(TestModeManager.isInTestMode(uuidA));
        assertFalse(TestModeManager.toggle(uuidA));
        assertFalse(TestModeManager.isInTestMode(uuidA));
    }

    @Test
    @DisplayName("clear 只清除指定玩家，不影响他人")
    void clearOnlyRemovesTarget() {
        TestModeManager.enable(uuidA);
        TestModeManager.enable(uuidB);
        TestModeManager.clear(uuidA);
        assertFalse(TestModeManager.isInTestMode(uuidA));
        assertTrue(TestModeManager.isInTestMode(uuidB));
    }
}
