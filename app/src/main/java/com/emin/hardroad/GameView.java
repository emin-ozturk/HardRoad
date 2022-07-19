package com.emin.hardroad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    Thread thread;
    Canvas canvas;
    Paint paint;
    Activity activity;
    int screenX, screenY;
    int ballSize; //Topun boyutu
    int counter; //Duvarların oluşması için geçen zaman
    int counterTriangle;
    int leftWallEnd, rightWallStart; //Sol duvarın bitim koordinatı, sağ duvarın başlama koordinatı
    int speed; //Duvarların kayma hızı
    int areaWidth; //Ortada kalan alanın uzunluğu
    int space; //Duvarlar arasındaki boşluk
    int width; //Duvarların uzunluğu
    int height; //Duvarların genişkiği
    int score;
    int scroll = 0;
    float ballX, ballY; //Topun x ve y koordinatları
    float leftEdge, rightEdge;
    float tempX, tempY, tempW;
    float lx, ly, lw, lh, rx, ry, rw, rh;
    int direction = 1;
    int GAMEMODE;
    int MODEOPEN, MODEGAMEBEFORE, MODESTART, MODECLOSE;
    boolean isPlaying; //Oyun ekranında olup olmasığını belirler
    boolean isLeft; //Topun gidiş yönün belirler
    List<Wall> leftWall, rightWall; //Duvarların koordinatlarını tutar
    Random random;
    Path path;
    Typeface font;
    Vibrator vibrator;
    Database db;
    MediaPlayer soundScore, soundGameOver;

    //Topun çevresinin koordinatları(üçgenin içerisindeki top)
    List<BallCoordinate> ballBottomRight = new ArrayList<>();
    List<BallCoordinate> ballBottomLeft = new ArrayList<>();
    List<BallCoordinate> ballTopRight = new ArrayList<>();
    List<BallCoordinate> ballTopLeft = new ArrayList<>();

    //Duvarların koordinatları skor sayımı için geçişi olarak tutululur
    List<Wall> tempLeftWall = new ArrayList<>();

    //Arkada bırakılan iz
    List<Rhombus> littleRhombus = new ArrayList<>();

    private void init() {
        MODEOPEN = 1;
        MODEGAMEBEFORE = 2;
        MODESTART = 3;
        MODECLOSE = 4;
        GAMEMODE = MODEOPEN;
        paint = new Paint();
        random = new Random();
        path = new Path();
        speed = (int) (screenY * 0.00365);
        leftWallEnd = (int) (screenX / 2f);
        rightWallStart = (int) (screenX / 2f);
        leftEdge = (int) (screenX / 6f * 1.75f);
        rightEdge = (int) (screenX / 6f * 4.25f);
        areaWidth = (int) (screenX / 6f * 2.5f);
        space = (int) (areaWidth * 0.463f);
        ballSize = (int) (areaWidth * 0.065f);
        height = (int) (ballSize * 1.714f);
        ballX = screenX / 2f;
        ballY = screenY / 11f * 8f;
        leftWall = new ArrayList<>();
        rightWall = new ArrayList<>();
        font = Typeface.createFromAsset(getContext().getAssets(), "teko_regular.ttf");
        counter = 0;
        counterTriangle = 0;
        score = 0;
        isLeft = false;
        path.reset();
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        db = new Database(activity);
        soundScore = MediaPlayer.create(activity, R.raw.score);
        soundGameOver = MediaPlayer.create(activity, R.raw.gameover);
    }

    public GameView(Context context, Activity activity, int screenX, int screenY) {
        super(context);
        this.activity = activity;
        this.screenX = screenX;
        this.screenY = screenY;
        init();
    }

    @Override
    public void run() {
        while (isPlaying) {
            draw();
            update();
            sleep();
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            canvas = getHolder().lockCanvas();
            canvas.drawColor(activity.getResources().getColor(R.color.orange));
            drawScore();
            drawLittleRhombus();
            drawTriangle();
            drawWall();
            drawEdge();
            if (GAMEMODE == MODEGAMEBEFORE) {
                drawWritingPlay();
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawScore() {
        paint.setColor(activity.getResources().getColor(R.color.white));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(areaWidth * 0.7f);
        paint.setTypeface(font);
        canvas.drawText(String.valueOf(score), screenX / 2f, (screenY / 10f * 2f) - ((paint.descent() + paint.ascent()) / 2), paint);
    }

    private void drawLittleRhombus() {
        int size = littleRhombus.size() - 1;
        for (int i = 0; i < littleRhombus.size(); i++) {
            int x = (int) littleRhombus.get(i).getX();
            int y = (int) littleRhombus.get(i).getY();
            int w = (int) littleRhombus.get(i).getW();
            path = new Path();
            paint.setColor(Color.argb(Math.max((255 - ((size - i) * 50)), 0), 255, 255, 255));
            path.moveTo(x, y + w); // Top
            path.lineTo(x - w, y); // Left
            path.lineTo(x, y - w); // Bottom
            path.lineTo(x + w, y); // Right
            path.lineTo(x, y + w); // Back to Top
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawTriangle() {
        int x = (int) ballX;
        int y = (int) ballY;
        int w = ballSize;

        paint.setColor(activity.getResources().getColor(R.color.white));
        path.moveTo(x, y - w); // Top
        path.lineTo(x - w, y + w); // Bottom left
        path.lineTo(x + w, y + w); // Bottom right
        path.lineTo(x, y - w); // Back to Top
        path.close();
        canvas.drawPath(path, paint);

//        Top
//        paint.setColor(Color.BLACK);
//        canvas.drawCircle(ballX, ballY + ballSize / 3f, ballSize / 1.5f, paint);
    }

    private void drawWall() {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < leftWall.size(); i++) {
            canvas.drawRect(
                    leftWall.get(i).getX(),
                    leftWall.get(i).getY(),
                    leftWall.get(i).getX() + leftWall.get(i).getWidth(),
                    leftWall.get(i).getY() + leftWall.get(i).getHeight(),
                    paint);

            canvas.drawRect(
                    rightWall.get(i).getX(),
                    rightWall.get(i).getY(),
                    rightWall.get(i).getX() + rightWall.get(i).getWidth(),
                    rightWall.get(i).getY() + rightWall.get(i).getHeight(),
                    paint);
        }
    }

    private void drawEdge() {
        paint.setColor(activity.getResources().getColor(R.color.white));
        canvas.drawRect(0, 0, leftWallEnd, canvas.getHeight(), paint);
        canvas.drawRect(rightWallStart, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }

    private void drawWritingPlay() {
        paint.setColor(activity.getResources().getColor(R.color.white));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(areaWidth * 0.15f);
        paint.setTypeface(font);
        canvas.drawText(db.getLanguage().equals("en") ? "Tap to play" : "Başlamak için dokun",
                screenX / 2f, (screenY / 10f * 9f) - ((paint.descent() + paint.ascent()) / 2), paint);
    }

    private void update() {
        if (GAMEMODE == MODEOPEN) {
            updateOpen();
            return;
        } else if (GAMEMODE == MODECLOSE) {
            updateClose();
            return;
        } else if (GAMEMODE == MODEGAMEBEFORE) {
            return;
        }

        if (isLeft) {
            ballX -= screenX * 0.0092;
        } else {
            ballX += screenX * 0.0092;
        }

        updateWall();
        createWall();
        createTriangle();

        //Ekrandan çıkan duvarı sil
        if (leftWall.get(0).getY() > screenY && rightWall.get(0).getY() > screenY) {
            deleteWall();
        }

        //Oyun bitti mi?
        if (isGameOver()) {
            GAMEMODE = MODECLOSE;
            if (db.getVibration() == 1) {
                vibrator.vibrate(150);
//                soundScore.start();
            }
        }

        isScore();
        updateLittleRhombus();
    }

    private void updateOpen() {
        leftWallEnd -= 10;
        rightWallStart += 10;
        if (leftWallEnd <= leftEdge && rightWallStart >= rightEdge) {
            GAMEMODE = MODEGAMEBEFORE;
        }
    }

    private void updateClose() {
        leftWallEnd += 10;
        rightWallStart -= 10;
        if (leftWallEnd >= (int) (screenX / 2f) + 10 && rightWallStart <= (int) (screenX / 2f) - 10) {
            Intent intent = new Intent(activity, EndGameActivity.class);
            intent.putExtra("score", score);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            activity.finish();
        }
    }

    private void updateWall() {
        scroll++;
        for (int i = 0; i < leftWall.size(); i++) {
            tempY = leftWall.get(i).getY();
            tempY += speed;
            tempW = leftWall.get(i).getWidth();
            if (leftWall.get(i).getIsScroll()) {
                if (scroll <= 100) {
                    tempW -= leftWall.get(i).getDirection() * 0.7;
                } else if (scroll <= 200) {
                    tempW += leftWall.get(i).getDirection() * 0.7;
                } else {
                    scroll = 0;
                }
            }
            Wall tempTopWall = new Wall(
                    leftWall.get(i).getX(),
                    tempY,
                    tempW,
                    leftWall.get(i).getHeight(),
                    leftWall.get(i).getIsScroll(),
                    leftWall.get(i).getDirection());
            leftWall.set(i, tempTopWall);

            tempY = rightWall.get(i).getY();
            tempY += speed;
            tempX = rightWall.get(i).getX();
            tempW = rightWall.get(i).getWidth();
            if (rightWall.get(i).getIsScroll()) {
                if (scroll <= 100) {
                    tempX -= rightWall.get(i).getDirection() * 0.7;
                    tempW += rightWall.get(i).getDirection() * 0.7;
                } else if (scroll <= 200) {
                    tempX += rightWall.get(i).getDirection() * 0.7;
                    tempW -= rightWall.get(i).getDirection() * 0.7;
                } else {
                    scroll = 0;
                }
            }
            Wall tempBottomWall = new Wall(
                    tempX,
                    tempY,
                    tempW,
                    rightWall.get(i).getHeight(),
                    rightWall.get(i).getIsScroll(),
                    rightWall.get(i).getDirection());
            rightWall.set(i, tempBottomWall);
        }

        for (int i = 0; i < tempLeftWall.size(); i++) {
            tempY = tempLeftWall.get(i).getY();
            tempY += speed;
            Wall tempWall = new Wall(
                    rightWall.get(i).getX(),
                    tempY,
                    tempW,
                    rightWall.get(i).getHeight(),
                    rightWall.get(i).getIsScroll(),
                    rightWall.get(i).getDirection());
            tempLeftWall.set(i, tempWall);
        }
    }

    private void updateLittleRhombus() {
        for (int i = 0; i < littleRhombus.size(); i++) {
            tempY = littleRhombus.get(i).getY();
            tempY += speed * 1.4;
            Rhombus tempRhombus = new Rhombus(
                    littleRhombus.get(i).getX(),
                    tempY,
                    littleRhombus.get(i).getW());
            littleRhombus.set(i, tempRhombus);
        }

        if (littleRhombus.size() > 0) {
            if (littleRhombus.get(0).getY() > screenY) {
                littleRhombus.remove(0);
            }
        }
    }

    private void isScore() {
        float top = tempLeftWall.get(0).getY();
        if (ballY + ballSize < top) {
            score++;
            tempLeftWall.remove(0);
            if (db.getVibration() == 1) {
                vibrator.vibrate(50);
//                soundGameOver.start();
            }
        }
    }

    private void deleteWall() {
        leftWall.remove(0);
        rightWall.remove(0);
    }

    private boolean isGameOver() {
        if (ballX - ballSize <= leftWallEnd || ballX + ballSize >= rightWallStart) {
            return true;
        }

        ballCoordinate();
        float bottom, top, left, right;
        for (int i = 0; i < ballTopLeft.size(); i++) {
            for (int j = 0; j < leftWall.size(); j++) {
                bottom = leftWall.get(j).getY() + leftWall.get(j).getHeight();
                top = leftWall.get(j).getY();
                left = leftWall.get(j).getX() + leftWall.get(j).getWidth();
                right = rightWall.get(j).getX();
                if (ballTopLeft.get(i).getY() <= bottom && ballTopLeft.get(i).getY() >= top && ballTopLeft.get(i).getX() <= left) {
                    return true;
                } else if (ballTopRight.get(i).getY() <= bottom && ballTopRight.get(i).getY() >= top && ballTopRight.get(i).getX() >= right) {
                    return true;
                } else if (ballBottomLeft.get(i).getY() <= bottom && ballBottomLeft.get(i).getY() >= top && ballBottomLeft.get(i).getX() <= left) {
                    return true;
                } else if (ballBottomRight.get(i).getY() <= bottom && ballBottomRight.get(i).getY() >= top && ballBottomRight.get(i).getX() >= right) {
                    return true;
                } else if (ballX <= left && ballY - ballSize <= bottom && ballY - ballSize >= top) {
                    return true;
                } else if (ballX >= right && ballY - ballSize <= bottom && ballY - ballSize >= top) {
                    return true;
                } else if (ballX - ballSize <= left && ballY + ballSize <= bottom && ballY + ballSize >= top) {
                    return true;
                } else if (ballX + ballSize >= right && ballY + ballSize <= bottom && ballY + ballSize >= top) {
                    return true;
                }
            }
        }
        return false;
    }

    private void ballCoordinate() {
        float x, y;
        ballBottomRight.clear();
        ballBottomLeft.clear();
        ballTopLeft.clear();
        ballTopRight.clear();
        for (int i = 0; i <= 90; i++) {
            //Topun sağ alt koordinatları
            x = (float) (ballX + Math.cos(Math.toRadians(i)) * ballSize / 1.5);
            y = (float) (ballY + ballSize / 3 + Math.sin(Math.toRadians(i)) * ballSize / 1.5);
            ballBottomRight.add(new BallCoordinate(x, y));

            //Topun sol alt koordinatları
            x = (float) (ballX + Math.cos(Math.toRadians(i + 90)) * ballSize / 2);
            y = (float) (ballY + ballSize / 3 + Math.sin(Math.toRadians(i + 90)) * ballSize / 1.5);
            ballBottomLeft.add(new BallCoordinate(x, y));

            //Topun sol üst koordinatları
            x = (float) (ballX + Math.cos(Math.toRadians(i + 180)) * ballSize / 2);
            y = (float) (ballY + ballSize / 3 + Math.sin(Math.toRadians(i + 180)) * ballSize / 1.5);
            ballTopLeft.add(new BallCoordinate(x, y));

            //Topun sağ üst koordinatları
            x = (float) (ballX + Math.cos(Math.toRadians(i + 270)) * ballSize / 2);
            y = (float) (ballY + ballSize / 3 + Math.sin(Math.toRadians(i + 270)) * ballSize / 1.5);
            ballTopRight.add(new BallCoordinate(x, y));
        }
    }

    private void createWall() {
        if (counter <= 0) {
            int randomScroll = random.nextInt(2);
            boolean scroll = randomScroll != 0;
            direction *= -1;
            width = Math.max(random.nextInt(areaWidth - space - 50), 50);
            lx = leftWallEnd;
            ly = -height;
            lw = width;
            lh = height;

            rx = leftWallEnd + width + space;
            ry = -height;
            rw = rightWallStart - (leftWallEnd + width + space);
            rh = height;

            leftWall.add(new Wall(lx, ly, lw, lh, scroll, direction));
            rightWall.add(new Wall(rx, ry, rw, rh, scroll, direction));
            tempLeftWall.add(new Wall(lx, ly, lw, lh, scroll, direction));
            counter = 70 * speed;
        }
        counter -= speed;
    }

    private void createTriangle() {
        if (counterTriangle <= 0) {
            littleRhombus.add(new Rhombus(ballX, ballY, ballSize / 3f));
            counterTriangle = 12 * speed;
        }
        counterTriangle -= speed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (GAMEMODE == MODEGAMEBEFORE) {
            GAMEMODE = MODESTART;
        } else if (GAMEMODE == MODESTART) {
            isLeft = !isLeft;
        }
        return super.onTouchEvent(event);
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
