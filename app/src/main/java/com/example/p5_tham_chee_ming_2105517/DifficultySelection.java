package com.example.p5_tham_chee_ming_2105517;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DifficultySelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_difficulty_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnHelp = findViewById(R.id.imgbutton_help);
        ImageButton btnSound = findViewById(R.id.imgbutton_sound);
        GeneralButtonUtils.setupButtons(this, btnHelp, btnSound);

        String gamemode = getIntent().getStringExtra("gamemode");
        TextView gamemode_selected = findViewById(R.id.gamemode_selected);
        if (gamemode != null) {
            gamemode_selected.setText(gamemode);
        }

        Button btnEasy = findViewById(R.id.button_easy);
        Button btnNormal = findViewById(R.id.button_normal);
        Button btnHard = findViewById(R.id.button_hard);
        btnEasy.setOnClickListener(v -> switchPage(gamemode, "easy"));
        btnNormal.setOnClickListener(v -> switchPage(gamemode, "normal"));
        btnHard.setOnClickListener(v -> switchPage(gamemode, "hard"));

        Button btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(DifficultySelection.this, MainMenu.class);
            startActivity(intent);
        });
    }

    private void switchPage(String gamemode, String difficulty) {
        Intent intent = new Intent(DifficultySelection.this, GamePage.class);
        intent.putExtra("gamemode", gamemode);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
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