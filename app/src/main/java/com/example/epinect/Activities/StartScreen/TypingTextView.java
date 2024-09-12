package com.example.epinect.Activities.StartScreen;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class TypingTextView extends AppCompatTextView {
    private CharSequence text;
    private int index;
    private long delay = 50; // Typing speed

    private Handler handler = new Handler();

    public TypingTextView(Context context) {
        super(context);
    }

    public TypingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTextWithAnimation(CharSequence text) {
        this.text = text;
        index = 0;
        handler.postDelayed(typeRunnable, delay);
    }

    private Runnable typeRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < text.length()) {
                setText(text.subSequence(0, ++index));
                handler.postDelayed(this, delay);
            }
        }
    };
}
