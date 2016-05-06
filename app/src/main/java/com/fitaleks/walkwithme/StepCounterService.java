package com.fitaleks.walkwithme;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;

import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.data.firebase.FirebaseHelper;

import java.util.HashMap;

public class StepCounterService extends Service {
    private static final String LOG_TAG = StepCounterService.class.getSimpleName();
    private static final int ONE_MINUTE_IN_MS = 60 * 1000;

    public static final String TODAYS_DATE = "todaysdate";

//    private SensorManager sensorManager;
    private ServiceHandler serviceHandler;

    private int maxDelay = 0;
    private int mSteps = 0;
    private int mCounterSteps = 0;
//    private int mPreviousCounterSteps = 0;

    @Override
    public void onCreate() {
//        mPreviousCounterSteps = SharedPrefUtils.getNumOfStepsToday(this);
        if (isKitkatWithStepSensor()) {
            // Start up the thread running the service
            final HandlerThread thread = new HandlerThread("ServiceStartArguments",
                    Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            // Link the HandlerThread's looper with our ServiceHandler
            serviceHandler = new ServiceHandler(thread.getLooper());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String messageForServiceHandler = TODAYS_DATE + ";" + System.currentTimeMillis();
        // Send message to ServiceHandler
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = messageForServiceHandler;
        serviceHandler.sendMessage(msg);
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    /**
     * Returns true if this device is supported. It needs to be running Android KitKat (4.4) or
     * higher and has a step counter and step detector sensor.
     * This check is useful when an app provides an alternative implementation or different
     * functionality if the step sensors are not available or this code runs on a platform version
     * below Android KitKat. If this functionality is required, then the minSDK parameter should
     * be specified appropriately in the AndroidManifest.
     *
     * @return True iff the device can run this sample
     */
    private boolean isKitkatWithStepSensor() {
        // BEGIN_INCLUDE(iskitkatsensor)
        // Require at least Android KitKat
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        PackageManager packageManager = getPackageManager();
        return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        // END_INCLUDE(iskitkatsensor)
    }


    private final class ServiceHandler extends Handler implements SensorEventListener2 {
        private final SensorManager mSensorManager;
        private final Sensor sensorStepCounter;

        public ServiceHandler(Looper looper) {
            super(looper);
            mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            sensorStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        @Override
        public void handleMessage(Message msg) {
            String command = (String) msg.obj;
            if (command == null)
                return;

            if (command.contains(TODAYS_DATE)) {
                // Register sensor listener
                mSensorManager.registerListener(this, sensorStepCounter,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onFlushCompleted(Sensor sensor) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (event.sensor.getType() != Sensor.TYPE_STEP_COUNTER) {
                    return;
                }
                /*
                A step counter event contains the total number of steps since the listener
                was first registered. We need to keep track of this initial value to calculate the
                number of steps taken, as the first value a listener receives is undefined.
                 */
                if (mCounterSteps < 1) {
                    // initial value
                    mCounterSteps = (int) event.values[0];
                }

                // Calculate steps taken based on first counter value received.
                mSteps = (int) event.values[0] - mCounterSteps;
                android.util.Log.d("StepCoutnerService", "numOfSteps = " + mSteps);

                // Add the number of steps previously taken, otherwise the counter would start at 0.
                // This is needed to keep the counter consistent across rotation changes.
//                mSteps += mPreviousCounterSteps;
                final Cursor historyCursor = getContentResolver().query(WalkWithMeProvider.History.CONTENT_URI, null, null, null, FitnessHistory.DATE + " DESC LIMIT 1");
                final ContentValues contentValues = new ContentValues();
                android.util.Log.d("StepCoutnerService", "cursor = " + historyCursor + "numOfSteps = " + mSteps);
                if (historyCursor != null && historyCursor.moveToNext() && mSteps > 0) {

                    final long timeOfPrevRecord = historyCursor.getLong(historyCursor.getColumnIndex(FitnessHistory.DATE));
                    // All steps which are done with intervals less then one minute should be in one section
                    int newNumOfSteps = considerPreviousActivity(historyCursor, mSteps);
                    contentValues.put(FitnessHistory.NUM_OF_STEPS, newNumOfSteps);
                    contentValues.put(FitnessHistory.DATE, System.currentTimeMillis());
                    if (System.currentTimeMillis() - timeOfPrevRecord < ONE_MINUTE_IN_MS) {
                        android.util.Log.d("StepCoutnerService", "update = " + newNumOfSteps);
                        getContentResolver().update(WalkWithMeProvider.History.CONTENT_URI, contentValues, FitnessHistory.DATE + " = " + timeOfPrevRecord, null);
                    } else {
                        android.util.Log.d("StepCoutnerService", "insert1 = " + newNumOfSteps);
                        getContentResolver().insert(WalkWithMeProvider.History.CONTENT_URI, contentValues);
                    }
                    sendDataToFirebase();
                } else if (mSteps > 0) {
                    android.util.Log.d("StepCoutnerService", "insert2 = " + mSteps);
                    contentValues.put(FitnessHistory.NUM_OF_STEPS, mSteps);
                    contentValues.put(FitnessHistory.DATE, System.currentTimeMillis());
                    getContentResolver().insert(WalkWithMeProvider.History.CONTENT_URI, contentValues);
                }

                if (historyCursor != null)
                    historyCursor.close();

            }


        }

        private void sendDataToFirebase() {
            final Cursor cursor = getContentResolver().query(WalkWithMeProvider.History.CONTENT_URI, null, null, null, FitnessHistory.DATE + " DESC");
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
            final FirebaseHelper helper = new FirebaseHelper.Builder()
                    .addChild("steps")
                    .addChild("alex")
                    .build();
            final HashMap<String, String> steps = new HashMap<>();
            for (int i = 0; i < cursor.getCount(); ++i) {
                steps.put(Long.toString(cursor.getLong(cursor.getColumnIndex(FitnessHistory.DATE))),
                        Long.toString(cursor.getLong(cursor.getColumnIndex(FitnessHistory.NUM_OF_STEPS))));
                cursor.moveToNext();
            }
            helper.getFirebase().setValue(steps);
            cursor.close();
        }

        /**
         *
         * @param cursor - cursor from database with the most recent record of physical activity.
         * @param newStepsCount - possible new steps count before taking into account old user's activity.
         * @return new steps count, which is the result of taking into account his old activity.
         */
        private int considerPreviousActivity(final Cursor cursor, int newStepsCount) {
            final int prevNumberOfSteps = cursor.getInt(cursor.getColumnIndex(FitnessHistory.NUM_OF_STEPS));
            int newNumOfSteps = newStepsCount;
            // if previous number of steps is greater then result from sensor, it means that sensor measurement was relaunched and we are collecting new data.
            if (prevNumberOfSteps < newStepsCount) {
                newNumOfSteps -= prevNumberOfSteps;
            }
            return newNumOfSteps;
        }
    }
}