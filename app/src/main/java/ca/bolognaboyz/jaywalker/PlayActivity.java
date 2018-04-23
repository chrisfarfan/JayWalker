//File Name: PlayActivity
//Authors: Christopher Farfan Centeno, Marco Giardina, Thong Pham, Esaac Ahn
//Student Numbers: 
//Date Last Modified: April 22nd, 2018
//Description: A simple Frogger inspired game where the player must dodge cars to get to the other
//             side. Players have 3 lives and must attempt to get the highest score possible
//             by crossing safetly as well as picking up money.

package ca.bolognaboyz.jaywalker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PlayActivity extends Activity {

    GameView gv;
    Canvas gameCanvas;
    Paint drawPaint = new Paint();

    Bitmap player, popo, taxi, background, money;

    // Sound
    // Initialize sound variables
    private SoundPool soundPool;
    int playerSplat = -1;
    int jumpSound = -1;
    int successSound = -1;
    MediaPlayer bgm;

    // For getting display details like the number of pixels
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;
    int bottomHalfScreen;
    int lastQuarterScreen;
    int endOfLeft;
    int startOfRight;

    //Starting position of player
    float playerX = 0;
    float playerY = 1020;
    int playerCol = 0;

    //Starting position of first column of cars
    float popoX = 150;
    float popoY = 2960;

    //Starting positions of second columns of cars
    float taxiX = 550;
   // float initialTaxiY[] = {0, 1300 , 1200};
    float taxiY[] ={2200, 900, -300};

    int taxiLapCounter = 0;
    int popoLapCounter = 0;


    //Where the player tapped
    float touchX;
    float touchY;

    //Storing player current pos
    //float currentX;
    //float currentY;

    //Speeds
    int taxiSpeed = 20;
    int popoSpeed = 30;

    //How far to move
    int playerVMovement = 340;
    int playerHMovement = 400;

    //Stats
    int fps;
    long lastFrameTime;

    int score = 0;
    int lives = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        bottomHalfScreen = screenHeight / 2;
        lastQuarterScreen = bottomHalfScreen + (bottomHalfScreen / 2);
        endOfLeft =  (screenWidth / 2) - (screenWidth / 5);
        startOfRight = (screenWidth / 2) + (screenWidth / 5);


        // Sound code
        soundPool = (new SoundPool.Builder()).setMaxStreams(2).build();
        playerSplat = soundPool.load(this, R.raw.playersplat, 1);
        jumpSound = soundPool.load(this, R.raw.jumpsound, 1);
        successSound = soundPool.load(this, R.raw.success, 1);
        bgm = MediaPlayer.create(this,R.raw.bgm);
        bgm.setLooping(true);
        bgm.start();



        gv = new GameView(this);
        setContentView(gv);

        player = BitmapFactory.decodeResource(getResources(), R.drawable.testplayer);
        popo = BitmapFactory.decodeResource(getResources(), R.drawable.police);
        taxi = BitmapFactory.decodeResource(getResources(), R.drawable.taxi);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.streetimg);
        money = BitmapFactory.decodeResource(getResources(), R.drawable.audi);

    }

    @Override
    protected void onStop() {
        super.onStop();

        while (true) {
            gv.pause();
            break;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gv.pause();
        bgm.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gv.resume();
    }

    public class GameView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        boolean threadOK = true;

        public GameView(Context context){
            super(context);
            ourHolder = getHolder();
        }

        @Override
        public void run(){
            while (threadOK) {
                updatePositions();
                detectCollisions();
                myDraw();
                controlFPS();
            }
        }

        public void pause(){
            threadOK = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {
            }
        }

        public void resume(){
            threadOK = true;
            ourThread = new Thread(this);
            ourThread.start();
        }



        //Function that figures out where the user touched the screen and then moves the player
        //in that direction as well as playing a sound
        public boolean onTouchEvent(MotionEvent event){

            touchX = event.getRawX();
            touchY = event.getRawY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (touchY > bottomHalfScreen) {//Player moving left
                    if (touchX < endOfLeft) {
                        if (playerX > 0) {
                            playerX = playerX - playerHMovement;
                            playerCol--;
                            soundPool.play(jumpSound,1.f,1.f,1, 0,1.f);
                        }
                    } else if (touchX > startOfRight) { //Right
                        if (playerX < screenWidth - 240) {
                            playerX = playerX + playerHMovement;
                            playerCol++;
                            soundPool.play(jumpSound,1.f,1.f,1, 0,1.f);
                            detectCollisions();
                        }
                    } else if (touchY > lastQuarterScreen && playerY < screenHeight - 400) { //Down
                        playerY = playerY + playerVMovement;
                        soundPool.play(jumpSound,1.f,1.f,1, 0,1.f);
                    } else if (touchY < lastQuarterScreen && playerY > 0) { //Up
                        playerY = playerY - playerVMovement;
                        soundPool.play(jumpSound,1.f,1.f,1, 0,1.f);
                    }
                }
            }


            return true;
        }
        //Function that deals with drawing all the graphics on the screen
        protected void myDraw(){

            if (ourHolder.getSurface().isValid()){
                gameCanvas = ourHolder.lockCanvas();
                // Log.w("test", "testing");

                //Draw background
                drawPaint.setAlpha(255);
                background = Bitmap.createScaledBitmap(background, gameCanvas.getWidth(), gameCanvas.getHeight(), true);
                gameCanvas.drawBitmap(background,0, 0, drawPaint);



                gameCanvas.drawBitmap(player, playerX, playerY, drawPaint);
                gameCanvas.drawBitmap(popo, popoX, popoY, drawPaint);
                for (int i = 0; i < 3; i++){
                    gameCanvas.drawBitmap(taxi, taxiX, taxiY[i], drawPaint);
                }

                drawPaint.setColor(Color.argb(255, 0, 0, 0));
                drawPaint.setTextSize(85);
                gameCanvas.drawText("Score: " + score +
                                "                                  Lives: " + lives,
                        20, 100, drawPaint);

                ourHolder.unlockCanvasAndPost(gameCanvas);
            }
        }

        //Function that controls the FPS of the game
        private void controlFPS() {
            long timeThisFrame = (System.
                    currentTimeMillis() - lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }
            if (timeToSleep > 0) {
                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    lastFrameTime = System.currentTimeMillis();
                }
            }
        }
        //Function that controls the position of the cars on screen
        private void updatePositions(){
            if (popoY > - 500){
                popoY -= popoSpeed;
            } else {
                popoY = 2960;
                popoLapCounter++;
                if (popoLapCounter % 7 == 0){
                    popoSpeed = 80;
                }else if (popoLapCounter % 3 == 0)
                    popoSpeed = 50;
                else
                    popoSpeed = 30;
            }

            for (int i = 0; i < 3; i++){
                if (taxiY[i] < 3000){
                    taxiY[i] += taxiSpeed;
                } else {
                    if (taxiLapCounter % 4 == 0){
                        taxiY[i] = -800;
                    } else if (taxiLapCounter % 3 == 0){
                        taxiY[i] = -1000;
                    } else{
                        taxiY[i] = -500;
                    }

                    if (i == 0)
                        taxiLapCounter++;
                }
            }

        }
        //Function that deals with player collision with cars and with reaching the end
        private void detectCollisions(){
            if (playerCol == 3){
                soundPool.play(successSound,1.f,1.f,1, 0,1.f);
                //add stuff for yellow dot detection
                score += 50;
                playerX = 0;
                playerCol = 0;


            } else{
                if (playerCol == 2) {
                    for (int i = 0; i < 3; i++){
                        if (playerY > taxiY[i] && playerY < taxiY[i] + taxi.getHeight() - 50 ||
                                playerY + player.getHeight()-200 >= taxiY[i] &&
                                        playerY + player.getHeight()-200 < taxiY[i] + taxi.getHeight() - 50){
                            playerDeath();
                        }
                    }
                } else if (playerCol == 1){
                    if (playerY > popoY && playerY < popoY + popo.getHeight() - 50 ||
                            playerY + player.getHeight() -200 >= popoY &&
                                    playerY + player.getHeight()-200 < popoY + popo.getHeight() - 50){
                        playerDeath();
                    }
                }
            }
        }
        //Function that handles what happens when a player hits a car
        private void playerDeath(){
            soundPool.play(playerSplat,1.f,1.f,1,0,1.f);
            playerX = 0;
            lives--;
            playerCol = 0;
            if (lives == 0){
                Intent intent = new Intent(PlayActivity.this,GameOverActivity.class);
                intent.putExtra("playerScore", score);
                startActivity(intent);
            }
        }
    }


}
