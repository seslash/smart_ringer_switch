package net.seslash.smartaudioswitch;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.service.quicksettings.TileService;

/**
 * Created by seslash.
 * Tile service for the Smart Ringer Switch
 */

public class RingerModeTileService extends TileService {

    private RingerModeController ringerModeController;

    @Override
    public void onClick() {
        super.onClick();

        if (!SmartRingerModeSettings.isNotificationPolicyAccessEnabled(this)) {
            Intent intent = new Intent(getApplicationContext(), SmartRingerModeSettings.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivityAndCollapse(intent);

            return;
        }

        this.ringerModeController.setNextRingerMode();
    }

    @Override
    public void onStartListening() {
        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        this.ringerModeController = new RingerModeController(this);
        super.onStartListening();
        ringerModeController.updateTile();
        registerReceiver(this.ringerModeController, filter);
    }

    @Override
    public void onStopListening() {
        unregisterReceiver(ringerModeController);

        if (this.ringerModeController != null) {
            this.ringerModeController = null;
        }

        super.onStopListening();
    }

    @Override
    public void onDestroy() {
        if (this.ringerModeController != null) {
            this.ringerModeController = null;
        }

        super.onDestroy();
    }
}
