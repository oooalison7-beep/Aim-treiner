package com.aim67.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        setContentView(new AimGame());
    }

    class AimGame extends View {

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random random = new Random();
        Handler handler = new Handler();

        float aimX = 300;
        float aimY = 300;

        float targetX = 500;
        float targetY = 300;
        float targetRadius = 32;

        int score = 0;
        int hits = 0;
        int shots = 0;
        int timeLeft = 30;

        boolean started = false;
        boolean finished = false;

        long endTime;

        final int blue = Color.rgb(0, 160, 255);
        final int dark = Color.rgb(10, 12, 20);

        Runnable timer = new Runnable() {
            @Override
            public void run() {
                if (!started || finished) return;

                timeLeft = (int) Math.max(
                        0, (endTime - System.currentTimeMillis() + 999) / 1000
                );

                if (timeLeft <= 0) {
                    finished = true;
                } else {
                    handler.postDelayed(this, 100);
                }

                invalidate();
            }
        };

        public AimGame() {
            super(MainActivity.this);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        void text(Canvas c, String s, float x, float y,
                  float size, int color) {
            p.setColor(color);
            p.setTextSize(size);
            p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            p.setTextAlign(Paint.Align.LEFT);
            c.drawText(s, x, y, p);
        }

        void centerText(Canvas c, String s, float x, float y,
                        float size, int color) {
            p.setTextSize(size);
            p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            p.setTextAlign(Paint.Align.CENTER);
            p.setColor(color);
            c.drawText(s, x, y, p);
            p.setTextAlign(Paint.Align.LEFT);
        }

        void button(Canvas c, float x, float y, float w, float h,
                    String label) {
            p.setColor(blue);
            c.drawRoundRect(x, y, x + w, y + h, 16, 16, p);

            centerText(c, label, x + w / 2, y + h / 2 + 8,
                    21, Color.WHITE);
        }

        void newTarget() {
            float w = getWidth();
            float h = getHeight();

            targetX = 80 + random.nextFloat() * Math.max(1, w - 300);
            targetY = 100 + random.nextFloat() * Math.max(1, h - 200);
        }

        void startGame() {
            score = 0;
            hits = 0;
            shots = 0;
            timeLeft = 30;

            started = true;
            finished = false;

            endTime = System.currentTimeMillis() + 30000;

            aimX = getWidth() / 2f;
            aimY = getHeight() / 2f;

            newTarget();

            handler.removeCallbacks(timer);
            handler.post(timer);

            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float w = getWidth();
            float h = getHeight();

            canvas.drawColor(dark);

            p.setColor(Color.rgb(20, 25, 38));
            p.setStrokeWidth(1);

            for (int x = 0; x < w; x += 40) {
                canvas.drawLine(x, 0, x, h, p);
            }

            for (int y = 0; y < h; y += 40) {
                canvas.drawLine(0, y, w, y, p);
            }

            text(canvas, "AIM 67", 25, 42, 27, blue);

            if (!started) {
                centerText(canvas, "AIM 67", w / 2, h / 2 - 65,
                        48, blue);

                centerText(canvas, "TREINO DE MIRA", w / 2, h / 2 - 25,
                        19, Color.WHITE);

                button(canvas, w / 2 - 100, h / 2 + 5,
                        200, 55, "JOGAR");

                centerText(canvas, "30 SEGUNDOS", w / 2, h / 2 + 95,
                        16, Color.LTGRAY);

                return;
            }

            text(canvas, "PONTOS: " + score, 25, 78, 19, Color.WHITE);
            text(canvas, "ACERTOS: " + hits, 25, 108, 16, blue);
            text(canvas, "TIROS: " + shots, 25, 134, 16, Color.LTGRAY);

            float accuracy = shots == 0 ? 0 : hits * 100f / shots;

            text(canvas, String.format(java.util.Locale.US,
                    "PRECISAO: %.0f%%", accuracy),
                    25, 160, 16, Color.LTGRAY);

            centerText(canvas, timeLeft + "s", w / 2, 45,
                    29, timeLeft <= 5 ? Color.RED : blue);

            if (finished) {
                p.setColor(Color.argb(220, 5, 8, 16));
                canvas.drawRect(0, 0, w, h, p);

                centerText(canvas, "FIM DE JOGO", w / 2, h / 2 - 65,
                        36, blue);

                centerText(canvas, "PONTOS: " + score,
                        w / 2, h / 2 - 20, 23, Color.WHITE);

                centerText(canvas, "ACERTOS: " + hits + " / " + shots,
                        w / 2, h / 2 + 15, 18, Color.LTGRAY);

                centerText(canvas, String.format(
                        java.util.Locale.US, "PRECISAO: %.1f%%", accuracy),
                        w / 2, h / 2 + 45, 18, Color.LTGRAY);

                button(canvas, w / 2 - 100, h / 2 + 65,
                        200, 55, "JOGAR NOVAMENTE");

                return;
            }

            // Alvo
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(255, 55, 75));
            canvas.drawCircle(targetX, targetY, targetRadius, p);

            p.setColor(Color.WHITE);
            canvas.drawCircle(targetX, targetY, targetRadius * 0.65f, p);

            p.setColor(Color.rgb(255, 55, 75));
            canvas.drawCircle(targetX, targetY, targetRadius * 0.30f, p);

            // Mira
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2.5f);
            p.setColor(blue);

            canvas.drawCircle(aimX, aimY, 15, p);
            canvas.drawLine(aimX - 23, aimY,
                    aimX - 5, aimY, p);
            canvas.drawLine(aimX + 5, aimY,
                    aimX + 23, aimY, p);
            canvas.drawLine(aimX, aimY - 23,
                    aimX, aimY - 5, p);
            canvas.drawLine(aimX, aimY + 5,
                    aimX, aimY + 23, p);

            p.setStyle(Paint.Style.FILL);

            // Botao de tiro
            button(canvas, w - 145, h - 95,
                    120, 65, "ATIRAR");
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                float w = getWidth();
                float h = getHeight();

                if (!started) {
                    startGame();
                    return true;
                }

                if (finished) {
                    if (x >= w / 2 - 130 && x <= w / 2 + 130
                            && y >= h / 2 + 45 && y <= h / 2 + 140) {
                        startGame();
                    }
                    return true;
                }

                // Botao de tiro
                if (x >= w - 160 && y >= h - 115) {
                    shots++;

                    float dx = xTargetDistance();
                    float dy = yTargetDistance();

                    if (Math.sqrt(dx * dx + dy * dy) <= targetRadius) {
                        hits++;
                        score += 100;
                        newTarget();
                    }

                    invalidate();
                    return true;
                }

                aimX = x;
                aimY = y;
                invalidate();
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE
                    && started && !finished) {
                aimX = x;
                aimY = y;
                invalidate();
                return true;
            }

            return true;
        }

        float xTargetDistance() {
            return aimX - targetX;
        }

        float yTargetDistance() {
            return aimY - targetY;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
