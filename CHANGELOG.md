# Changelog

## 1.1.0 — Cross-version compatibility (1.21.11 → 26.1.2)

### Added
- **Single-jar support for Minecraft 1.21.11 and 26.1.2** ("Tiny Takeover", new year-based
  version scheme, Java 25). The same artifact runs across the whole range.
- **Fabric support via Cardboard** for 1.21.11, with setup instructions in `README.md`.
- `util/PluginCompat` shim: metadata access now falls back from Paper's `getPluginMeta()` to
  the legacy `getDescription()` API, so the plugin boots cleanly under Cardboard / non-Paper
  Bukkit implementations.
- `README.md` and `CHANGELOG.md`.

### Changed
- Build now compiles against `paper-api:1.21.11-R0.1-SNAPSHOT` (the lowest supported API) and
  emits Java 21 bytecode, which loads on both the Java 21 (1.21.11) and Java 25 (26.1.x)
  runtimes.
- `plugin.yml` keeps `api-version: '1.21'` — the highest value accepted by both 1.21.11 and
  26.1.2 — with an explanatory comment.
- Refreshed project version (1.0.0 → 1.1.0) and descriptions.

### Compatibility notes
- The enchantment system is PDC + lore based with **no NMS / no packets**, and already uses
  modern (1.20.5+) `Material` / `Sound` / `Particle` / `PotionEffectType` / `Enchantment`
  constant names, so no version-specific branches are needed.
- Deprecated String-based `ItemMeta` text methods (`setLore` / `setDisplayName`) are still
  present on 26.1.2 (deprecated-for-removal, not removed). A future migration to the Adventure
  `Component` API is recommended but not required for current support.
