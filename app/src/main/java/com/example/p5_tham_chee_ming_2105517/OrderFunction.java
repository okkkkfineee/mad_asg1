package com.example.p5_tham_chee_ming_2105517;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
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

public class OrderFunction {
    private static int currentRound = 1;
    private static int maxRounds;
    private static int correctAnswers = 0;
    private static String orderType;
    private static HeartFunction heartFunction;
    private static String gameMode;
    private static String diffSelected;
    private static List<Integer> userOrder;
    private static List<Integer> correctOrder;
    private static List<Button> numberButtons;

    public static void setupOrderGame(Activity activity, int round, HeartFunction heartFunc, String gamemode, String difficulty) {
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

        orderType = new Random().nextBoolean() ? "ascending" : "descending";
        userOrder = new ArrayList<>();
        numberButtons = new ArrayList<>();

        LinearLayout gameContainer1 = activity.findViewById(R.id.game_container1);
        LinearLayout gameContainer2 = activity.findViewById(R.id.game_container2);
        gameContainer1.removeAllViews();
        gameContainer2.removeAllViews();

        Random random = new Random();
        Set<Integer> uniqueNumbers = new HashSet<>();
        while (uniqueNumbers.size() < 3) {
            uniqueNumbers.add(random.nextInt(100));
        }
        List<Integer> numbers = new ArrayList<>(uniqueNumbers);
        Collections.shuffle(numbers);

        correctOrder = new ArrayList<>(numbers);
        if (orderType.equals("ascending")) {
            Collections.sort(correctOrder);
        } else {
            correctOrder.sort(Collections.reverseOrder());
        }

        // Instructions
        TextView instruction = activity.findViewById(R.id.game_instruction);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Order the numbers in ");
        int start = builder.length();
        String boldText = orderType.equals("ascending") ? "ascending" : "descending";
        builder.append(boldText);
        int end = builder.length();
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(" order:");

        instruction.setText(builder);
        instruction.setTextSize(18);

        // Game Container 1 for showing the selections
        LinearLayout slotRow = new LinearLayout(activity);
        slotRow.setOrientation(LinearLayout.HORIZONTAL);
        slotRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        for (int i = 0; i < 3; i++) {
            TextView emptySlot = new TextView(activity);
            emptySlot.setText("_");
            emptySlot.setTextSize(28);
            emptySlot.setTextColor(Color.WHITE);
            emptySlot.setBackgroundResource(R.drawable.custom_button);
            emptySlot.setTag("empty");
            emptySlot.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(0, 275, 1.0f);
            slotParams.setMargins(20, 20, 20, 20);
            emptySlot.setLayoutParams(slotParams);

            slotRow.addView(emptySlot);
        }

        gameContainer1.addView(slotRow);

        // Game Container 2 for showing the buttons
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
            numButton.setOnClickListener(v -> handleNumberSelection(activity, num, numButton, gameContainer1));

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, 275, 1.0f);
            btnParams.setMargins(20, 20, 20, 20);
            numButton.setLayoutParams(btnParams);

            numberButtons.add(numButton);
            buttonRow.addView(numButton);
        }

        gameContainer2.addView(buttonRow);

        // Display round info
        TextView roundLeft = activity.findViewById(R.id.round_left);
        roundLeft.setText("Round " + currentRound + "/" + maxRounds);
        roundLeft.setTextSize(16);
        roundLeft.setPadding(0, 20, 0, 0);
    }

    private static void handleNumberSelection(Activity activity, int number, Button button, LinearLayout gameContainer1) {
        LinearLayout slotRow = (LinearLayout) gameContainer1.getChildAt(0); // Get the row layout

        for (int i = 0; i < slotRow.getChildCount(); i++) {
            TextView slot = (TextView) slotRow.getChildAt(i);
            if (slot.getText().equals("_")) {
                slot.setText(String.valueOf(number));
                slot.setTag(number);
                userOrder.add(number);
                button.setBackgroundResource(R.drawable.custom_button2);
                button.setTag(slot);
                button.setOnClickListener(v -> undoSelection(activity, number, button));
                break;
            }
        }

        if (userOrder.size() == 3) {
            checkAnswer(activity);
        }
    }

    private static void undoSelection(Activity activity, int number, Button button) {
        TextView slot = (TextView) button.getTag();

        if (slot != null && userOrder.contains(number)) {
            slot.setText("_");
            slot.setTag("empty");
            userOrder.remove((Integer) number);

            button.setBackgroundResource(R.drawable.custom_button);

            button.setOnClickListener(v -> handleNumberSelection(activity, number, button, activity.findViewById(R.id.game_container1)));
        }
    }

    private static void checkAnswer(Activity activity) {
        boolean isCorrect = userOrder.equals(correctOrder);

        if (isCorrect) {
            Toast.makeText(activity, "Correct!", Toast.LENGTH_SHORT).show();
            correctAnswers++;
        } else {
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
