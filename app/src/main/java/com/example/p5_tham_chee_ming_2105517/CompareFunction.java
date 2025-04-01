package com.example.p5_tham_chee_ming_2105517;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class CompareFunction {

    private static int currentRound = 1;
    private static int maxRounds;
    private static int correctAnswers = 0; // Track correct answers
    private static String gametype;
    private static HeartFunction heartFunction;
    private static String gameMode;
    private static String diffSelected;

    public static void setupCompareGame(Activity activity, int round, HeartFunction heartFunc, String gamemode, String difficulty) {
        maxRounds = round;
        currentRound = 1;
        correctAnswers = 0;
        heartFunction = heartFunc;
        gameMode = gamemode;
        diffSelected = difficulty;
        startNewRound(activity);
    }

    private static void startNewRound(Activity activity) {
        if (currentRound > maxRounds || heartFunction.getHearts() <= 0) {
            endGame(activity);
            return;
        }

        gametype = new Random().nextBoolean() ? "larger" : "smaller";

        LinearLayout gameContainer1 = activity.findViewById(R.id.game_container1);
        gameContainer1.removeAllViews();

        Random random = new Random();
        int tempNum1 = random.nextInt(100);
        int tempNum2 = random.nextInt(100);

        while (tempNum1 == tempNum2) {
            tempNum2 = random.nextInt(100);
        }

        final int num1 = tempNum1;
        final int num2 = tempNum2;

        // Instruction
        TextView instruction = activity.findViewById(R.id.game_instruction);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Select the ");
        int start = builder.length();
        String boldText = gametype.equals("larger") ? "larger" : "smaller";
        builder.append(boldText);
        int end = builder.length();
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(" number:");
        instruction.setText(builder);
        instruction.setTextSize(18);

        // Game Container 1 (2 buttons for selection)
        Button btn1 = new Button(activity);
        Button btn2 = new Button(activity);

        btn1.setText(String.valueOf(num1));
        btn2.setText(String.valueOf(num2));

        btn1.setTextColor(Color.WHITE);
        btn2.setTextColor(Color.WHITE);
        btn1.setTextSize(28);
        btn2.setTextSize(28);
        btn1.setBackgroundResource(R.drawable.custom_button);
        btn2.setBackgroundResource(R.drawable.custom_button);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, 350, 1.0f);
        btnParams.setMargins(50, 50, 50, 50);
        btn1.setLayoutParams(btnParams);
        btn2.setLayoutParams(btnParams);
        gameContainer1.setOrientation(LinearLayout.HORIZONTAL);
        gameContainer1.addView(btn1);
        gameContainer1.addView(btn2);

        // Display round info
        TextView roundLeft = activity.findViewById(R.id.round_left);
        roundLeft.setText("Round " + currentRound + "/" + maxRounds);
        roundLeft.setTextSize(16);
        roundLeft.setPadding(0, 20, 0, 0);

        btn1.setOnClickListener(v -> checkAnswer(activity, num1, num2, btn1, btn2));
        btn2.setOnClickListener(v -> checkAnswer(activity, num2, num1, btn2, btn1));
    }

    private static void checkAnswer(Activity activity, int selected, int other, Button btnSelected, Button btnOther) {
        boolean isCorrect;

        if (gametype.equals("larger")) {
            isCorrect = selected > other;
        } else {
            isCorrect = selected < other;
        }

        if (isCorrect) {
            btnSelected.setBackgroundColor(Color.GREEN);
            correctAnswers++;
            Toast.makeText(activity, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            btnSelected.setBackgroundColor(Color.RED);
            btnOther.setBackgroundColor(Color.GREEN);
            Toast.makeText(activity, "Incorrect!", Toast.LENGTH_SHORT).show();

            heartFunction.decreaseHeart();
        }

        new Handler().postDelayed(() -> {
            currentRound++;
            startNewRound(activity);
        }, 500);
    }

    private static void endGame(Activity activity) {
        Intent intent = new Intent(activity, ResultsPage.class);
        intent.putExtra("totalRounds", maxRounds);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("remainingHearts", heartFunction.getHearts());
        intent.putExtra("gamemode", gameMode);
        intent.putExtra("difficulty", diffSelected);

        activity.startActivity(intent);
        activity.finish();
    }
}
