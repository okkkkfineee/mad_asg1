package com.example.p5_tham_chee_ming_2105517;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class GeneralButtonUtils {
    public static void setupButtons(Activity activity, ImageButton buttonHelp, ImageButton buttonSound) {
        buttonHelp.setOnClickListener(v -> showHelpDialog(activity));
        buttonSound.setOnClickListener(v -> toggleSound(activity, buttonSound));
        updateSoundButton(buttonSound);
    }

    private static void showHelpDialog(Activity activity) {
        ScrollView scrollView = new ScrollView(activity);
        TextView textView = new TextView(activity);
        textView.setText(activity.getString(R.string.game_instructions));
        textView.setPadding(70, 50, 70, 50);
        scrollView.addView(textView);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Game Instruction")
                .setView(scrollView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private static void toggleSound(Activity activity, ImageButton buttonSound) {
        MainMenu.isMuted = !MainMenu.isMuted;

        if (MainMenu.mp != null) {
            if (MainMenu.isMuted) {
                MainMenu.mp.pause();
            } else if (!MainMenu.appInBackground) {
                MainMenu.mp.start();
            }
            updateSoundButton(buttonSound);
        }
    }

    private static void updateSoundButton(ImageButton buttonSound) {
        if (buttonSound != null) {
            buttonSound.setImageResource(MainMenu.isMuted ?
                    R.drawable.icon_mute : R.drawable.icon_speaker);
        }
    }
}