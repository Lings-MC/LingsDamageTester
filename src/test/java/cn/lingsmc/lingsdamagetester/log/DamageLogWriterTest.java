package cn.lingsmc.lingsdamagetester.log;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DamageLogWriter 伤害日志写入测试（@TempDir 临时目录）。
 *
 * @author 16870
 * @since 2026/8/29
 */
class DamageLogWriterTest {
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("写入一条伤害记录：来源 对 受害者 伤害（原因），文件按日期命名")
    void writeCreatesDatedFileWithLine() throws IOException {
        DamageLogWriter writer = new DamageLogWriter(tempDir);

        writer.write("测试玩家", "僵尸", 7.5D, "近战攻击");

        Path file = tempDir.resolve("damage-" + LocalDate.now() + ".log");
        assertTrue(Files.exists(file));
        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("僵尸 对 测试玩家 造成了 7.5 伤害（近战攻击）"));
        assertTrue(lines.get(0).matches("^\\[\\d{2}:\\d{2}:\\d{2}\\].*"));
    }

    @Test
    @DisplayName("多次写入追加到同一文件")
    void writeAppends() throws IOException {
        DamageLogWriter writer = new DamageLogWriter(tempDir);

        writer.write("测试玩家", "环境", 3.0D, "摔落");
        writer.write("测试玩家", "骷髅", 12.0D, "弹射物");

        Path file = tempDir.resolve("damage-" + LocalDate.now() + ".log");
        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("环境 对 测试玩家 造成了 3.0 伤害（摔落）"));
        assertTrue(lines.get(1).contains("骷髅 对 测试玩家 造成了 12.0 伤害（弹射物）"));
    }
}
