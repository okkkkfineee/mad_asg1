package com.example.p5_tham_chee_ming_2105517;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainMenu extends AppCompatActivity {
    public static MediaPlayer mp;
    public static boolean isMuted = false;
    public static boolean appInBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.bg_music);
            mp.setLooping(true);
        }
        ImageButton btnHelp = findViewById(R.id.imgbutton_help);
        ImageButton btnSound = findViewById(R.id.imgbutton_sound);
        GeneralButtonUtils.setupButtons(this, btnHelp, btnSound);

        Button btnCompare = findViewById(R.id.button_compare);
        Button btnOrder = findViewById(R.id.button_order);
        Button btnCompose = findViewById(R.id.button_compose);
        btnCompare.setOnClickListener(v -> switchPage("Compare"));
        btnOrder.setOnClickListener(v -> switchPage("Order"));
        btnCompose.setOnClickListener(v -> switchPage("Compose"));

        Button btnExit = findViewById(R.id.button_exit);
        btnExit.setOnClickListener(v -> exitGame());
    }

    private void exitGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Game")
                .setMessage("Are you sure you want to exit the game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (mp != null) {
                        mp.release();
                        mp = null;
                    }
                    finishAffinity();
                    System.exit(0);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void switchPage(String gamemode) {
        Intent intent = new Intent(MainMenu.this, DifficultySelection.class);
        intent.putExtra("gamemode", gamemode);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appInBackground = false;
        if (mp != null && !isMuted) {
            mp.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing()) {
            appInBackground = true;
            if (mp != null && mp.isPlaying()) {
                mp.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (isFinishing() && mp != null) {
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }
}