# Discord Bridge Plugin for Minecraft
A Spigot plugin that relays the chat between Minecraft and Discord.

## Building
To build the plugin JAR, run `./gradlew jar`.

## Usage
To use the plugin, place the JAR in your Spigot server's `plugins` folder. Additionally, create a subdirectory named `DiscordBridge` with a file `config.yaml`:

```yaml
botToken: "YOUR_BOT_TOKEN"
```
