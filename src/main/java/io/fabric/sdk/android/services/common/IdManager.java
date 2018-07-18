package io.fabric.sdk.android.services.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class IdManager {
    private static final String FORWARD_SLASH_REGEX = Pattern.quote("/");
    private static final Pattern ID_PATTERN = Pattern.compile("[^\\p{Alnum}]");
    AdvertisingInfo advertisingInfo;
    AdvertisingInfoProvider advertisingInfoProvider;
    private final Context appContext;
    private final String appIdentifier;
    private final String appInstallIdentifier;
    private final boolean collectHardwareIds;
    private final boolean collectUserIds;
    boolean fetchedAdvertisingInfo;
    FirebaseInfo firebaseInfo;
    private final ReentrantLock installationIdLock = new ReentrantLock();
    private final InstallerPackageNameProvider installerPackageNameProvider;
    private final Collection<Kit> kits;

    public enum DeviceIdentifierType {
        WIFI_MAC_ADDRESS(1),
        BLUETOOTH_MAC_ADDRESS(2),
        FONT_TOKEN(53),
        ANDROID_ID(100),
        ANDROID_DEVICE_ID(101),
        ANDROID_SERIAL(102),
        ANDROID_ADVERTISING_ID(103);
        
        public final int protobufIndex;

        private DeviceIdentifierType(int pbufIndex) {
            this.protobufIndex = pbufIndex;
        }
    }

    public IdManager(Context appContext, String appIdentifier, String appInstallIdentifier, Collection<Kit> kits) {
        if (appContext == null) {
            throw new IllegalArgumentException("appContext must not be null");
        } else if (appIdentifier == null) {
            throw new IllegalArgumentException("appIdentifier must not be null");
        } else if (kits == null) {
            throw new IllegalArgumentException("kits must not be null");
        } else {
            this.appContext = appContext;
            this.appIdentifier = appIdentifier;
            this.appInstallIdentifier = appInstallIdentifier;
            this.kits = kits;
            this.installerPackageNameProvider = new InstallerPackageNameProvider();
            this.advertisingInfoProvider = new AdvertisingInfoProvider(appContext);
            this.firebaseInfo = new FirebaseInfo();
            this.collectHardwareIds = CommonUtils.getBooleanResourceValue(appContext, "com.crashlytics.CollectDeviceIdentifiers", true);
            if (!this.collectHardwareIds) {
                Fabric.getLogger().d("Fabric", "Device ID collection disabled for " + appContext.getPackageName());
            }
            this.collectUserIds = CommonUtils.getBooleanResourceValue(appContext, "com.crashlytics.CollectUserIdentifiers", true);
            if (!this.collectUserIds) {
                Fabric.getLogger().d("Fabric", "User information collection disabled for " + appContext.getPackageName());
            }
        }
    }

    public boolean canCollectUserIds() {
        return this.collectUserIds;
    }

    private String formatId(String id) {
        return id == null ? null : ID_PATTERN.matcher(id).replaceAll("").toLowerCase(Locale.US);
    }

    public String getAppInstallIdentifier() {
        String appInstallId = this.appInstallIdentifier;
        if (appInstallId != null) {
            return appInstallId;
        }
        SharedPreferences prefs = CommonUtils.getSharedPrefs(this.appContext);
        checkAdvertisingIdRotation(prefs);
        appInstallId = prefs.getString("crashlytics.installation.id", null);
        if (appInstallId == null) {
            return createInstallationUUID(prefs);
        }
        return appInstallId;
    }

    public String getAppIdentifier() {
        return this.appIdentifier;
    }

    public String getOsVersionString() {
        return getOsDisplayVersionString() + "/" + getOsBuildVersionString();
    }

    public String getOsDisplayVersionString() {
        return removeForwardSlashesIn(VERSION.RELEASE);
    }

    public String getOsBuildVersionString() {
        return removeForwardSlashesIn(VERSION.INCREMENTAL);
    }

    public String getModelName() {
        return String.format(Locale.US, "%s/%s", new Object[]{removeForwardSlashesIn(Build.MANUFACTURER), removeForwardSlashesIn(Build.MODEL)});
    }

    private String removeForwardSlashesIn(String s) {
        return s.replaceAll(FORWARD_SLASH_REGEX, "");
    }

    @SuppressLint({"CommitPrefEdits"})
    private String createInstallationUUID(SharedPreferences prefs) {
        this.installationIdLock.lock();
        try {
            String uuid = prefs.getString("crashlytics.installation.id", null);
            if (uuid == null) {
                uuid = formatId(UUID.randomUUID().toString());
                prefs.edit().putString("crashlytics.installation.id", uuid).commit();
            }
            this.installationIdLock.unlock();
            return uuid;
        } catch (Throwable th) {
            this.installationIdLock.unlock();
        }
    }

    private void checkAdvertisingIdRotation(SharedPreferences prefs) {
        AdvertisingInfo newAdInfo = getAdvertisingInfo();
        if (newAdInfo != null) {
            flushInstallationIdIfNecessary(prefs, newAdInfo.advertisingId);
        }
    }

    @SuppressLint({"CommitPrefEdits"})
    private void flushInstallationIdIfNecessary(SharedPreferences prefs, String advertisingId) {
        this.installationIdLock.lock();
        try {
            if (!TextUtils.isEmpty(advertisingId)) {
                String oldId = prefs.getString("crashlytics.advertising.id", null);
                if (TextUtils.isEmpty(oldId)) {
                    prefs.edit().putString("crashlytics.advertising.id", advertisingId).commit();
                } else if (!oldId.equals(advertisingId)) {
                    prefs.edit().remove("crashlytics.installation.id").putString("crashlytics.advertising.id", advertisingId).commit();
                }
                this.installationIdLock.unlock();
            }
        } finally {
            this.installationIdLock.unlock();
        }
    }

    public Map<DeviceIdentifierType, String> getDeviceIdentifiers() {
        Map<DeviceIdentifierType, String> ids = new HashMap();
        for (Kit kit : this.kits) {
            if (kit instanceof DeviceIdentifierProvider) {
                for (Entry<DeviceIdentifierType, String> entry : ((DeviceIdentifierProvider) kit).getDeviceIdentifiers().entrySet()) {
                    putNonNullIdInto(ids, (DeviceIdentifierType) entry.getKey(), (String) entry.getValue());
                }
            }
        }
        String advertisingId = getAdvertisingId();
        if (TextUtils.isEmpty(advertisingId)) {
            putNonNullIdInto(ids, DeviceIdentifierType.ANDROID_ID, getAndroidId());
        } else {
            putNonNullIdInto(ids, DeviceIdentifierType.ANDROID_ADVERTISING_ID, advertisingId);
        }
        return Collections.unmodifiableMap(ids);
    }

    public String getInstallerPackageName() {
        return this.installerPackageNameProvider.getInstallerPackageName(this.appContext);
    }

    public Boolean isLimitAdTrackingEnabled() {
        if (shouldCollectHardwareIds()) {
            return explicitCheckLimitAdTracking();
        }
        return null;
    }

    public String getAdvertisingId() {
        if (!shouldCollectHardwareIds()) {
            return null;
        }
        AdvertisingInfo advertisingInfo = getAdvertisingInfo();
        if (advertisingInfo == null || advertisingInfo.limitAdTrackingEnabled) {
            return null;
        }
        return advertisingInfo.advertisingId;
    }

    private void putNonNullIdInto(Map<DeviceIdentifierType, String> idMap, DeviceIdentifierType idKey, String idValue) {
        if (idValue != null) {
            idMap.put(idKey, idValue);
        }
    }

    public String getAndroidId() {
        boolean shouldLimitAdTracking = Boolean.TRUE.equals(explicitCheckLimitAdTracking());
        if (!shouldCollectHardwareIds() || shouldLimitAdTracking) {
            return null;
        }
        String androidId = Secure.getString(this.appContext.getContentResolver(), "android_id");
        if ("9774d56d682e549c".equals(androidId)) {
            return null;
        }
        return formatId(androidId);
    }

    protected boolean shouldCollectHardwareIds() {
        return this.collectHardwareIds && !this.firebaseInfo.isFirebaseCrashlyticsEnabled(this.appContext);
    }

    synchronized AdvertisingInfo getAdvertisingInfo() {
        if (!this.fetchedAdvertisingInfo) {
            this.advertisingInfo = this.advertisingInfoProvider.getAdvertisingInfo();
            this.fetchedAdvertisingInfo = true;
        }
        return this.advertisingInfo;
    }

    private Boolean explicitCheckLimitAdTracking() {
        AdvertisingInfo advertisingInfo = getAdvertisingInfo();
        if (advertisingInfo != null) {
            return Boolean.valueOf(advertisingInfo.limitAdTrackingEnabled);
        }
        return null;
    }
}
