# DamageTester 测试模式 设计方案

> 日期：2026-08-29 ｜ 对应需求：docs/requirements.md ｜ 状态：已按推荐方案实施（自主模式，决策依据见各节"权衡"）

## 1. 技术栈与工程决策

| 决策点 | 选择 | 权衡说明 |
|--------|------|----------|
| 目标平台 | spigot-api **26.2-R0.1-SNAPSHOT**（provided） | 用户指定 MC 26.2；Spigot 官方仓库已发布该 API。Paper API 26.2 移除了弃用 API 且 spigot() 动作条 API 在两者均可用，选 Spigot 保兼容。 |
| Java | **release 25**（编译用 Zulu 25.0.4.1） | MC 26.1+ 要求 Java 25 运行时；spigot-api 26.2 类文件版本高于 21，`--release 8/21` 无法读取其 class 文件，必须 25。模板的 Java 8 基线随之升级。 |
| Lombok | 1.18.46 | 模板 1.18.24 不支持 JDK 25，升级至 Central 最新（1.18.46）。 |
| 测试方案 | JUnit 5.11 + Mockito 5.23 直测 | MockBukkit 尚不兼容 26.2 API；事件处理器/命令属框架胶水，按 testing-reference 的"逻辑抽离模式"：纯逻辑（模式管理、文案格式化）严格 TDD，胶水用 Mockito 验证交互契约（取消事件/调度回血/动作条输出）。 |
| 测试模式存储 | 静态 `ConcurrentHashMap.newKeySet()<UUID>`，内存态 | 仅主线程访问+防御性并发容器；单服务器单实例；不做持久化（需求非目标）。 |

## 2. 命令树

根命令 `damagetester`（别名 `dt`），参数统一 `toLowerCase` 分发（模板 StringUtils 约定）：

```
/damagetester            → ROOT_MESSAGE 运行横幅（无权限要求）
/damagetester help       → 帮助（无权限要求）
/damagetester on         → 进入测试模式        [Op]
/damagetester off        → 退出测试模式        [Op]
/damagetester toggle     → 切换测试模式        [Op]
/damagetester status     → 查询测试模式状态    [Op]
/damagetester reload     → 重载配置            [Op]
其他                     → §c未知命令.
```

权限模型：沿用模板 `PermissionUtils.nonAdminAuth`（isOp 判断），不新增 permission 节点。理由：测试模式等同无敌，按服主工具定位默认仅 Op；模板约定即 isOp，保持一致。

## 3. 事件清单

| 事件 | 优先级 | 行为 |
|------|--------|------|
| `EntityDamageEvent` | `HIGHEST` + `ignoreCancelled = true` | ① 非玩家或不在测试模式 → 直接返回；② 读取 `getFinalDamage()`；③ 若 `当前血量 - 最终伤害 ≤ 0`（致死）→ `setCancelled(true)` 防死；④ 动作条显示伤害数值；⑤ 调度下一 tick 恢复满血。 |
| `PlayerQuitEvent` | `NORMAL` | 从测试模式集合移除该玩家 UUID。 |

**关键时序说明**：在 HIGHEST 优先级处理器内读到的 `getHealth()` 是伤害结算前的血量（原版在事件后结算伤害），因此：
- 非致死伤害：不取消事件 → 原版正常结算（有受击红闪/击退，符合"受到伤害后自动回到满血"的体验）→ 下一 tick `setHealth(max)`。
- 致死伤害：必须取消事件（若不取消，下一 tick 恢复前玩家已死亡）。
- 回血目标值取 `getAttribute(Attribute.MAX_HEALTH).getValue()`（属性可能被其它插件修改）；属性缺失时记日志跳过该次回血，不写死 20.0。
- 回血任务内检查 `isOnline()`，避免退出竞态。

## 4. 类结构（包 cn.lingsmc.damagetester）

