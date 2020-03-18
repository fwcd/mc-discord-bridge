# Discord Bridge Plugin for Minecraft
A Spigot plugin that enables two-way messaging between Minecraft and Discord.

## Building
To build the plugin JAR, run `./gradlew jar`.

## Usage
To use the plugin, place the JAR in your Spigot server's `plugins` folder. Additionally, create a subdirectory named `DiscordBridge` with a file `config.yaml`:

```yaml
bot:
  token: YOUR_BOT_TOKEN
  commandPrefix: +
webhook:
  url: YOUR_WEBHOOK_URL
  enabled: true
```
