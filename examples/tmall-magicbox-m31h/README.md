# 天猫魔盒 M31H（Tmall MagicBox，阿里云 YunOS，Android 10）

阿里巴巴天猫魔盒 M31H（全志 H313）的完整驯化记录：固件行为、每个坑的成因、可复现的处置步骤。
这是天猫魔盒，不是中国移动魔百和。Android 10。专用逻辑只在 `getprop` 读出下面这组字符串时打开。

## 怎么确认是这台（getprop）

连上盒子后：

```bash
adb shell getprop ro.product.model
adb shell getprop ro.product.device
adb shell getprop ro.product.name
adb shell getprop ro.product.brand
adb shell getprop ro.product.manufacturer
adb shell getprop ro.build.version.release
```

这台实测是：

| 属性 | 值 |
|---|---|
| `ro.product.model` | `MagicBox_M_31_H` |
| `ro.product.device` | `MagicBox_M_31_H` |
| `ro.product.name` | `MagicBox_M_31_H` |
| `ro.product.brand` | `MagicBox_M_31_H` |
| `ro.product.manufacturer` | `Tmall` |
| `ro.build.version.release` | `10` |

不是 `MagicBox_M31H`（31 两边没有下划线）。`TmallM31H.isTmallM31H()` 要求 model 或 device 正好是 `MagicBox_M_31_H`，并且厂商是 `Tmall` 或品牌是 `MagicBox_M_31_H`。厂商 `Tmall` 不会被当成排除条件。

工具通用于本目录的 `snapshot.sh` / `apply.sh` / `restore.sh`（任何 adb 可达的电视都能跑）。
`snapshot.sh` 和 `apply.sh` 的备份目录都来自 `backup-dir.sh`。

## 固件行为（全部逆向实证）

| 行为 | 位置 | 后果 |
|---|---|---|
| 每次 SCREEN_ON 显式启动优酷桌面 | `TvWindowManager`（system_server 内，硬编码组件名） | 优酷缺失时 ActivityNotFoundException **炸掉整个 system_server**，只能断电 |
| 待机时定点强停第三方桌面 | `com.yunos.tvmgr`（日志原话 `kill 3rd processName`） | 第三方桌面的守护随进程死亡 |
| `com.ott.minimonitor` | 平台签名 + FORCE_STOP_PACKAGES + 使用监控 | 协同杀手 |
| 包解析时强制优酷进 system 组 | `PackageParser` 硬编码 `mSharedUserId = android.uid.system` | 优酷=系统本体，签名墙/权限墙的根源 |
| 守护复活后永久「失聪」 | 无障碍绑定存活但收不到窗口事件 | 需用量统计轮询兜底 |
| 入睡时清空全部任务栈 | `PowerManagerService.clearTaskCache()` | 待机=清后台 |
| `pm disable/hide` 被拒 | 保护名单（`Shell cannot change component state`） | 常规停用路全断 |

## 关键教训

1. **优酷不能卸**——它是唤醒链的接球手，卸了=每次亮屏系统崩溃（假象是「成功唤醒」实为崩溃软重启）。
2. **替身占名不可行**——`com.youku.taitan.tv` 被系统分区真身占用（隐藏时记录仍在），平台签名墙挡死冒名安装。
3. **先除杀手，再谈防御**——tvmgr/minimonitor 在岗时，一切守护层防御都是概率性的；除掉后全部变成必然。
4. **真挂起下红外唤醒是通的**（设备树 `s_cir@07040000` 带 wakeup-source + 9 组硬件键码槽位）——「叫不醒」从来是框架崩溃，不是硬件。

## 最终架构

- **优酷**：留在系统里当唤醒接球手，appops 掐掉其开机广播，永远隐形
- **tvmgr / minimonitor**：用户级卸载（数据保留，`install-existing` 可逆）
- **Aerial**：默认桌面 + 守护栈（黑幕遮蔽 / 预解锁抢位 / 失聪自愈 / 复活闹钟 / M 键深链拦截 / already-resumed 防抖）
- **更新**：`--es update_repo` 指向自己的 fork，与官方发布解耦

## 复现步骤

```bash
# 示例在 magicbox-m31h 分支上，默认克隆 main 拿不到这个目录。
git clone -b magicbox-m31h https://github.com/weiw0923/aerial-tv && cd aerial-tv/examples/tmall-magicbox-m31h
TV=<盒子IP>:5555 ./snapshot.sh          # 1. 快照（与 apply.sh 共用 backup-dir.sh；默认仓库 backups/，可用 BACKUP_DIR 重定向；勿提交）
TV=<盒子IP>:5555 ./apply.sh             # 2. 除杀手 + 掐优酷开机广播
# 3. 安装 Aerial（见仓库 README 的安装命令；本分支的构建含天猫魔盒 M31H 的守护补丁）
TV=<盒子IP>:5555 ./restore.sh           # 需要回出厂状态时
```

## 恢复边界

- `restore.sh` 恢复杀手与原桌面，但**不会**移除 Aerial 或其补丁——桌面去留独立决定
- 系统大版本更新可能重装被卸的包：重跑 `apply.sh` 即可
