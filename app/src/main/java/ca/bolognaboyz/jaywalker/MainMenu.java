package ca.bolognaboyz.jaywalker;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class MainMenu extends Activity {

    MediaPlayer menuSounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        menuSounds = MediaPlayer.create(this, R.raw.ambientsound);
        menuSounds.start();
    }

    public void playGame(android.view.View button){
        Intent intent = new Intent(this,PlayActivity.class);
        startActivity(intent);
        menuSounds.release();
    }

    public void instructions(android.view.View button){
        Intent intent = new Intent(this,InstructionsActivity.class);
        startActivity(intent);
    }
}
