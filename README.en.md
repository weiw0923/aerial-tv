# aerial-tv — Tmall MagicBox M31H edition

This is a personal fork of [pengchujin/aerial-tv](https://github.com/pengchujin/aerial-tv)
(Aerial, a tvOS-style launcher for Chinese TVs), customized for the **Tmall MagicBox M31H**
(YunOS Android 10, Allwinner H313).

Everything from upstream is kept; this branch only adds device-specific fixes:

- wake overlay hiding the firmware's hardcoded stock-launcher splash on every SCREEN_ON
- shallow-standby heartbeat + revival alarm (the firmware force-stops the launcher at standby)
- M-key consumption and `yunostv_yingshi://goto_tab` deep-link interception
- deaf-guard self-heal, pre-unlock grab, anti-jolt and anti-hijack behaviors
- configurable update source: the TV updates from **this repo's releases**
- a `TmallM31H` device gate (unit-tested): all of the above turns itself off on any other TV

The device playbook (firmware behavior table, snapshot/apply/restore scripts) lives in
[examples/tmall-magicbox-m31h/README.md](examples/tmall-magicbox-m31h/README.md) (Chinese).

Branches: `magicbox-m31h` = everything; `main` = upstream mirror.
See the [Chinese README](README.md) for full details. License follows upstream.
