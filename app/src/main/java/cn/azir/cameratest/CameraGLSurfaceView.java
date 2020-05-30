package cn.azir.cameratest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import cn.azir.cameratest.filter.GPUVideoFilter;

public class CameraGLSurfaceView extends GLSurfaceView {


    private CameraRendar renderer;

    public CameraGLSurfaceView(Context context) {
        this(context , null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        renderer = new CameraRendar(this);
        setRenderer(renderer);
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

    public void setFilter(GPUVideoFilter filter) {
        renderer.setFilter(filter);
    }
}
