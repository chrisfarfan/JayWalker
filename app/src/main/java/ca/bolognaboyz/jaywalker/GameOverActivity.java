package ca.bolognaboyz.jaywalker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends Activity {


    TextView gameOverTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        gameOverTextView = findViewById(R.id.gameOverMsg);
        int playerScore = getIntent().getIntExtra("playerScore", 0);

        gameOverTextView.setText("Game Over Man, Game Over! \n\n You had a score of " + playerScore +
        ". \n\n What would you like to do?");
    }

    public void mainMenuClick(View view){
        Intent intent = new Intent(this,MainMenu.class);
        startActivity(intent);
    }

    public void playAgainClick(View view){
        Intent intent = new Intent(this,PlayActivity.class);
        startActivity(intent);
    }
}
