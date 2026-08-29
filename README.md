# LingsDamageTester

一个 Minecraft 26.2 伤害测试插件：开启测试模式后自身打不死、受伤自动回满血，并在动作条实时显示每次受到的伤害数值。

## 技术栈

| 项目 | 值 |
|------|-----|
| 类型 | 插件（Bukkit/Spigot） |
| 平台 | Spigot/Paper 26.2（spigot-api 26.2-R0.1-SNAPSHOT） |
| Java | 25 |
| 构建 | Maven（maven-compiler-plugin release 25 + maven-shade） |
| 测试 | JUnit 5 + Mockito（29 例单元测试） |

## 目录结构

```
src/main/java/cn/lingsmc/lingsdamagetester/
├── LingsDamageTester.java   # 入口：生命周期/命令/监听器挂载
├── commands/                # 命令系统（SubCommand 模式 + Tab 补全）
├── constants/               # 命令/消息/伤害原因/配置键常量（玩家可见文案统一在此）
├── listener/                # 伤害监听（显示+防死+回血）、退出清理
├── manager/                 # 测试模式状态集合（TestModeManager）
└── utils/                   # 配置读取、伤害文案格式化、权限、字符串工具
src/main/resources/          # plugin.yml + config.yml
```

## 使用

1. 构建（`mvn clean package`）或获取 `target/LingsDamageTester-1.1.jar`，放入服务器 `plugins/` 目录后重启服务器（Minecraft 26.2 服务端需 Java 25）。
2. 游戏内以 Op 执行 `/dt on` 进入测试模式；测试完毕 `/dt off` 退出。
3. 测试模式中：自身受到的任何伤害都会被取消（不扣血、不会死亡），每次受击统一补播原版受击音效，动作条显示本次伤害数值与来源，并写入伤害测试日志。

## 命令

根命令 `/lingsdamagetester`，别名 `/dt`；`on`/`off`/`toggle`/`status`/`reload` 需要 **Op** 权限。

- `/dt on` — 进入测试模式
- `/dt off` — 退出测试模式
- `/dt toggle` — 切换测试模式
- `/dt status` — 查看当前状态
- `/dt help` — 查看帮助
- `/dt reload` — 重载配置（需 Op）

## 配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| show-cause | true | 动作条伤害提示是否显示来源类型（摔落/近战等），`/dt reload` 后生效 |
| damage-log | true | 是否写入伤害测试日志，`/dt reload` 后生效 |

## 伤害日志

- 位置：`plugins/LingsDamageTester/logs/damage-yyyy-MM-dd.log`，按日期分文件。
- 每行格式：`[HH:mm:ss] 来源 对 受害玩家 造成了 N.N 伤害（原因）`；弹射物溯源到发射者，无来源实体时记为「环境」。

## 常见问题

### 版本行为差异
- v1.1：伤害事件在读取数值后统一取消（不真实扣血、无心形血量闪动），受击音效由插件统一补播（修复旧版致死伤害无音效的割裂感）。
- v1.2：伤害监听优先级调整为 MONITOR，晚于其它插件的伤害处理。
- v1.3：移除受击后回满血——伤害已取消、血量不会下降；进入测试模式时已有的血量缺失保持原状。

### 测试模式中掉进虚空不会死
测试模式会取消伤害事件（含虚空伤害），玩家会滞留虚空但不会死亡；`/dt off` 后传送回地面即可。

### 伤害数值显示在哪里
动作条（物品栏上方），不刷聊天栏；同一时间只显示最近一次伤害。数值按大小着色：小于 2.0 绿色、小于 10.0 黄色、10.0 及以上红色。

### 为什么需要 Java 25
Minecraft 26.1 及以上的服务端要求 Java 25 运行时，本插件编译目标同为 25。

## License

MIT License（见 LICENSE 文件）。
