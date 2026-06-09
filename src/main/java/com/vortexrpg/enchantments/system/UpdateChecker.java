package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.util.PluginCompat;
import com.vortexrpg.enchantments.util.SchedulerUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Checks for plugin updates on startup via SpigotMC API or GitHub releases.
 */
public class UpdateChecker {

    private final VortexEnchantments plugin;
    private final String currentVersion;
    private String latestVersion;
    private boolean updateAvailable;

    public UpdateChecker(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.currentVersion = PluginCompat.version(plugin);
    }

    /** Run the update check asynchronously. */
    public void checkAsync() {
        if (!plugin.getConfig().getBoolean("update-checker.enabled", true)) return;

        SchedulerUtil.runAsync(plugin, () -> {
            try {
                // Use SpigotMC resource API (replace RESOURCE_ID with actual ID when published)
                int resourceId = plugin.getConfig().getInt("update-checker.resource-id", 0);
                if (resourceId <= 0) {
                    // No resource ID configured — skip check
                    return;
                }

                HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    latestVersion = response.body().trim();
                    if (!currentVersion.equals(latestVersion)) {
                        updateAvailable = true;
                        plugin.getLogger().info("§e[Update] A new version is available: v" + latestVersion
                            + " (you are running v" + currentVersion + ")");
                    }
                }
            } catch (IOException | InterruptedException e) {
                // Silently fail — update check is non-critical
            }
        });
    }

    public boolean isUpdateAvailable() { return updateAvailable; }
    public String getLatestVersion() { return latestVersion; }
    public String getCurrentVersion() { return currentVersion; }
}
