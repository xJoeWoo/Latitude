package ng.latitude.support.map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;

/**
 * Created by Ng on 15/6/13
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class SensorUnit implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor aSensor;
    private Sensor mSensor;

    private Sensor oSensor;

    private float[] aValues;
    private float[] mValues;
    private OnHeadingChangedListener onHeadingChangedListener;
    private int lastHeading;


    public SensorUnit(Context context) {
        if (sensorManager == null)
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void setOnHeadingChangedListener(OnHeadingChangedListener onHeadingChangedListener) {
        this.onHeadingChangedListener = onHeadingChangedListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (onHeadingChangedListener != null) {

            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

                Log.e(Thread.currentThread().getStackTrace()[1].getClassName() + "#" + Thread.currentThread().getStackTrace()[1].getMethodName()
                        , String.valueOf(event.values[0]));

                int orientation = (int) event.values[0];
                WindowManager wm = (WindowManager) Latitude.getContext()
                        .getSystemService(Context.WINDOW_SERVICE);
                int rotation = wm.getDefaultDisplay().getRotation();

                if (rotation == Surface.ROTATION_0) {
                } else if (rotation == Surface.ROTATION_90) {
                    orientation = (orientation + 90) % 360;
                } else if (rotation == Surface.ROTATION_180) {
                    orientation = (orientation + 180) % 360;
                } else if (rotation == Surface.ROTATION_270) {
                    orientation = (orientation + 270) % 360;
                }

                if (Math.abs(orientation - lastHeading) > Constants.SENSOR_HEADING_UPDATE_LIMIT) {
                    lastHeading = orientation;
                    onHeadingChangedListener.onHeadingChanged(orientation);
                }


            }


//            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//                aValues = event.values.clone();
//            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//                mValues = event.values.clone();
//
//            if (aValues != null && mValues != null) {
//                float R[] = new float[9];
//                float I[] = new float[9];
//                boolean success = SensorManager.getRotationMatrix(R, I, aValues, mValues);
//
//                if (success) {
//                    float values[] = new float[3];
//                    SensorManager.getOrientation(R, values);
//
//                    int heading = (int) (Math.toDegrees(values[0]));
//
//                    if (Math.abs(heading - lastHeading) > Constants.SENSOR_HEADING_UPDATE_LIMIT) {
//
//                        lastHeading = heading;
//
//                        if (heading < 0)
//                            heading = -heading % 360;
//                        else
//                            heading = (360 - heading) % 360;
//
//                        WindowManager wm = (WindowManager) Latitude.getContext().getSystemService(Context.WINDOW_SERVICE);
//                        int rotation = wm.getDefaultDisplay().getRotation();
//
//                        if (rotation == Surface.ROTATION_0) {
//
//                        } else if (rotation == Surface.ROTATION_90) {
//                            heading = (heading + 90) % 360;
//                        } else if (rotation == Surface.ROTATION_180) {
//                            heading = (heading + 180) % 360;
//                        } else if (rotation == Surface.ROTATION_270) {
//                            heading = (heading + 270) % 360;
//                        }
//
//                        onHeadingChangedListener.onHeadingChanged(heading);
//                    }
//
//
//                }
//            }
        }


//            Log.e("Heading Raw", String.valueOf(values[0]));
//            if (values[0] >= -5 && values[0] < 5) {
//                Log.e("Heading", "正北");
//            } else if (values[0] >= 5 && values[0] < 85) {
//                Log.e("Heading", "东北");
//            } else if (values[0] >= 85 && values[0] <= 95) {
//                Log.e("Heading", "正东");
//            } else if (values[0] >= 95 && values[0] < 175) {
//                Log.e("Heading", "东南");
//            } else if ((values[0] >= 175 && values[0] <= 180) || (values[0]) >= -180 && values[0] < -175) {
//                Log.e("Heading", "正南");
//            } else if (values[0] >= -175 && values[0] < -95) {
//                Log.e("Heading", "西南");
//            } else if (values[0] >= -95 && values[0] < -85) {
//                Log.e("Heading", "正西");
//            } else if (values[0] >= -85 && values[0] < -5) {
//                Log.e("Heading", "西北");
//            }
//        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void release() {
        sensorManager.unregisterListener(this);
    }

    public void bind() {

        if (sensorManager != null) {

            if (aSensor == null)
                aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mSensor == null)
                mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (oSensor == null)
                oSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, oSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    public interface OnHeadingChangedListener {
        void onHeadingChanged(int heading);
    }


}