```
DamageTester.java                 主类：onLoad 初始化配置；onEnable 注册命令与两个监听器
commands/
  Commands.java                   CommandExecutor + TabCompleter，注册子命令（防重复）
  SubCommand.java                 子命令接口（模板原样保留）
  subcommands/
    HelpCommand / ReloadCommand   模板保留，文案更新
    OnCommand / OffCommand / ToggleCommand / StatusCommand   新增
constants/
  CommandConstants.java           ALIAS="dt"、子命令名常量、COMMAND_MAP
  MessageConstants.java           全部玩家可见文案（§ 色板，String.format 占位）
  DamageCauseConstants.java       DamageCause → 中文名映射（含常用 20 项，回退枚举名）
  ConfigConstants.java            配置键 show-cause 及默认值
listener/
  DamageListener.java             EntityDamageEvent 处理（核心逻辑）
  QuitListener.java               PlayerQuitEvent 清理
manager/
  TestModeManager.java            测试模式集合：enable/disable/toggle/isInTestMode/clear
utils/
  ConfigUtils.java                配置初始化 + showCause() 读取（模板扩展）
  DamageMessageFormatter.java     伤害文案格式化（纯逻辑，可 TDD）
  PermissionUtils / StringUtils   模板原样保留
```

## 5. 配置项清单（config.yml）

| 键 | 默认值 | 说明 |
|----|--------|------|
| `show-cause` | `true` | 伤害提示是否附带来源类型（摔落/近战…）。reload 后生效。 |

## 6. 消息文案（MessageConstants，色板 §3/§b/§a/§c/§e/§7）

| 常量 | 文案 |
|------|------|
| ROOT_MESSAGE | `§3此服务器正在运行 §bDamageTester <版本>§3 by §b16870` / `§3命令列表: §b/dt help` |
| HELP_MESSAGE | `§3§l----- DamageTester指令 -----` + 5 条 `§b/dt <子命令> §3- §a<说明>` |
| MODE_ENABLED | `§a测试模式已开启，受到伤害后将自动回满血.` |
| MODE_DISABLED | `§a测试模式已关闭.` |
| ALREADY_IN_TEST_MODE | `§e你已经在测试模式中.` |
| NOT_IN_TEST_MODE | `§e你当前不在测试模式中.` |
| STATUS_ON / STATUS_OFF | `§3测试模式: §b开启中` / `§3测试模式: §7关闭` |
| RELOAD_SUCCESS / UNKNOWN_COMMAND / NO_PERMISSION / CONSOLE | 模板原文（§a重载成功. 等） |
| 动作条伤害 | `<色值><数值> §7伤害 §7(<来源>)`，来源可关 |

## 7. 测试计划（TDD 任务拆分）

| # | 任务 | 测试先行内容 | 验证 |
|---|------|--------------|------|
| 1 | `TestModeManager` | enable/disable/toggle/isInTestMode/clear 的状态断言（纯 UUID 逻辑） | `mvn test` 红→绿 |
| 2 | `DamageMessageFormatter` + `DamageCauseConstants` | 数值格式化(1 位小数)、三档着色阈值边界(1.9/2.0/9.9/10.0)、show-cause 开关、未知原因回退 | 红→绿 |
| 3 | `DamageListener` | Mockito：致死→cancel+调度回血；非致死→不取消+调度回血；非测试模式→零交互；动作条输出 | 红→绿（mockStatic(Bukkit) 验证调度） |
| 4 | `QuitListener` | quit 后集合移除 | 红→绿 |
| 5 | 命令子类 | On/Off/Toggle/Status 的消息与状态变化、权限拒绝 | 红→绿 |
| 6 | 构建 | `mvn clean package`；解压 jar 验证 plugin.yml version 展开 | 新鲜日志 |

## 8. 风险与对策

| 风险 | 对策 |
|------|------|
| spigot-api 26.2 中 `getDescription()`/动作条 API 可能被移除 | 构建即验证；若移除，运行横幅版本改由 Maven filter 资源注入、动作条降级聊天栏并在交付说明标注。 |
| `Attribute.MAX_HEALTH` 枚举名（1.21.3+ 去掉 GENERIC_ 前缀） | 按 26.2 API 书写，编译期即验证。 |
| 虚空伤害被取消后玩家滞留虚空但不死 | 需求内行为（防死优先）；README 注意事项说明，可用 `/dt off` + 传送解除。 |
| 同 tick 多次伤害产生多个回血任务 | `setHealth` 幂等，无害。 |
| api-version '26.2' 校验 | 与服务器版本一致，启动期验证；若校验失败按服务器实际要求调整。 |
