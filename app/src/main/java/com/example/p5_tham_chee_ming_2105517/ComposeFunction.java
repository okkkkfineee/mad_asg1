package com.example.p5_tham_chee_ming_2105517;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ComposeFunction {
    private static int currentRound = 1;
    private static int maxRounds;
    private static int correctAnswers = 0;
    private static HeartFunction heartFunction;
    private static LinearLayout gameContainer1;
    private static LinearLayout gameContainer2;
    private static String gameMode;
    private static String diffSelected;
    private static int targetNumber;
    private static List<Integer> numberButtons;
    private static List<Integer> selectedNumbers = new ArrayList<>();

    public static void setupComposeGame(Activity activity, int round, HeartFunction heartFunc, String gamemode, String difficulty) {
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

        selectedNumbers.clear();
        numberButtons = new ArrayList<>();

        gameContainer1 = activity.findViewById(R.id.game_container1);
        gameContainer2 = activity.findViewById(R.id.game_container2);
        if (gameContainer1 != null) {
            gameContainer1.removeAllViews();
        }
        if (gameContainer2 != null) {
            gameContainer2.removeAllViews();
        }

        Random random = new Random();
        targetNumber = random.nextInt(18) + 1;

        int maxAttempts = 100;
        int attempts = 0;
        boolean foundValidPair = false;
        int num1 = -1, num2 = -1;

        // Find a valid pair for the target numbers
        while (!foundValidPair && attempts < maxAttempts) {
            num1 = random.nextInt(10);
            num2 = random.nextInt(10);
            foundValidPair = (num1 != num2 && (num1 + num2 == targetNumber));
            attempts++;
        }

        // If no valid pair found after 100 tries
        if (!foundValidPair) {
            num1 = targetNumber / 2;
            num2 = targetNumber - num1;
            if (num1 == num2) {
                num1--;
                num2++;
            }
            if (num1 < 0) num1 = 0;
            if (num2 > 9) num2 = 9;
        }

        List<Integer> numbers = new ArrayList<>();
        numbers.add(num1);
        numbers.add(num2);

        Set<Integer> uniqueNumbers = new HashSet<>(numbers);
        while (uniqueNumbers.size() < 4) {
            uniqueNumbers.add(random.nextInt(10));
        }

        numbers = new ArrayList<>(uniqueNumbers);
        Collections.shuffle(numbers);

        // Instruction
        TextView instruction = activity.findViewById(R.id.game_instruction);
        instruction.setText("Select two numbers to make: ");
        instruction.setTextSize(18);

        // Game Container 1 for Target Numbers
        LinearLayout targetRow = new LinearLayout(activity);
        targetRow.setOrientation(LinearLayout.HORIZONTAL);
        targetRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        targetRow.setGravity(Gravity.CENTER);

        TextView targetView = new TextView(activity);
        targetView.setText(String.valueOf(targetNumber));
        targetView.setTextSize(28);
        targetView.setTextColor(Color.WHITE);
        targetView.setBackgroundResource(R.drawable.custom_button);
        targetView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(350, 350);
        tvParams.setMargins(20, 20, 20, 20);
        targetView.setLayoutParams(tvParams);

        targetRow.addView(targetView);
        gameContainer1.addView(targetRow);

        // Game Container 2 for buttons
        LinearLayout buttonRow = new LinearLayout(activity);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        for (int num : numbers) {
            Button numButton = new Button(activity);
            numButton.setText(String.valueOf(num));
            numButton.setTextSize(28);
            numButton.setTextColor(Color.WHITE);
            numButton.setBackgroundResource(R.drawable.custom_button);
            numButton.setOnClickListener(v -> handleNumberSelection(activity, num, numButton));

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, 275, 1.0f);
            btnParams.setMargins(20, 20, 20, 20);
            numButton.setLayoutParams(btnParams);

            numberButtons.add(num);
            buttonRow.addView(numButton);
        }

        gameContainer2.addView(buttonRow);

        // Display round info
        TextView roundLeft = activity.findViewById(R.id.round_left);
        roundLeft.setText("Round " + currentRound + "/" + maxRounds);
        roundLeft.setTextSize(16);
        roundLeft.setPadding(0, 20, 0, 0);
    }

    private static void handleNumberSelection(Activity activity, int number, Button button) {
        if (selectedNumbers.size() < 2) {
            selectedNumbers.add(number);
            button.setAlpha(0.5f);
            button.setOnClickListener(v -> undoSelection(activity, number, button));

            // If two buttons is selected
            if (selectedNumbers.size() == 2) {
                checkAnswer(activity);
            }
        }
    }

    private static void undoSelection(Activity activity, int number, Button button) {
        selectedNumbers.remove((Integer) number);
        button.setAlpha(1.0f);
        button.setOnClickListener(v -> handleNumberSelection(activity, number, button));
    }

    private static void checkAnswer(Activity activity) {
        if (selectedNumbers.size() == 2) {
            int sum = selectedNumbers.get(0) + selectedNumbers.get(1);
            boolean isCorrect = (sum == targetNumber);

            for (int i = 0; i < gameContainer2.getChildCount(); i++) {
                LinearLayout buttonRow = (LinearLayout) gameContainer2.getChildAt(i);
                for (int j = 0; j < buttonRow.getChildCount(); j++) {
                    Button btn = (Button) buttonRow.getChildAt(j);
                    int btnValue = Integer.parseInt(btn.getText().toString());

                    if (selectedNumbers.contains(btnValue)) {
                        if (isCorrect) {
                            btn.setBackgroundColor(Color.GREEN);
                        } else {
                            btn.setBackgroundColor(Color.RED);
                        }
                    }
                }
            }

            if (isCorrect) {
                correctAnswers++;
                Toast.makeText(activity, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                heartFunction.decreaseHeart();
                Toast.makeText(activity, "Incorrect!", Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(() -> {
                currentRound++;
                startNewRound(activity);
            }, 500);
        }
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
