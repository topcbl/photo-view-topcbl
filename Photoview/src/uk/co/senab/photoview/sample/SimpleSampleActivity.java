///*******************************************************************************
// * Copyright 2011, 2012 Chris Banes.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *******************************************************************************/
//package uk.co.senab.photoview.sample;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.Random;
//
//import uk.co.senab.photoview.PhotoView;
//import uk.co.senab.photoview.PhotoViewAttacher;
//import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
//import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.graphics.RectF;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ImageView.ScaleType;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.photoview.R;
//
//import eu.janmuller.android.simplecropimage.CropImage;
//
//public class SimpleSampleActivity extends Activity implements
//		View.OnClickListener {
//
//	static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
//	static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
//	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
//	private TextView mCurrMatrixTv;
//
//	private PhotoViewAttacher mAttacher;
//
//	private Toast mCurrentToast;
//
//	private Matrix mCurrentDisplayMatrix = null;
//	private PhotoView mImageView;
//	private File outFile;
//
//	private SeekBar mSeekBar;
//	private int curProgress = 45;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		mImageView = (PhotoView) findViewById(R.id.iv_photo);
//		mCurrMatrixTv = (TextView) findViewById(R.id.tv_current_matrix);
//
//		Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
//		mImageView.setImageDrawable(bitmap);
//
//		// The MAGIC happens here!
//		mAttacher = new PhotoViewAttacher(mImageView);
//
//		// Lets attach some listeners, not required though!
//		mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
//		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
//		mSeekBar = (SeekBar) findViewById(R.id.seekBar1);
//		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				mAttacher.setRotationBy(curProgress - progress);
//				mAttacher.addRotationDegrees(curProgress - progress);
//				curProgress = progress;
//				mAttacher.setScaleFix(1.f + (float) Math.abs(curProgress - 45) / 45f);
//			}
//		});
//		findViewById(R.id.btn_left).setOnClickListener(this);
//		findViewById(R.id.btn_right).setOnClickListener(this);
//		findViewById(R.id.button1).setOnClickListener(this);
//	}
//
//	public void onClick(View v) {
//		float degrees = mAttacher.getRotationDegrees();
//		curProgress = 45;
//		mSeekBar.setProgress(curProgress);
//		switch (v.getId()) {
//		case R.id.btn_left:
//			if (degrees % 90 == 0) {
//				mAttacher.setRotationBy(-90);
//				mAttacher.addRotationDegrees(-90);
//			} else {
//				while (!(degrees % 90 == 0)) {
//					degrees--;
//				}
//				mAttacher.setRotationTo(degrees);
//				mAttacher.setRotationDegrees(degrees);
//			}
//			break;
//		case R.id.btn_right:
//			if (degrees % 90 == 0) {
//				mAttacher.setRotationBy(90);
//				mAttacher.addRotationDegrees(90);
//			} else {
//				while (!(degrees % 90 == 0)) {
//					degrees++;
//				}
//				mAttacher.setRotationTo(degrees);
//				mAttacher.setRotationDegrees(degrees);
//			}
//			break;
//		case R.id.button1:
//			outFile = mAttacher.createFileFromBitmap(mAttacher.getCropImage());
//			Intent intent = new Intent(this, CropImage.class);
//			intent.putExtra(CropImage.IMAGE_PATH, outFile.getPath());
//			intent.putExtra(CropImage.SCALE, true);
//			intent.putExtra(CropImage.ASPECT_X, 3);
//			intent.putExtra(CropImage.ASPECT_Y, 2);
//			startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
//			break;
//		}
//	};
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main_menu, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//
//		// Need to call clean-up
//		mAttacher.cleanup();
//	}
//
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		MenuItem zoomToggle = menu.findItem(R.id.menu_zoom_toggle);
//		assert null != zoomToggle;
//		zoomToggle.setTitle(mAttacher.canZoom() ? R.string.menu_zoom_disable
//				: R.string.menu_zoom_enable);
//
//		return super.onPrepareOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.menu_zoom_toggle:
//			mAttacher.setZoomable(!mAttacher.canZoom());
//			return true;
//
//		case R.id.menu_scale_fit_center:
//			mAttacher.setScaleType(ScaleType.FIT_CENTER);
//			return true;
//
//		case R.id.menu_scale_fit_start:
//			mAttacher.setScaleType(ScaleType.FIT_START);
//			return true;
//
//		case R.id.menu_scale_fit_end:
//			mAttacher.setScaleType(ScaleType.FIT_END);
//			return true;
//
//		case R.id.menu_scale_fit_xy:
//			mAttacher.setScaleType(ScaleType.FIT_XY);
//			return true;
//
//		case R.id.menu_scale_scale_center:
//			mAttacher.setScaleType(ScaleType.CENTER);
//			return true;
//
//		case R.id.menu_scale_scale_center_crop:
//			mAttacher.setScaleType(ScaleType.CENTER_CROP);
//			return true;
//
//		case R.id.menu_scale_scale_center_inside:
//			mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
//			return true;
//
//		case R.id.menu_scale_random_animate:
//		case R.id.menu_scale_random:
//			Random r = new Random();
//
//			float minScale = mAttacher.getMinimumScale();
//			float maxScale = mAttacher.getMaximumScale();
//			float randomScale = minScale
//					+ (r.nextFloat() * (maxScale - minScale));
//			mAttacher.setScale(randomScale,
//					item.getItemId() == R.id.menu_scale_random_animate);
//
//			showToast(String.format(SCALE_TOAST_STRING, randomScale));
//
//			return true;
//		case R.id.menu_matrix_restore:
//			if (mCurrentDisplayMatrix == null)
//				showToast("You need to capture display matrix first");
//			else
//				mAttacher.setDisplayMatrix(mCurrentDisplayMatrix);
//			return true;
//		case R.id.menu_matrix_capture:
//			mCurrentDisplayMatrix = mAttacher.getDisplayMatrix();
//			return true;
//		case R.id.extract_visible_bitmap:
//			try {
//				Bitmap bmp = mAttacher.getVisibleRectangleBitmap();
//				File tmpFile = File
//						.createTempFile(
//								"photoview",
//								".png",
//								Environment
//										.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
//				FileOutputStream out = new FileOutputStream(tmpFile);
//				bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
//				out.close();
//				Intent share = new Intent(Intent.ACTION_SEND);
//				share.setType("image/png");
//				share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tmpFile));
//				startActivity(share);
//				Toast.makeText(
//						this,
//						String.format("Extracted into: %s",
//								tmpFile.getAbsolutePath()), Toast.LENGTH_SHORT)
//						.show();
//			} catch (Throwable t) {
//				t.printStackTrace();
//				Toast.makeText(this, "Error occured while extracting bitmap",
//						Toast.LENGTH_SHORT).show();
//			}
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
//
//	private class PhotoTapListener implements OnPhotoTapListener {
//
//		@Override
//		public void onPhotoTap(View view, float x, float y) {
//			float xPercentage = x * 100f;
//			float yPercentage = y * 100f;
//
//			showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage,
//					yPercentage, view == null ? 0 : view.getId()));
//		}
//	}
//
//	private void showToast(CharSequence text) {
//		if (null != mCurrentToast) {
//			mCurrentToast.cancel();
//		}
//
//		mCurrentToast = Toast.makeText(SimpleSampleActivity.this, text,
//				Toast.LENGTH_SHORT);
//		mCurrentToast.show();
//	}
//
//	private class MatrixChangeListener implements OnMatrixChangedListener {
//
//		@Override
//		public void onMatrixChanged(RectF rect) {
//			mCurrMatrixTv.setText(rect.toString());
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//		if (resultCode != RESULT_OK) {
//
//			return;
//		}
//
//		Bitmap bitmap;
//
//		switch (requestCode) {
//		case REQUEST_CODE_CROP_IMAGE:
//
//			String path = data.getStringExtra(CropImage.IMAGE_PATH);
//			if (path == null) {
//				return;
//			}
//
//			bitmap = BitmapFactory.decodeFile(outFile.getPath());
//			mImageView.setImageBitmap(bitmap);
//			break;
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//}
