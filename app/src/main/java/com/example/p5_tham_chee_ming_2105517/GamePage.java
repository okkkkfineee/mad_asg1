package com.example.p5_tham_chee_ming_2105517;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GamePage extends AppCompatActivity {
    private HeartFunction heartFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnExit = findViewById(R.id.imgbutton_exit);
        ImageButton btnHelp = findViewById(R.id.imgbutton_help);
        ImageButton btnSound = findViewById(R.id.imgbutton_sound);
        GeneralButtonUtils.setupButtons(this, btnHelp, btnSound);
        btnExit.setOnClickListener(v -> exitGamepage());

        // Get Gamemode & Difficulty selection from previous pages
        String gamemode = getIntent().getStringExtra("gamemode");
        String difficulty = getIntent().getStringExtra("difficulty");

        TextView heartTV = findViewById(R.id.heart_remaining);

        // Set difficulty-based values
        int round = 10;
        int initialHearts;
        switch (difficulty) {
            case "easy":
                initialHearts = 5;
                break;
            case "normal":
                initialHearts = 3;
                break;
            case "hard":
                initialHearts = 1;
                break;
            default:
                initialHearts = 3;
        }

        heartFunction = new HeartFunction(this, heartTV, initialHearts);
        setupGameMode(gamemode, round, difficulty);
    }

    private void exitGamepage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Game")
                .setMessage("Are you sure you want to exit the current game and return to the homepage?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean wasPlaying = MainMenu.mp != null && MainMenu.mp.isPlaying();
                    finish();
                    Intent intent = new Intent(GamePage.this, MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    if (wasPlaying && !MainMenu.isMuted && MainMenu.mp != null) {
                        MainMenu.mp.start();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setupGameMode(String gamemode, int round, String difficulty) {
        TextView gamemodeSelected = findViewById(R.id.gamemode_selected);

        if (gamemode == null) {
            Toast.makeText(this, "Error: No game mode selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switch (gamemode) {
            case "Compare":
                gamemodeSelected.setText(gamemode);
                CompareFunction.setupCompareGame(this, round, heartFunction, gamemode, difficulty);
                break;
            case "Order":
                gamemodeSelected.setText(gamemode);
                 OrderFunction.setupOrderGame(this, round, heartFunction, gamemode, difficulty);
                break;
            case "Compose":
                gamemodeSelected.setText(gamemode);
                 ComposeFunction.setupComposeGame(this, round, heartFunction, gamemode, difficulty);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainMenu.appInBackground = false;
        if (MainMenu.mp != null && !MainMenu.isMuted) {
            MainMenu.mp.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing()) {
            MainMenu.appInBackground = true;
            if (MainMenu.mp != null && MainMenu.mp.isPlaying()) {
                MainMenu.mp.pause();
            }
        }
    }
}
