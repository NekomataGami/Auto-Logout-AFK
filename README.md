<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/87b100d859992af6a1b5f0e395d948d3949595ea.png" width="640">
</p>

<p align="center">
<b>Auto Logout</b> saves you when having low life. When enabled it automatically disconnects you from the sever or world if your lifes drop below the set threshold.
</p>

## Config
Use **Commands** or the **Mod Menu Integration** to change the settings to fit your wishes.

> [!NOTE]
> ⚠️ From v2.0.0 (Minecraft 1.21.9+) Auto Logout works without any other Mods except FabricAPI and can be controlled using the commands.
> But for using the Settings GUI you need [Mod Menu](https://modrinth.com/project/mOgUt4GM) and [Cloth Config API](https://modrinth.com/project/9s6osm5g), which also enables the Keybinding option. ⚠️

### Commands
```yml
/auto-logout help
/auto-logout enable
/auto-logout disable
/auto-logout threshold
/auto-logout threshold <value>
/auto-logout entity-tracking enable
/auto-logout entity-tracking disable
/auto-logout entity-tracking entity-count
/auto-logout entity-tracking entity-count <value>
/auto-logout entity-tracking radius
/auto-logout entity-tracking radius <value>
/auto-logout join-message enable
/auto-logout join-message disable
```
**Threshold** value has to be between **0** and **20**.<br/>
**Entity Count** value has to be between **1** and **10**.<br/>
**Radius value** has to be between **1** and **64**.

### Mod Menu Integration
> [!IMPORTANT]
> Requires [Mod Menu](https://modrinth.com/project/mOgUt4GM) and [Cloth Config API](https://modrinth.com/project/9s6osm5g)

![https://cdn.modrinth.com/data/vJC9d9qa/images/f0390fdf295f1deb42c0f6847c1d571f5e656f2e.png](https://cdn.modrinth.com/data/vJC9d9qa/images/f0390fdf295f1deb42c0f6847c1d571f5e656f2e.png)

### Defaults
- Enabled: Yes
- Threshold: 4
- Keybinding: Not Bound
- Entity Tracking: Yes
- Entity Count: 5
- Radius: 20
- Show message when joining world: Yes
---
<p align="center"><b>Creator Website: <a href="https://myownbrain.de">🔗myownbrain.de</a></b></p>