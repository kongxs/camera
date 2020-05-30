package cn.azir.cameratest;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.azir.cameratest.filter.GPUVideoFilter;
import cn.azir.cameratest.util.LogUtils;

public class CameraRendar implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener {


    private final GLSurfaceView mSurfaceView;
    private final GPUVideoFilter mFilter;
    private int mTextureID;
    private SurfaceTexture mSurface;
    private DirectDrawer mDirectDrawer;

    public CameraRendar(GLSurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
        this.mFilter = new GPUVideoFilter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        LogUtils.error("onSurfaceCreated");

        createTextureID();

        mSurface = new SurfaceTexture(mTextureID);

        mSurface.setOnFrameAvailableListener(this);

        mDirectDrawer = new DirectDrawer(mTextureID);
        CameraInterface.getInstance().doOpenCamera(null);
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        LogUtils.error("onSurfaceChanged");

        GLES20.glViewport(0, 0, width, height);
        if(!CameraInterface.getInstance().isPreviewing()){
            CameraInterface.getInstance().doStartPreview(mSurface, 1.33f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        LogUtils.error("onDrawFrame");

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurface.updateTexImage();
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mDirectDrawer.draw(mtx);
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        LogUtils.error("onFrameAvailable");

        this.mSurfaceView.requestRender();
    }

    private void createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        mTextureID = texture[0];

    }

}
