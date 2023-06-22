package de.host.mobsys.starrun;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.starrun.R;

import java.io.IOException;
import java.io.InputStream;

import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.GameView;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.size.systems.PercentSizeSystem;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.CollisionObject;
import de.host.mobsys.starrun.views.Player;

import java.time.Duration;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private final GameLayer backgroundLayer = new GameLayer();
    private final GameLayer collisionLayer = new GameLayer();
    private final GameLayer overlayLayer = new GameLayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupSizeSystem();
        setupGame();

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

    private void setupSizeSystem() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        SizeSystem.setup(size.x, size.y);
        SizeSystem.setSizeSystem(new PercentSizeSystem());
    }

    private void setupGame() {
        GameView game = new GameView(this);
        setContentView(game);
        game.start();

        game.add(backgroundLayer);
        game.add(collisionLayer);
        game.add(overlayLayer);

        createBackground();
        createPlayer();
        createCollisionObject();

        scheduleCollisionObjectCreation();
    }

    private void createBackground() {
        Background background = new Background(
            Size.fromHeightAndAspectRatio(110, 2),
            loadAsset("space_bit_v2.png")
        );
        backgroundLayer.add(background);
    }



    private void createPlayer() {
        Rect playerRect = new Rect(
            new Position(5, 45),
            Size.fromWidthAndAspectRatio(15, 428 / 168f)
        );
        Player player = new Player(playerRect, loadAsset("ship_cut.png"));
        player.addOnMoveListener((x, y) -> backgroundLayer.translate(0, -y / 100));
        collisionLayer.add(player);
    }

    float s1;
    float s2;
    float s3;
    private void createCollisionObject() {


        Random randAst = new Random();
        Random randNum = new Random();

        int minS = 5;
        int maxS = 15;

        int minPY = 0;
        int maxPY = 90;

        int minPX = 95;
        int maxPX = 110;

        int ast = 4;
        int num = 2;

        for(int i = 0;i <= randNum.nextInt(num);i++){
            int int_ast = randAst.nextInt(ast);

            String asset = "asteriod_gray.png";
        Rect collisionRect = new Rect(
            new Position((int)Math.floor(Math.random() * (maxPX - minPX + 1) + minPX), (int)Math.floor(Math.random() * (maxPY - minPY + 1) + minPY)),  // Position des Objekts
            new Size((int)Math.floor(Math.random() * (maxS - minS + 1) + minS), (int)Math.floor(Math.random() * (maxS - minS + 1) + minS)) // Größe des Objekts

            );

            if(i==0){
            s1 = collisionRect.getTop();}
            if(i==1){
                s2 = collisionRect.getTop();}


        switch(int_ast){
            case 0:
                asset = "asteriod_gray.png";
                break;
            case 1:
                asset = "asteriod_gray_v1.png";
                break;

            case 2:
                asset = "asteriod_grey_v2.png";
                break;

            case 3:
                asset = "red_asteriod.png";
                break;

            case 4:
                asset = "red_double_asteroid.png";
                break;


        }
        CollisionObject collisionObject = new CollisionObject(collisionRect, loadAsset(asset));

            if(Math.abs(s2-s1)<=20){

            }else {
                collisionLayer.add(collisionObject);
            }
        }


    }


    private void scheduleCollisionObjectCreation() {
        // Wiederholte Ausführung der Methode createCollisionObject() nach einer Verzögerung
        int delayMillis = 2500;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createCollisionObject();
                scheduleCollisionObjectCreation();
            }
        }, delayMillis);
    }




    private Bitmap loadAsset(String fileName) {
        AssetManager assets = getResources().getAssets();
        try (InputStream playerSpriteStream = assets.open(fileName)) {
            return BitmapFactory.decodeStream(playerSpriteStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }







}

