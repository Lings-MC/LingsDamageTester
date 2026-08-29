package cn.lingsmc.lingsdamagetester.manager;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试模式状态管理：记录处于测试模式的玩家。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class TestModeManager {
    /** 处于测试模式的玩家集合；事件与命令均在主线程访问，用并发集合做防御性约定 */
    private static final Set<UUID> TEST_MODE_PLAYERS = ConcurrentHashMap.newKeySet();

    private TestModeManager() {
    }

    /**
     * @return 该玩家是否处于测试模式
     */
    public static boolean isInTestMode(UUID uuid) {
        return TEST_MODE_PLAYERS.contains(uuid);
    }

    public static void enable(UUID uuid) {
        TEST_MODE_PLAYERS.add(uuid);
    }

    public static void disable(UUID uuid) {
        TEST_MODE_PLAYERS.remove(uuid);
    }

    /**
     * 切换测试模式。
     *
     * @return 切换后是否处于测试模式
     */
    public static boolean toggle(UUID uuid) {
        if (TEST_MODE_PLAYERS.remove(uuid)) {
            return false;
        }
        TEST_MODE_PLAYERS.add(uuid);
        return true;
    }

    /**
     * 玩家退出服务器时清理状态，避免集合残留。
     */
    public static void clear(UUID uuid) {
        TEST_MODE_PLAYERS.remove(uuid);
    }
}
