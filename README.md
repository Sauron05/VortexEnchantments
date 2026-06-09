# VortexEnchantments Source

This repository contains the reviewable source code for the Paper/Folia version of VortexEnchantments.

The purpose of this repository is transparency: server owners and developers can inspect the plugin source and verify that the public jar is built from ordinary Java source, YAML resources, and Gradle build files.

## What Is Included

| Path | Purpose |
| --- | --- |
| `src/main/java/` | Paper/Folia plugin source code. |
| `src/main/resources/` | `plugin.yml`, configs, messages, mob drops, custom items, and enchantment YAML files. |
| `build.gradle.kts` | Gradle build script for the Paper/Folia jar. |
| `settings.gradle.kts` | Gradle project settings. |
| `gradle/`, `gradlew`, `gradlew.bat` | Gradle wrapper for reproducible builds. |
| `LICENSE` | Proprietary source license. |

## What Is Not Included

This repository intentionally does not include:

- Extra platform source.
- Marketing images.
- Changelog files.
- Build output jars.
- `.gradle/` caches.
- IDE folders.
- Server runtime folders.
- Obfuscation tasks.

## Build The Plugin

Windows:

```powershell
.\gradlew.bat clean build
```

Linux/macOS:

```bash
./gradlew clean build
```

The built jar is generated in:

```text
build/libs/
```

## Supported Runtime

- Paper
- Folia
- Java 21+
- Minecraft 1.21.x compatible server APIs

Optional soft dependencies:

- Vault
- PlaceholderAPI
- ProtocolLib

## Safety Review Notes

The repository is structured so reviewers can inspect the actual plugin behavior:

- Commands are implemented under `src/main/java/com/vortexrpg/enchantments/command/`.
- Event listeners and enchant behavior are under `src/main/java/com/vortexrpg/enchantments/enchant/`.
- Player data handling is under `src/main/java/com/vortexrpg/enchantments/data/`.
- Configuration loading is under `src/main/java/com/vortexrpg/enchantments/config/`.
- Server-facing metadata is in `src/main/resources/plugin.yml`.
- Player-facing messages and gameplay tuning are in YAML resources.

There are no tracked compiled plugin jars in this repository. Build artifacts are ignored by `.gitignore`.

## Support

Developer: Sauron

Discord: https://discord.gg/Tya84XrgSF

Website: https://eternalrealm.uk

## License

All rights reserved to the EternalRealm Team and Sauron.
