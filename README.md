# RaikouHUD

RaikouHUD is a modern, configurable HUD plugin for **Paper/Spigot 1.21+**.

It provides:
- Scoreboard
- BossBar
- ActionBar
- TAB header/footer formatting

## Requirements
- Java 21
- Paper or Spigot 1.21+

## Installation
1. Build the plugin jar with Gradle.
2. Put the jar into your server `plugins/` folder.
3. Start the server once to generate default config files.
4. Edit configs in `plugins/RaikouHUD/`.
5. Use `/raikouhud reload` after changes.

## Commands
- `/raikouhud help`
- `/raikouhud reload`
- `/raikouhud status [player]`
- `/raikouhud toggle <scoreboard|bossbar|actionbar|tab> [player]`
- `/raikouhud version`

## Permissions
- `raikouhud.command.use` (default: true)
- `raikouhud.command.reload` (default: op)
- `raikouhud.command.status` (default: true)
- `raikouhud.command.status.others` (default: op)
- `raikouhud.command.toggle` (default: true)
- `raikouhud.command.toggle.others` (default: op)
- `raikouhud.admin` (default: op)

## Configuration Files
- `config.yml`: global plugin settings, locale, module toggles, performance, integrations.
- `scoreboard.yml`: scoreboard module settings and lines.
- `bossbar.yml`: bossbar module style/title/progress settings.
- `actionbar.yml`: actionbar message and update settings.
- `tab.yml`: tab header/footer and update settings.
- `lang/en_US.yml`, `lang/tr_TR.yml`: localized command and plugin messages.

## Formatting
RaikouHUD uses **MiniMessage** formatting in all message and HUD config values.

Examples:
- `<aqua><bold>RaikouHUD</bold></aqua>`
- `<gray>Online: <white>%server_online%</white></gray>`
- `<gradient:#58D6FF:#2F80ED>Title</gradient>`

## Placeholder Support
Built-in placeholders include:
- `%player_name%`
- `%player_display_name%`
- `%player_ping%`
- `%player_world%`
- `%player_x%`, `%player_y%`, `%player_z%`
- `%player_health%`
- `%player_health_ratio%`
- `%server_online%`
- `%server_max_players%`
- `%server_tps_1m%`
- `%time_hhmmss%`

If PlaceholderAPI is installed and enabled in `config.yml`, unresolved placeholders are also resolved through PlaceholderAPI.

## Localization
RaikouHUD ships with:
- English (`en_US`)
- Turkish (`tr_TR`)

Set locale in `config.yml`:
```yaml
locale:
  default: en_US
  fallback: en_US
```

## Build
```powershell
gradle clean build
```

If your environment blocks global Gradle home access, use:
```powershell
$env:GRADLE_USER_HOME=(Resolve-Path '.').Path + '\.gradle-user-home'
gradle clean build
```
