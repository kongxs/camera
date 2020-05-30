package cn.azir.cameratest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CameraGLSurfaceView extends GLSurfaceView {


    public CameraGLSurfaceView(Context context) {
        this(context , null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(new CameraRendar(this));
    }





    @Override
    public void onResume() {
        super.onResume();

        bringToFront();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
    }
}
