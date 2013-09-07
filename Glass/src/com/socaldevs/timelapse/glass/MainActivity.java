package com.socaldevs.timelapse.glass;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	private static final int DELAY = 5000; // 5 seconds

	private static String TAG = "test";

	private Camera mCamera;
	private CameraPreview mPreview;

	private WakeLock wakeLock = null;

	// private File dir = new File("/sdcard/timelapse");
	// private DecimalFormat formatter = new DecimalFormat("00000");
	// private static int picNum = 0;

	private LocationManager locationManager = null;
	private String locationProvider = null;
	
	private int picIndex = 0;

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i("status", "picture taken");
			Log.i("length", "" + data.length);
			Log.i("exposure", ""
					+ camera.getParameters().getExposureCompensation());

			// File out = new File(dir,
			// "lapse_1_img"+formatter.format(picNum)+".jpg");
			// File out = new File(dir, "single.jpg");
			// picNum++;
			// FileOutputStream fos;
			// try {
			// fos = new FileOutputStream(out);
			// fos.write(data);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

			Uploader uploader = new Uploader(picIndex);
			uploader.execute(data);
			picIndex++;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new Handler();

		// dir.mkdirs();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationProvider = locationManager.getProviders(true).get(0);

		// Log.i("autofocus support",
		// String.valueOf(getPackageManager().hasSystemFeature("android.hardware.camera.autofocus")));
	}

	@Override
	public void onResume() {
		super.onResume();
		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"Glass Timelapse");

		Camera.Parameters params = mCamera.getParameters();

		params.setPictureSize(1280, 720);

		List<String> focusModes = params.getSupportedFocusModes();

		for (String s : focusModes) {
			Log.i("supported focus mode", s);
		}
		Log.i("autofocus mode", params.getFocusMode());

		Log.i("min exposure compensation",
				"" + params.getMinExposureCompensation());
		Log.i("max exposure compensation",
				"" + params.getMaxExposureCompensation());

		params.setExposureCompensation(-30);

		params.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);

		mCamera.setParameters(params);

		Log.i("exposure compensation", ""
				+ mCamera.getParameters().getExposureCompensation());
		Log.i("scene mode", "" + mCamera.getParameters().getSceneMode());
	}

	@Override
	public void onPause() {
		super.onPause();
		releaseCamera();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			e.printStackTrace();
		}
		return c; // returns null if camera is unavailable
	}

	private Handler handler = null;
	private boolean running = false;

	private void start() {
		wakeLock.acquire();
		Log.i("status", "acquired wakelock");
		mPreview.setVisibility(View.INVISIBLE);
		running = true;
		handler.postDelayed(new Runnable() {

			public void run() {
				// do something
				takePicture();

				if (running)
					handler.postDelayed(this, DELAY);
			}
		}, 1000);
	}

	private void stop() {
		running = false;
		wakeLock.release();
		Log.i("status", "released wakelock");
	}

	private void takePicture() {
		if (mCamera == null) {
			Log.e("cam status", "camera is null");
			return;
		}
		Camera.Parameters params = mCamera.getParameters();
		Location loc = locationManager.getLastKnownLocation(locationProvider);
		params.setGpsLatitude(loc.getLatitude());
		params.setGpsLongitude(loc.getLongitude());
		mCamera.setParameters(params);
		mCamera.takePicture(null, null, mPicture);
		Log.i("status", "called takePicture");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_start:
			start();
			return true;
		case R.id.action_stop:
			stop();
			return true;
		case R.id.action_take_picture:
			takePicture();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handle the tap event from the touchpad.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// On Tap, we want to open the menus
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			openOptionsMenu();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** A basic Camera preview class */
	public class CameraPreview extends SurfaceView implements
			SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		private Camera mCamera;

		public CameraPreview(Context context, Camera camera) {
			super(context);
			mCamera = camera;

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e) {
				Log.d(TAG, "Error setting camera preview: " + e.getMessage());
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// empty. Take care of releasing the Camera preview in your
			// activity.
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			// set preview size and make any resize, rotate or
			// reformatting changes here

			// start preview with new settings
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
				Log.d(TAG, "Error starting camera preview: " + e.getMessage());
			}
		}
	}

}
