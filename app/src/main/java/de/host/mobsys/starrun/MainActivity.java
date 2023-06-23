package de.host.mobsys.starrun;

import static de.host.mobsys.starrun.control.PreferenceInfo.HIGHSCORE;

import android.content.Intent;
import android.os.Bundle;

import de.host.mobsys.starrun.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.highscore.setText(getString(R.string.highscore, storage.get(HIGHSCORE)));
        binding.play.setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
    }
}