<p align="center">
A fork of Auto-Logout that automatically disconnects you from the server or world if you are AFK and your HP drops below the threshold after taking damage.
</p>

## Config
Use **Commands** or the **Mod Menu Integration** to change the settings to fit your wishes.

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
- Threshold: 10
- Keybinding: Not Bound
- Entity Tracking: Yes
- Entity Count: 1
- Radius: 20
- Show message when joining world: No
