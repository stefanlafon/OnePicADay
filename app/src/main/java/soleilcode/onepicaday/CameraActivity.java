package soleilcode.onepicaday;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {

    private CameraManager mCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        mCameraManager = new CameraManager(this, preview);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        mCameraManager.start();
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        mCameraManager.stop();
    }
}
