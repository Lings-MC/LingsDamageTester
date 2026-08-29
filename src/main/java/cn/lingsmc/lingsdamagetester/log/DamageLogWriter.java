package cn.lingsmc.lingsdamagetester.log;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 伤害测试日志写入器：按日期滚动写入 logs/damage-yyyy-MM-dd.log。
 * 每次写入独立追加并立即落盘，格式：[HH:mm:ss] 来源 对 受害者 造成了 N.N 伤害（原因）。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class DamageLogWriter {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String LINE_FORMAT = "[%s] %s 对 %s 造成了 %.1f 伤害（%s）";

    private final Path logsDir;

    public DamageLogWriter(Path logsDir) {
        this.logsDir = logsDir;
    }

    /**
     * 追加一条伤害记录；IO 失败仅记服务端日志，不影响测试流程。
     */
    public void write(String victim, String damager, double damage, String cause) {
        String line = String.format(LINE_FORMAT, LocalTime.now().format(TIME_FORMAT), damager, victim, damage, cause);
        Path file = logsDir.resolve("damage-" + LocalDate.now() + ".log");
        try {
            Files.createDirectories(logsDir);
            Files.write(file, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            Bukkit.getLogger().warning("[LingsDamageTester] 写入伤害日志失败: " + e.getMessage());
        }
    }
}
