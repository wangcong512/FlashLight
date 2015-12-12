package com.example.flashlight;

import com.example.flashlight.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	private Button btnChange = null;
	private boolean m_bKaiGuan = false;
	private Camera camera;
	private int m_nBackCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
		
		btnChange = (Button)findViewById(R.id.dummy_button);
		btnChange.setOnClickListener(new MyButton());
		
		//camara
		
		camera = Camera.open();
        if (!m_bKaiGuan) {

            //lightBtn.setBackgroundResource(R.drawable.shou_on);
        	//camera = Camera.open();
            Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
            camera.setParameters(parameters);
            camera.startPreview();
            m_bKaiGuan = true;
            btnChange.setText("@string/button_open");
        }
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	
	
    class MyButton implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (!m_bKaiGuan) {

                //lightBtn.setBackgroundResource(R.drawable.shou_on);
            	//camera = Camera.open();
                Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
                camera.setParameters(parameters);
                camera.startPreview();
                m_bKaiGuan = true;
                btnChange.setText("@string/button_open");
            } else {
                // addContentView(adView, new ViewGroup.LayoutParams(-1, -2));
               // lightBtn.setBackgroundResource(R.drawable.shou_off);
            	Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
                camera.setParameters(parameters);
                camera.stopPreview();
                m_bKaiGuan = false;
                btnChange.setText("@string/button_close");
                //camera.release();
            }

            // AdView构造函数可以接收三个参数：context(上下文), AdSize类型(广告样式),
            // 广告位ID(非高级广告位填null即可)
            // AdView adView = new AdView(this, AdSize.Square, null);
            // AdView adView = new AdView(this, AdSize.Banner, null);

            // 设置adView为当前Activity的View

        }
    }

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            m_nBackCount++;
            switch (m_nBackCount) {
            case 1:
           
                break;
            case 2:
            	m_nBackCount = 0;
                Myback();
                break;
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void Myback() { // 关闭程序
        if (!m_bKaiGuan) {// 开关关闭时
            FullscreenActivity.this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());// 关闭进程
        } else if (m_bKaiGuan) {// 开关打开时
            camera.release();
            FullscreenActivity.this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());// 关闭进程
            m_bKaiGuan = true;// 避免，打开开关后退出程序，再次进入不打开开关直接退出时，程序错误
        }
    }
}
