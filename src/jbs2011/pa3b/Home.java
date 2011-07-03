package jbs2011.pa3b;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Home extends Activity implements OnClickListener{

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
      	setContentView(R.layout.home);
      
		//Finds the play view
		View play = findViewById(R.id.launch_game);
		play.setOnClickListener(this);

	}
	
	public void onClick(View v) {
		switch(v.getId()) {

		case R.id.launch_game:
			Intent i = new Intent(this, GameActivity.class);
			startActivity(i);
			break;
		}
	}
}
