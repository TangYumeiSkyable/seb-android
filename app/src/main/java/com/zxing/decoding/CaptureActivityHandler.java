/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.decoding;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACUserDevice;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.LoginActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.supor.suporairclear.activity.AddDeviceActivity;
import com.supor.suporairclear.activity.DeviceBindQRActivity;
import com.supor.suporairclear.activity.MainActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.Map;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class
			.getSimpleName();

	private final DeviceBindQRActivity activity;
	private final DecodeThread decodeThread;
	private State state;
	private final CameraManager cameraManager;
	private BindNewDeviceListener bindNewDeviceListener;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(DeviceBindQRActivity activity,
			Collection<BarcodeFormat> decodeFormats,
			Map<DecodeHintType, ?> baseHints, String characterSet,
			CameraManager cameraManager) {
		this.activity = activity;
		// change by xuri
		decodeThread = new DecodeThread(activity, decodeFormats, baseHints,
				characterSet, null);
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		this.cameraManager = cameraManager;
		cameraManager.startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.restart_preview:
			restartPreviewAndDecode();
			break;
		case R.id.decode_succeeded:
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = null;
			float scaleFactor = 1.0f;
			if (bundle != null) {
				byte[] compressedBitmap = bundle
						.getByteArray(DecodeThread.BARCODE_BITMAP);
				if (compressedBitmap != null) {
					barcode = BitmapFactory.decodeByteArray(compressedBitmap,
							0, compressedBitmap.length, null);
					// Mutable copy:
					barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
				}
				scaleFactor = bundle
						.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
			}
			activity.handleDecode((Result) message.obj, barcode, scaleFactor);
			break;
		case R.id.decode_failed:
			// We're decoding as fast as possible, so when one decode fails,
			// start another.
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			break;
		case R.id.return_scan_result:
			String msg = ((Intent) message.obj)
					.getStringExtra(DeviceBindQRActivity.KEY_SCAN_RESULT);
			if(msg.indexOf("#") < 0 && msg.indexOf("&") > 0)
			{
				//model&APPLIANCE_1045&subdomainId&subDomainId
				String[] msgArray = msg.split("&");
				boolean check =false;
				for(ACObject mode : ConstantCache.deviceModeList){
					if(msgArray[1].equals(mode.get("id").toString())){
						check=true;
						ConstantCache.subDomainId = Integer.valueOf(msgArray[3]);
						ConstantCache.subDomainName = mode.getString("subDomainName");
						ConstantCache.bindModelName = mode.getString("name");
						ConstantCache.bindModelPic = mode.getString("img");
						break;
					}
				}
                if(!check){
					Toast.makeText(activity,activity.getString(R.string.airpurifier_moredevice_show_qrcodeidwrong_text), AppConstant.TOAST_DURATION).show();
					activity.finish();
					return;
				}
				bindNewDeviceListener.bindNewDevice();
			}
			// 扫共享码添加
			else
			{
				AC.bindMgr().bindDeviceWithShareCode(msg,
					new PayloadCallback<ACUserDevice>() {
						@Override
						public void success(ACUserDevice userDevice) {
							try {
								Toast.makeText(activity, activity.getString(R.string.airpurifier_moredevice_show_bindsuccessfuly_text), AppConstant.TOAST_DURATION)
								.show();
								Intent intent = new Intent();
								intent.setClass(activity, MainActivity.class);
								activity.startActivity(intent);
								ConstantCache.appManager.finishAllActivity();
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}

						@Override
						public void error(ACException e) {
							if (e.getErrorCode() == 3817) {
								Toast.makeText(
										activity,
										activity.getString(R.string.airpurifier_moredevice_show_qrcodeisoverdue_text),
										AppConstant.TOAST_DURATION).show();

							} else {
								Toast.makeText(
										activity,
										activity.getString(R.string.airpurifier_moredevice_show_additionfailed_text),
										AppConstant.TOAST_DURATION).show();

							}
							
							activity.finish();
						}
					});
			}
			break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			// Wait at most half a second; should be enough time, and onPause()
			// will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			activity.drawViewfinder();
		}
	}
	
	public interface BindNewDeviceListener {
    	public void bindNewDevice();
	}
    public void setBindNewDeviceListener(BindNewDeviceListener bindNewDeviceListener) {
		this.bindNewDeviceListener = bindNewDeviceListener;
	}
}
