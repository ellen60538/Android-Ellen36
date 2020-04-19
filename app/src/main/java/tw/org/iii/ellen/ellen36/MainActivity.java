package tw.org.iii.ellen.ellen36;
//照相程式

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator ;
    private SwitchCompat switchLight ;
    private CameraManager cameraManager ;
    private File sdroot, downloadDir ;
    private ImageView img ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }else {
            init() ;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init() ;
    }

    private void init(){
        img = findViewById(R.id.img) ;
        sdroot = Environment.getExternalStorageDirectory() ;
        downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) ;
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE) ;
        switchLight = findViewById(R.id.switchLight) ;
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE) ;
        switchLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //APK23以前要用不同方法開啟閃光燈
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        onFlashLight() ;
                    else
                        setOnLight() ;
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        offFlashLight() ;
                    else
                        setOffLight();
                }
            }
        });
    }

    private Camera camera ;
    private void setOnLight(){
        camera = Camera.open() ;
        Camera.Parameters p = camera.getParameters() ;
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH) ;
        camera.setParameters(p) ;
        camera.startPreview() ;
    }

    private void setOffLight(){
        camera.stopPreview();
        camera.release() ;
    }

    private void onFlashLight(){
        try {
            cameraManager.setTorchMode("0",true) ;

        }catch (Exception e){}
    }
    private void offFlashLight(){
        try {
            cameraManager.setTorchMode("0",false) ;

        }catch (Exception e){}
    }

    public void test1(View view) {
        //震動
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(VibrationEffect.createOneShot(
                    1000,VibrationEffect.DEFAULT_AMPLITUDE));
        }else {
            vibrator.vibrate(1000) ;
        }
    }

    public void test2(View view) {
        //SOS震動
        int dot = 200; // Length of a Morse Code "dot" in milliseconds
        int dash = 500; // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200; // Length of Gap Between dots/dashes
        int medium_gap = 500; // Length of Gap Between Letters
        int long_gap = 1000; // Length of Gap Between Words

        long[] pattern = { 0, // Start immediately
                dot, short_gap, dot, short_gap, dot, // s
                medium_gap, dash, short_gap, dash, short_gap, dash, // o
                medium_gap, dot, short_gap, dot, short_gap, dot, // s
                long_gap };
        vibrator.vibrate(pattern,-1) ;
    }

    public void test3(View view) {
        //Uri uri = Uri.fromFile(new File(sdroot,"iii01.jpg")) ;
        Uri uri = FileProvider.getUriForFile(
                this, getPackageName()+".fileprovider",new File(downloadDir,"iii01.jpg")) ;



        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE) ;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri) ;
        startActivityForResult(intent, 123) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK){
            Bitmap bitmap =
                    BitmapFactory.decodeFile(downloadDir.getAbsolutePath() + "/iii01.jpg") ;
            img.setImageBitmap(bitmap) ;
        }else if (requestCode == 124 && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras() ;
            Set<String> keys = bundle.keySet() ;
            for (String key : keys){
                Log.v("ellen",key) ;
                Object obj = bundle.get(key) ;
                Log.v("ellen",obj.getClass().getName()) ;
            }
            Bitmap bmp = (Bitmap) bundle.get("data") ;
            img.setImageBitmap(bmp) ;
        }else if (requestCode == 125 && resultCode == RESULT_OK){
            Bitmap bitmap =
                    BitmapFactory.decodeFile(sdroot.getAbsolutePath() + "/iii02.jpg") ;
            img.setImageBitmap(bitmap) ;
        }
    }

    public void test4(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE) ;
        startActivityForResult(intent, 124) ;
    }

    public void test5(View view) {
        Intent intent = new Intent(this,CameraActivity.class) ;
        startActivityForResult(intent,125);
    }
}
