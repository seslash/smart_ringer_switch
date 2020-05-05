package net.seslash.smartaudioswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by seslash.
 * Holds the logic for switching ringer modes.
 */

final class RingerModeController extends BroadcastReceiver {
    private static final Integer DEFAULT_RINGER_MODE = AudioManager.RINGER_MODE_NORMAL;

    private Integer activeRingerModeKey;
    private HashMap<Integer, RingerMode> ringerModes = new HashMap<>(4);
    private final RingerModeTileService soundTileSvc;


    public RingerModeController(RingerModeTileService sound_tile_service) {
        this.soundTileSvc = sound_tile_service;
        Integer ringer_mode = RingerMode.INVALID_RINGER_MODE;
        this.activeRingerModeKey = RingerMode.getRingerOrderNum(ringer_mode);
        this.ringerModes = this.getRingerModes();
    }

    public void setNextRingerMode() {
        if (this.getCurrentRingerMode().equals(RingerMode.INVALID_RINGER_MODE)) {
            this.setRinger(DEFAULT_RINGER_MODE);
        } else {
            this.setRinger(this.ringerModes.get(this.getNextRingerID()).getRingerMode());
        }

        AudioManager audioManager =
                (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);

        assert audioManager != null;
        audioManager.setRingerMode(this.getCurrentRingerMode());
    }

    public void updateTile() {
        Tile tile = this.soundTileSvc.getQsTile();
        tile.setIcon(Icon.createWithResource(this.getContext(), this.getIcon()));
        tile.setLabel(this.getFriendlyName());
        tile.updateTile();
    }

    private void setRinger(Integer ringer_mode) {
        if (SmartRingerModeSettings.isNotificationPolicyAccessEnabled(this.getContext())) {
            this.activeRingerModeKey = RingerMode.getRingerOrderNum(ringer_mode);
        } else {
            this.activeRingerModeKey = RingerMode.getRingerOrderNum(RingerMode.INVALID_RINGER_MODE);
        }
    }

    @NonNull
    private Context getContext() {
        return this.soundTileSvc.getApplicationContext();
    }


    @NonNull
    private String getFriendlyName() {
        return this.getContext().getString(this.getCurrentRingerModeObject().getNameResId());
    }

    @NonNull
    private RingerMode getCurrentRingerModeObject() {
        RingerMode ringer_mode = this.ringerModes.get(this.activeRingerModeKey);

        return ringer_mode != null
                ? ringer_mode
                : this.ringerModes.entrySet().iterator().next().getValue();
    }

    @NonNull
    private Integer getDefaultRingerMode() {
        return this.ringerModes.get(RingerMode.getRingerOrderNum(DEFAULT_RINGER_MODE)).getIsActive()
                ? DEFAULT_RINGER_MODE
                : this.getNextRingerID();
    }

    @NonNull
    private Integer getCurrentRingerMode() {
        return this.getCurrentRingerModeObject().getRingerMode();
    }

    private int getIcon() {
        return this.getCurrentRingerModeObject().getIcon();
    }

    private Integer getNextRingerID() {
        RingerMode active_ringer_mode = this.getCurrentRingerModeObject();
        RingerMode next_ringer_mode = this.ringerModes.get(this.activeRingerModeKey + 1);

        if (next_ringer_mode != null && next_ringer_mode.getIsActive()) {
            return this.activeRingerModeKey + 1;
        }

        Integer next_ringer_mode_key = this.activeRingerModeKey;
        for (Map.Entry<Integer, RingerMode> ringer_mode: this.ringerModes.entrySet()) {
            if (ringer_mode.getValue().getRingerMode().equals(active_ringer_mode.getRingerMode())) {
                continue;
            }
            if (ringer_mode.getValue().getIsActive()) {
                next_ringer_mode_key = ringer_mode.getKey();
                break;
            }
        }

        return next_ringer_mode_key;

    }

    private HashMap<Integer, RingerMode> getRingerModes() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        HashMap<Integer, RingerMode> ringer_modes = new HashMap<>(3);

        for (Map.Entry<Integer, Integer> map: getValidOrderedRingerModes().entrySet()) {
            boolean is_active_mode = preferences.getBoolean(
                    this.getContext().getString(RingerMode.getSettingsKeyResId(map.getValue())),
                    true
            );

            ringer_modes.put(map.getKey(), new RingerMode(map.getValue(), is_active_mode));
        }

        return ringer_modes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        switch (action) {
            case AudioManager.RINGER_MODE_CHANGED_ACTION:
                Integer new_ringer_mode = intent.getIntExtra(
                        AudioManager.EXTRA_RINGER_MODE,
                        getDefaultRingerMode()
                );
                if (!this.getCurrentRingerModeObject().getRingerMode().equals(new_ringer_mode)) {
                    this.setRinger(new_ringer_mode);
                }
                this.updateTile();
        }
    }

    private static HashMap<Integer, Integer> getValidOrderedRingerModes() {
        HashMap<Integer, Integer> ringer_modes_order_map = new HashMap<>();
        for (Integer valid_ringer_mode: RingerMode.getSupportedRingerModes()) {
            ringer_modes_order_map.put(
                    RingerMode.getRingerOrderNum(valid_ringer_mode),
                    valid_ringer_mode
            );
        }
        return ringer_modes_order_map;
    }
}
