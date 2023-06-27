package de.host.mobsys.starrun;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.host.mobsys.starrun.control.PreferenceStorage;

/**
 * A superclass for fullscreen activities with a preference storage.
 */
public class BaseActivity extends AppCompatActivity {

    protected PreferenceStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = new PreferenceStorage(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            hideSystemUi();
        }
    }

    private void hideSystemUi() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    protected MaterialAlertDialogBuilder createDialogBuilder() {
        return new MaterialAlertDialogBuilder(this, R.style.materialAlertDialogStyle)
            .setBackground(AppCompatResources.getDrawable(this, R.drawable.space_bit));
    }
}
