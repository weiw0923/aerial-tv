<div align="center">

# aerial-tv · 天猫魔盒 M31H 定制版

**[Aerial](https://github.com/pengchujin/aerial-tv)（tvOS 风格电视桌面）的个人分支**，
为 **天猫魔盒 M31H**（Tmall MagicBox，YunOS Android 10，全志 H313）深度定制。

上游的完整能力全部保留，本分支只多不少 · 上游文档与机型支持见
[上游 README](https://github.com/pengchujin/aerial-tv#readme)

</div>

---

## 这台盒子现在的样子

- **开机 / 唤醒 / 闲置**：全程只有 Aerial，原桌面（优酷酷喵）零露脸
- **M 键**：不再打开「M键生活广场」广告门户
- **待机**：遥控器随时可唤醒；守护进程自愈
- **更新**：控制中心检查**本仓库**的 Releases，签名匹配、装完即编译，与上游发布解耦

## 补丁集（相对上游，按功能分组）

### 唤醒 / 待机链路
- **唤醒遮罩**：每次亮屏（SCREEN_ON）盖出壁纸浮层，藏掉固件硬编码拉起的原桌面 splash 与空白启动窗，Aerial 稳定后揭开
- **浅待机心跳**：息屏后 500ms 心跳防止滑入深睡（这台板子失去原桌面管理后红外会叫不醒），遥控器走系统正常唤醒路径
- **复活闹钟**：固件在待机中强停守护（`tvmgr` 的 "kill 3rd"），60 秒系统闹钟把无障碍拉起来让系统重绑；Android 12+ 精确闹钟合规降级

### 桌面抢位
- **预解锁抢位**：Direct Boot 阶段看见原桌面就提前拉起 Aerial
- **already-resumed 防抖**：Aerial 已在前台时不再重启自己（消除开机时的一次画面抖动）
- **战争击杀**：原桌面页面每秒自顶对抗抢回时，连续 3 次即就地结束其后台进程

### M 键
- 遥控器 M 键（KEYCODE 140/141）在输入管线消费；`yunostv_yingshi://goto_tab?tabId=1039` 深链在框架层拦截（优先级 999），其他深链原样转发

### 修复
- **CecHome 解锁前崩溃**：Direct Boot 阶段读加密偏好直接炸守护——所有绑定早于解锁的机型受益
- **聋守护自愈**：待机复活后无障碍绑定收不到窗口事件，用量统计轮询兜底 + 自动重启无障碍

### 安全阀
- **`TmallM31H` gate**（含单测）：上述全部行为只在真机 getprop 匹配时开启；换一台电视装这个包，闹钟自动清理、深链转发、心跳/遮罩/击杀一律不生效

### 更新系统
- **`update_repo`**：更新检查与下载可指向任意 fork 的 Releases（本仓库即更新源）
- **`update_check`**：与上游发布彻底解耦的开关

## 电视怎么从本仓库更新

控制中心读取 `releases/latest/download/version.txt` 与 `aerial-standard.apk`。发新版：

```bash
# 1. 构建并打 tag（资产名必须一致）
gh release create v1.xx-m31h.N --title "..." --notes "..." \
   version.txt aerial-standard.apk
# 2. 电视首页恢复前台时自动检查，弹出更新 → 确认安装
```

切回官方源：`--es update_repo default` · 彻底关闭检查：`--ez update_check false`

## 设备工具箱

[`examples/tmall-magicbox-m31h/`](examples/tmall-magicbox-m31h/README.md) —— 这台固件的完整驯化手册：

| 文件 | 用途 |
|---|---|
| `snapshot.sh` | 术前全量快照（落在运行者本地，gitignored） |
| `debloat.txt` + `apply.sh` | 已验证的中和清单（杀手的死因都写在注释里） |
| `restore.sh` | 一键回出厂状态 |
| `README.md` | 固件行为表（源码级证据）、四条教训、复现步骤 |

## 分支

- **`magicbox-m31h`**（本页）＝ 上游 + 补丁 + 工具箱
- `main` ＝ 上游镜像，不携带定制

## 授权

跟随上游项目（见 [LICENSE](LICENSE)）。Aerial 由 [pengchujin](https://github.com/pengchujin) 开发，
本分支的所有机型适配工作基于对其源码的逆向分析，感谢上游的持续维护。
