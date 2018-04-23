//File Name: GameOver
//Authors: Christopher Farfan Centeno, Marco Giardina, Thong Pham, Esaac Ahn
//Student#: 101067074                , 101025550     , 101035471 , 100836038
//Date Last Modified: April 22nd, 2018
//Description: Game Over screen for the game, can either click play again to start the game over or
//             a user can click on main menu to return to the main menu
package ca.bolognaboyz.jaywalker;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends Activity {


    TextView gameOverTextView;
    MediaPlayer gameOverSounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        gameOverTextView = findViewById(R.id.gameOverMsg);
        int playerScore = getIntent().getIntExtra("playerScore", 0);

        gameOverTextView.setText("Game Over Man, Game Over! \n\n You had a score of " + playerScore +
        ". \n\n What would you like to do?");

        gameOverSounds = MediaPlayer.create(this,R.raw.gameover);
        gameOverSounds.start();

    }

    public void mainMenuClick(View view){
        Intent intent = new Intent(this,MainMenu.class);
        startActivity(intent);
        gameOverSounds.release();

    }

    public void playAgainClick(View view){
        Intent intent = new Intent(this,PlayActivity.class);
        startActivity(intent);
        gameOverSounds.release();
    }
}
