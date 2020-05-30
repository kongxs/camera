package cn.azir.cameratest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.widget.FrameLayout;

import cn.azir.cameratest.filter.GPUVideoGrayscaleFilter;
import cn.azir.cameratest.filter.GPUVideoSaturationFilter;
import cn.azir.cameratest.util.DisplayUtil;

public class MainActivity extends AppCompatActivity {

    CameraGLSurfaceView gl_surface_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gl_surface_view = findViewById(R.id.gl_surface_view);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) gl_surface_view.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        float previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        gl_surface_view.setLayoutParams(params);


        gl_surface_view.setFilter(new GPUVideoGrayscaleFilter());
    }
}
