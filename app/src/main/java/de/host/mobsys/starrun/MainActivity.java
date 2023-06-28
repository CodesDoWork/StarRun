package de.host.mobsys.starrun;

import static de.host.mobsys.starrun.control.PreferenceInfo.HIGHSCORE;

import android.content.Intent;
import android.os.Bundle;

import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private Sounds sounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sounds = new Sounds(this);
        sounds.playMusic(R.raw.lobby_music);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.highscore.setText(getString(R.string.highscore, storage.get(HIGHSCORE)));
        binding.play.setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        sounds.resumeMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sounds.pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sounds.release();
    }
}