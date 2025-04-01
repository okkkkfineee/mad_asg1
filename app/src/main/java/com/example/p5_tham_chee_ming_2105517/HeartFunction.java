package com.example.p5_tham_chee_ming_2105517;

import android.app.Activity;
import android.widget.TextView;

public class HeartFunction {
    private int hearts;
    private TextView heartTV;

    public HeartFunction(Activity activity, TextView heartTV, int initialHearts) {
        this.hearts = initialHearts;
        this.heartTV = heartTV;
        updateHeartDisplay();
    }

    public void decreaseHeart() {
        hearts--;
        updateHeartDisplay();
    }

    public int getHearts() {
        return hearts;
    }

    private void updateHeartDisplay() {
        if (heartTV != null) {
            heartTV.setText(hearts + " ❤️");
        }
    }
}
