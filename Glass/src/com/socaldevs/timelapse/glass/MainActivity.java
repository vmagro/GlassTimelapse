package com.socaldevs.timelapse.glass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private static final int DELAY = 5000; // 5 seconds

	private static String TAG = "test";

	private Camera mCamera;
	private CameraPreview mPreview;

	private WakeLock wakeLock = null;

	private File dir = new File("/sdcard/timelapse");
	private DecimalFormat formatter = new DecimalFormat("00000");
	private static int picNum = 0;

	File vidDir = null;
	private String eventId = null;

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (!running)
				return;
			Log.i("status", "picture taken");
			Log.i("length", "" + data.length);

			File out = new File(vidDir, "img" + formatter.format(picNum)
					+ ".jpg");
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(out);
				fos.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (eventId == null)
				return;

			Uploader uploader = new Uploader(MainActivity.this, eventId, picNum);
			uploader.execute(data);

			picNum++;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new Handler();

		File ffmpeg = new File(this.getFilesDir(), "ffmpeg");

		if (!ffmpeg.exists()) {
			Log.i("ffmpeg", "does not exist, installing");
			try {
				FileOutputStream fos = this.openFileOutput("ffmpeg",
						Context.MODE_WORLD_WRITEABLE);
				InputStream in = getResources().openRawResource(R.raw.ffmpeg);
				while (in.available() > 0) {
					byte[] buffer = new byte[in.available()];
					int read = in.read(buffer);
					fos.write(buffer, 0, read);
				}
				fos.close();
				ffmpeg.setExecutable(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			Log.i("ffmpeg", "exists, path = " + ffmpeg.getAbsolutePath());
		}

		try {
			Process p = Runtime.getRuntime().exec(
					ffmpeg.getAbsolutePath() + " --help");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				Log.i("output", line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		dir.mkdirs();

		Log.i("status", "started");

		// Log.i("autofocus support",
		// String.valueOf(getPackageManager().hasSystemFeature("android.hardware.camera.autofocus")));
	}

	@Override
	public void onResume() {
		super.onResume();
		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		if (preview != null)
			preview.addView(mPreview);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"Glass Timelapse");

		openOptionsMenu();
	}

	@Override
	public void onPause() {
		super.onPause();
		mCamera.stopPreview();
		mCamera.unlock();
		mCamera.release();
		mCamera = null;
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
		if (running) {
			Log.i("status", "already running with event id = " + eventId);
			return;
		}
		wakeLock.acquire();
		Log.i("status", "acquired wakelock");
		running = true;

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String event = null;
				String id = Secure.getString(
						MainActivity.this.getContentResolver(),
						Secure.ANDROID_ID);

				try {
					HttpClient cli = new DefaultHttpClient();
					HttpPost post = new HttpPost(Constants.EVENT_URL
							+ "?mode=new&glassId=" + id);
					Log.i("event url", Constants.EVENT_URL
							+ "?mode=new&glassId=" + id);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(cli.execute(post).getEntity()
									.getContent()));
					event = reader.readLine();
					Log.i("event id", "" + event);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				return event;
			}

			@Override
			protected void onPostExecute(String result) {
				MainActivity.this.eventId = result;
				vidDir = new File(dir, MainActivity.this.eventId);
				vidDir.mkdirs();

				handler.postDelayed(new Runnable() {

					public void run() {
						// do something
						takePicture();

						if (running)
							handler.postDelayed(this, DELAY);
					}
				}, 1000);
			}
		}.execute();
	}

	private void stop() {
		running = false;

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						Log.i("event", "canceling event " + eventId);
						String id = Secure.getString(
								MainActivity.this.getContentResolver(),
								Secure.ANDROID_ID);

						HttpClient cli = new DefaultHttpClient();
						HttpPost post = new HttpPost(Constants.EVENT_URL
								+ "?mode=end&glassId=" + id + "&eventId="
								+ eventId);

						try {
							cli.execute(post).getEntity().consumeContent();
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						try {
							wakeLock.release();
							Log.i("status", "released wakelock");
						} catch (Exception ex) {
							Log.e("wakelock",
									"error releasing wakelock, proceeding normally");
						}
						MainActivity.this.finish();
						return null;
					}

				}.execute();

				// make the video
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						int num = vidDir.listFiles().length;
						String framerate = null;
						if (num < 10)
							framerate = "1/5";
						if (num < 20)
							framerate = "1/2";
						if (num < 100)
							framerate = "1";
						if (num > 100)
							framerate = "3";

						String args = " -y -r "
								+ framerate
								+ " -pattern_type glob -i '*.png' -c:v libx264 -pix_fmt yuv420p vid"
								+ eventId + ".mp4";
						String command = new File(MainActivity.this
								.getFilesDir(), "ffmpeg").getAbsolutePath()
								+ " " + args;

						Log.i("command", command);
						try {
							Runtime.getRuntime().exec(command);
						} catch (IOException e) {
							e.printStackTrace();
						}

						return null;
					}
				}.execute();
			}

		}, 20000); // wait 20 seconds to make sure that the last image is
					// uploaded
	}

	private void takePicture() {
		if (mCamera == null) {
			Log.e("cam status", "camera is null");
			return;
		}
		mCamera.takePicture(null, null, mPicture);
		Log.i("status", "called takePicture");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_start:
			start();
			invalidateOptionsMenu();
			return true;
		case R.id.action_stop:
			stop();
			invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (running) {
			menu.getItem(0).setEnabled(false);
			menu.getItem(1).setEnabled(true);
		} else {
			menu.getItem(1).setEnabled(false);
			menu.getItem(0).setEnabled(true);
		}
		return true;
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

		public CameraPreview(Context context) {
			super(context);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			if (mCamera == null) {
				return;
			}
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