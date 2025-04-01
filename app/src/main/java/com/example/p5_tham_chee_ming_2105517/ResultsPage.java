package com.example.p5_tham_chee_ming_2105517;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnHelp = findViewById(R.id.imgbutton_help);
        ImageButton btnSound = findViewById(R.id.imgbutton_sound);
        GeneralButtonUtils.setupButtons(this, btnHelp, btnSound);

        // Get the related data from game page
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
        int totalRounds = getIntent().getIntExtra("totalRounds", 0);
        int heartsLeft = getIntent().getIntExtra("remainingHearts", 0);
        String gamemode = getIntent().getStringExtra("gamemode");
        String difficulty = getIntent().getStringExtra("difficulty");

        TextView txtScore = findViewById(R.id.txt_score);
        TextView txtHeart = findViewById(R.id.txt_heart);
        TextView txtMessage = findViewById(R.id.txt_message);
        ImageButton btnMainMenu = findViewById(R.id.btn_main_menu);
        ImageButton btnReplay = findViewById(R.id.btn_replay);

        txtScore.setText(correctAnswers + " / " + totalRounds);
        txtHeart.setText("❤️ left: " + heartsLeft);

        if (correctAnswers == totalRounds) {
            txtMessage.setText("Perfect Score!");
        } else if (heartsLeft > 0) {
            txtMessage.setText("You did well! Keep going!");
        } else {
            txtMessage.setText("Game Over! Try Again!");
        }

        btnMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsPage.this, MainMenu.class);
            startActivity(intent);
        });

        btnReplay.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsPage.this, GamePage.class);
            intent.putExtra("gamemode", gamemode);
            intent.putExtra("difficulty", difficulty);
            startActivity(intent);
        });
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