package net.seslash.smartaudioswitch;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.preference.PreferenceFragment;

import android.preference.Preference;

/**
 * Created by seslash.
 * Main settings screen
 */

public class SmartRingerModeSettings extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getFragmentManager().beginTransaction().replace(
                android.R.id.content,
                new MainPreferenceFragment()
        ).commit();
    }

    @Override
    public boolean isValidFragment(String fragmentName) {
        return RingerModeTileService.class.getName().equals(fragmentName);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            // grant DND permissions listener
            Preference dnd_pref = findPreference(getString(R.string.grant_permission_notif_key));
            dnd_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    grantNotificationPolicyPermissions(getActivity());
                    return true;
                }
            });
        }

        @Override
        public void onStart() {
            super.onStart();
            Preference dnd_pref = findPreference(getString(R.string.grant_permission_notif_key));
            if (isNotificationPolicyAccessEnabled(getActivity())) {
                dnd_pref.setEnabled(false);
                dnd_pref.setSelectable(false);
                dnd_pref.setSummary(R.string.dnd_granted_summary);
            } else {
                dnd_pref.setEnabled(true);
                dnd_pref.setSelectable(true);
                dnd_pref.setSummary(R.string.dnd_perm_summary);
            }
        }
    }

    private static void grantNotificationPolicyPermissions(Context context) {
        if (!isNotificationPolicyAccessEnabled(context)) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            context.startActivity(intent);
        }
    }

    public static boolean isNotificationPolicyAccessEnabled(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        return notificationManager != null &&
                notificationManager.isNotificationPolicyAccessGranted();
    }
}
