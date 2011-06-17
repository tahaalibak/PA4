package jbs2011.pa3b;

import android.app.Activity;
import android.os.Bundle;
import java.util.concurrent.TimeUnit;
import jbs2011.pa3.GameModel;
import jbs2011.pa3.Disk;
import jbs2011.pa3.Square;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.content.res.Configuration;
import android.util.Log;

/**
 * This game consists of the user flinging disks into the air trying to hit
 * a (red) target square without hitting static blocking element, initiallysquares. 
 * If it does hit a static object, it becomes static itself making it harder
 * to get to the target.  The user can grab a disk in flight and refling it
 * but cannot drag it around the screen. The level ends when the target is hit
 * and the game goes to the next level.
 * 
 * This game is a sketetal version of a simple game that was developed
 * as part of Programming Assignment 3 for the Brandeis University JBS
 * in Summer of 2011.
 */

public class GameActivity extends Activity implements Callback {
	/** Called when the activity is first created. */
	private SurfaceView surface;
	private SurfaceHolder holder;
	private GameModel model;
	private GameController controller;
	private GameLoop gameLoop;
	private Paint backgroundPaint;
	private Paint diskPaint, squarePaint, targetPaint;
	private static final String TAG="GA";

	/**
	 * When the activity starts we create a model, view, and controller for the game.
	 * The model and controller are separate classes
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setup a model for the game and initialize with the first level
		 model = new GameModel();

		 
        //  create the view which contqins a game_surface on which the game will be drawn
        createPaints();
		setContentView(R.layout.game);
		// create the drawing surface and register this class as the callback listener
		surface = (SurfaceView) findViewById(R.id.game_surface);
		holder = surface.getHolder();
		surface.getHolder().addCallback(this);

		// Next, create a controller for the game and set it up to listen for inputs
		controller  = new GameController(this, model);
		surface.setOnTouchListener(controller);
		
		// Finally, start the game loop!
		gameLoop = new GameLoop(this,model,controller);
		gameLoop.start();
		Log.d(TAG,"surface created");
		//model.createLevel(2);



	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	}


	/*
	 * The disk, square, and targets all have different colors
	 */
	private void createPaints(){
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.BLUE);

		diskPaint = new Paint();
		diskPaint.setColor(Color.BLACK);
		diskPaint.setAntiAlias(true);

		squarePaint = new Paint();
		squarePaint.setColor(Color.WHITE);
		squarePaint.setAntiAlias(true);

		targetPaint = new Paint();
		targetPaint.setColor(Color.RED);
		targetPaint.setAntiAlias(true);
	}

	@Override
	protected void onPause() {
		// model.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// model.onResume(this);
	}

	
	/**
	 * When the drawing surface size changes we need to tell the controller so it
	 * can adjust the mapping between the view and the model
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		controller.setSize(width, height);

	}

		
	/**
	 *  When the drawing surface is created we start up a game loop,
	 *  the game loop just draws the scene and updates the model in an infinite loop
	 *  running in a separate thread. We create the thread and start it up here ...
	 */
	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void draw() {

		Canvas c = null;
		try {
			c = holder.lockCanvas();

			if (c != null) {
				doDraw(c);
			
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}

	/*
	 * The drawing method simply draws the disks, squares, and targets
	 * after it paints the entire screen with the background color
	 */
	private void doDraw(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		controller.setSize(width, height);

		//Log.d("GA","w="+width+" h="+height);

		c.drawRect(0, 0, width, height, backgroundPaint);

		for (Disk d : model.disks) {
			c.drawCircle(d.x, height - d.y, d.r, diskPaint);
		}
		for (Square d : model.squares) {
			c.drawRect(d.x - d.w / 2, height - (d.y + d.w / 2), d.x + d.w / 2,
					height - (d.y - d.w / 2), squarePaint);
		}
		for (Square d : model.targets) {
			c.drawRect(d.x - d.w / 2, height - (d.y + d.w / 2), d.x + d.w / 2,
					height - (d.y - d.w / 2), targetPaint);
		}
	}

	/**
	 * When the surface is destroyed we stop the game loop
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
	}

	


}