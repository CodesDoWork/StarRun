package de.host.mobsys.starrun;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.size.systems.PercentSizeSystem;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.databinding.GameOverBinding;
import de.host.mobsys.starrun.databinding.PauseMenuBinding;
import de.host.mobsys.starrun.models.Score;

public class GameActivity extends BaseActivity {

    private Sounds sounds;
    private Score score;
    private Game game;

    private AlertDialog menu;
    private AlertDialog gameOverMenu;

    private int highScore = 0;
    private boolean isRecreating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRecreating = false;

        setupSizeSystem();
        sounds = new Sounds(this);
        score = new Score(storage);

        // only update if unset
        if (highScore == 0) {
            highScore = score.getHighScore();
        }

        createMenuDialog();
        createGameOverDialog();
        setupGame();
        game.start();
    }

    private void setupSizeSystem() {
        SizeSystem.setup(getWindowManager().getDefaultDisplay());
        SizeSystem.setSizeSystem(new PercentSizeSystem());
    }

    private void setupGame() {
        game = new Game(this, sounds, score, highScore);
        game.setOpenMenuListener(menu::show);
        game.addGameOverListener(() -> runOnUiThread(gameOverMenu::show));
        setContentView(game.getView());
    }

    private void createMenuDialog() {
        PauseMenuBinding dialogBinding = PauseMenuBinding.inflate(getLayoutInflater());
        dialogBinding.resume.setOnClickListener(v -> menu.dismiss());
        dialogBinding.exit.setOnClickListener(v -> finish());
        dialogBinding.restart.setOnClickListener(v -> {
            isRecreating = true;
            menu.dismiss();
            recreate();
        });

        menu = createDialogBuilder()
            .setMessage(getString(R.string.pause))
            .setView(dialogBinding.getRoot())
            .setOnDismissListener(v -> game.start())
            .create();
    }

    private void createGameOverDialog() {
        GameOverBinding gameOverBinding = GameOverBinding.inflate(getLayoutInflater());
        gameOverBinding.exit.setOnClickListener(v -> finish());
        gameOverBinding.restart.setOnClickListener(v -> {
            isRecreating = true;
            menu.dismiss();
            recreate();
        });

        gameOverMenu = createDialogBuilder()
            .setMessage(getString(R.string.game_over))
            .setView(gameOverBinding.getRoot())
            .setBackground(ContextCompat.getDrawable(this, R.drawable.game_over))
            .setCancelable(false)
            .create();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
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

    @Override
    public void onBackPressed() {
        if (!game.isMenuDisabled()) {
            pauseGame();
        }
    }

    private void pauseGame() {
        game.pause();
        if (canOpenMenu()) {
            menu.show();
        }
    }

    private boolean canOpenMenu() {
        return !menu.isShowing() && !isRecreating;
    }
}

