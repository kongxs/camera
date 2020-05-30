package cn.azir.cameratest;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.azir.cameratest.filter.GPUVideoFilter;
import cn.azir.cameratest.util.LogUtils;

import static cn.azir.cameratest.TextureRotationUtil.TEXTURE_NO_ROTATION;

public class CameraRendar implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener {

    public static final int NO_VIDEO = -1;
    public static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };


    private final GLSurfaceView mSurfaceView;
    private  GPUVideoFilter mFilter;
    private final LinkedList<Runnable> mRunOnDraw;
    private final LinkedList<Runnable> mRunOnDrawEnd;
    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private int mTextureID;
    private SurfaceTexture mSurface;
    private DirectDrawer mDirectDrawer;
    private int mOutputWidth;
    private int mOutputHeight;

    public CameraRendar(GLSurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
        this.mFilter = new GPUVideoFilter();

        mRunOnDraw = new LinkedList<Runnable>();
        mRunOnDrawEnd = new LinkedList<Runnable>();

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TEXTURE_NO_ROTATION);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        LogUtils.error("onSurfaceCreated");

        createTextureID();

        mSurface = new SurfaceTexture(mTextureID);

        mSurface.setOnFrameAvailableListener(this);

        mFilter.init();

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


        mOutputWidth = width;
        mOutputHeight = height;
        GLES20.glUseProgram(mFilter.getProgram());
        mFilter.onOutputSizeChanged(width, height);


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


//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        runAll(mRunOnDraw);
//        mFilter.onDraw(mTextureID, mGLCubeBuffer, mGLTextureBuffer);
//        runAll(mRunOnDrawEnd);
//        if (mSurface != null) {
//            mSurface.updateTexImage();
////            mSurfaceTexture.getTransformMatrix(mStMatrix);
//        }

    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    public void setFilter(final GPUVideoFilter filter) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                final GPUVideoFilter oldFilter = mFilter;
                mFilter = filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                mFilter.init();
                GLES20.glUseProgram(mFilter.getProgram());
                mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
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
