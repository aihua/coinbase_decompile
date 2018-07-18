package io.fabric.sdk.android.services.settings;

public class AppSettingsData {
    public final AppIconSettingsData icon;
    public final String identifier;
    public final String ndkReportsUrl;
    public final String reportsUrl;
    public final String status;
    public final boolean updateRequired;
    public final String url;

    public AppSettingsData(String identifier, String status, String url, String reportsUrl, String ndkReportsUrl, boolean updateRequired, AppIconSettingsData icon) {
        this.identifier = identifier;
        this.status = status;
        this.url = url;
        this.reportsUrl = reportsUrl;
        this.ndkReportsUrl = ndkReportsUrl;
        this.updateRequired = updateRequired;
        this.icon = icon;
    }
}
