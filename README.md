# Discord Bridge Plugin for Minecraft

[![Build](https://github.com/fwcd/mc-discord-bridge/actions/workflows/build.yml/badge.svg)](https://github.com/fwcd/mc-discord-bridge/actions/workflows/build.yml)

A Spigot plugin that enables two-way messaging between Minecraft and Discord.

## Building

To build the plugin JAR, make sure to have Java 17 installed and run `./gradlew shadowJar`. The plugin JAR should then be located in `build/libs`.

> [!IMPORTANT]
> While the source code is [MIT-licensed](./LICENSE), the JAR is [LGPL-licensed](./src/main/resources/LICENSE) since it repackages LGPL libraries.

## Usage

To use the plugin, place the JAR in your Spigot/Paper 1.20.2 server's `plugins` folder. Additionally, create a subdirectory named `DiscordBridge` with a file `config.yml`:

```yaml
bot:
  token: YOUR_BOT_TOKEN
  commandPrefix: +
webhook:
  url: YOUR_WEBHOOK_URL
  enabled: true
```

Optionally, you can configure exactly which events should be forwarded to Discord:

```yaml
forward:
  chat: true
  joinLeave: true
  death: true
  advancement: false
  webChat: true # only if Dynmap is present
```

Additionally, you can configure whether the Discord integration should display the current player count through a custom presence:

```yaml
bot:
  presence:
    enabled: true
```
