package ca.bolognaboyz.jaywalker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainMenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void playGame(android.view.View button){
        Intent intent = new Intent(this,PlayActivity.class);
        startActivity(intent);
    }

    public void instructions(android.view.View button){
        Intent intent = new Intent(this,InstructionsActivity.class);
        startActivity(intent);
    }
}
