package tw.org.iii.ellen.ellen36;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;

public class CameraActivity extends AppCompatActivity {
    private Camera camera ;
    private CameraPreview cameraPreview ;
    private FrameLayout container ;
    private File sdroot ;
    private SensorManager sensorManager ;
    private Sensor sensor ;
    private MyListener myListener ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE) ;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) ;
        myListener = new MyListener() ;

        //-------------
        sdroot = Environment.getExternalStorageDirectory() ;
        camera = getCameraInstance() ;
        container = findViewById(R.id.priview) ;
        cameraPreview = new CameraPreview(this, camera) ;
        container.addView(cameraPreview,0) ;
        //camera.setDisplayOrientation(90);

        int r = getWindowManager().getDefaultDisplay().getRotation() ;
        Log.v("ellen","r = " + r) ;
//        if (r == 0){
//            camera.setDisplayOrientation(90) ;
//        }else if (r == 1){
//            camera.setDisplayOrientation(0);
//        }else {
//            camera.setDisplayOrientation(180);
//        }
        sensorManager.registerListener(myListener,sensor,SensorManager.SENSOR_DELAY_NORMAL) ;

    }

    private class MyListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values ;
            float v1 = values[0], v2 = values[1],v3 = values[2] ;
            Log.v("ellen", v1 + ":" + v2 + ":" + v3) ;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }


    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(myListener) ;
        releaseCamera() ;
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    public void takePic(View view) {
        camera.takePicture(
                new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        Log.v("ellen","shutter") ;
                    }
                },
                new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Log.v("ellen","debug1") ;
                    }
                },
                new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        savePic(data) ;
                        Log.v("ellen","debug2") ;
                    }
                });
    }

    private void savePic(byte[] data){
        Log.v("ellen","file" + data.length) ;
        try {
            FileOutputStream fout = new FileOutputStream(new File(sdroot, "iii02.jpg")) ;
            fout.write(data) ;
            fout.flush() ;
            fout.close() ;
            setResult(RESULT_OK);
            finish() ;

        }catch (Exception e){
            Log.v("ellen",e.toString()) ;
        }
    }
}
