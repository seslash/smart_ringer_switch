package net.seslash.smartaudioswitch;

import android.media.AudioManager;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.Vector;

/**
 * Created by seslash
 * Basic class to store information about an individual ringer mode.
 * Also includes static functions with hardcoded presets for ringer modes.
 */

final class RingerMode {

    static final Integer INVALID_RINGER_MODE = -1;
    private final Integer ringerMode;
    private boolean isActive = false;

    public RingerMode(Integer ringer_mode, boolean is_active) {
        if (isValidRingerMode(ringer_mode)) {
            this.ringerMode = ringer_mode;
            this.isActive = is_active;
        } else {
            this.ringerMode = INVALID_RINGER_MODE;
        }
    }

    boolean getIsActive() {
        return this.isActive;
    }

    @Contract(pure = true)
    @NonNull
    Integer getRingerMode() {
        return this.ringerMode;
    }

    @Contract(pure = true)
    Integer getNameResId() {
        switch (this.ringerMode) {
            case AudioManager.RINGER_MODE_NORMAL:
                return R.string.state_normal;

            case AudioManager.RINGER_MODE_VIBRATE:
                return R.string.state_vibrate;

            case AudioManager.RINGER_MODE_SILENT:
                return R.string.state_silent;

            default:
                return R.string.state_invalid;
        }
    }

    @Contract(pure = true)
    public Integer getIcon() {
        switch (this.ringerMode) {
            case AudioManager.RINGER_MODE_NORMAL:
                return R.drawable.ic_volume_up_black_24dp;

            case AudioManager.RINGER_MODE_VIBRATE:
                return R.drawable.ic_vibration_black_24dp;

            case AudioManager.RINGER_MODE_SILENT:
                return R.drawable.ic_volume_off_black_24dp;

            default:
                return R.drawable.ic_warning_black_24dp;
        }
    }

    static Integer getSettingsKeyResId(Integer ringer_mode) {
        switch (ringer_mode) {
            case AudioManager.RINGER_MODE_NORMAL:
                return R.string.state_normal_key;

            case AudioManager.RINGER_MODE_VIBRATE:
                return R.string.state_vibrate_key;

            case AudioManager.RINGER_MODE_SILENT:
                return R.string.state_silent_key;

            default:
                return R.string.state_invalid_key;
        }
    }

    private static Vector<Integer> getValidRingerModes() {
        Vector<Integer> valid_modes = new Vector<>(3);
        valid_modes.add(AudioManager.RINGER_MODE_NORMAL);
        valid_modes.add(AudioManager.RINGER_MODE_VIBRATE);
        valid_modes.add(AudioManager.RINGER_MODE_SILENT);

        return valid_modes;
    }

    static Vector<Integer> getSupportedRingerModes() {
        Vector<Integer> supported_modes = getValidRingerModes();
        supported_modes.add(INVALID_RINGER_MODE);

        return supported_modes;
    }

    @NonNull
    @Contract(pure = true)
    static Integer getRingerOrderNum(Integer ringer_mode) {
        switch (ringer_mode) {
            case AudioManager.RINGER_MODE_NORMAL:
                return 0;

            case AudioManager.RINGER_MODE_VIBRATE:
                return 1;

            case AudioManager.RINGER_MODE_SILENT:
                return 2;

            default:
                return 3;
        }
    }

    @Contract(pure = true)
    private static boolean isValidRingerMode(Integer ringer_mode) {
        return getValidRingerModes().contains(ringer_mode);
    }
}
