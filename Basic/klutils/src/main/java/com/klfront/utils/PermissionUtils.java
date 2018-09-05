package com.klfront.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by L on 2016/12/5.
 */

public class PermissionUtils {
    public static boolean GetAudioRecordPermission() {
        boolean result =false;
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                result = false;
            }
            else {
                record.release();
                result = true;
            }
        } catch (Exception e) {
            Log.e("GetAudioPermission",e.getMessage());

        }
        return result;
    }

    public static boolean HasPermission(Context context,String permission)
    {
        return context.getPackageManager().checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean HasRefusedPermission(Context context,String permission)
    {
        return context.getPackageManager().checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_DENIED;
    }

    // 是否开启gps服务
    public static final boolean isGpsEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

}
