package team6.iguide;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.drawable.Drawable;

import com.mapbox.mapboxsdk.clustering.geometry.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.MapView;

public class DrawRoute extends PathOverlay {


    @Override
    protected void draw(Canvas canvas, MapView mapView, boolean shadow) {

        //addPoint(29.7222194,-95.3431569);
        //addPoint(29.729268922766913, -95.34362554550171);




/*
        PathEffect pathEffect = new PathDashPathEffect(makePathDash(), 12, 10, PathDashPathEffect.Style.MORPH);

        Path path = new Path();
        path.
        canvas.drawPath(path, mPaint);


        pathOverlay.addPoint(29.7222194,-95.3431569);
        pathOverlay.addPoint(29.729268922766913, -95.34362554550171);
        pathOverlay.setOptimizePath(false);

        pathOverlay.getPaint().setColor(Color.BLACK);
        pathOverlay.getPaint().setStrokeCap(Paint.Cap.ROUND);
        pathOverlay.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        //pathOverlay.getPaint().setPathEffect(new D)
        pathOverlay.getPaint().setStrokeWidth(10);


        //super.draw(canvas, mapView, shadow);
        //Paint paint = new Paint();

       // PathEffect pathEffect = new ComposePathEffect()
/*

        PathOverlay pathOverlay = new PathOverlay();
        pathOverlay.addPoint(29.7222194,-95.3431569);
        pathOverlay.addPoint(29.729268922766913, -95.34362554550171);
        pathOverlay.setOptimizePath(false);

        pathOverlay.getPaint().setColor(Color.BLACK);
        pathOverlay.getPaint().setStrokeCap(Paint.Cap.ROUND);
        pathOverlay.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        //pathOverlay.getPaint().setPathEffect(new D)
        pathOverlay.getPaint().setStrokeWidth(10);
*/
        // Get Height for LinearGradient
        //PointF topLeft = mv.getProjection().toPixels(new LatLng(29.7222194,-95.3431569), null);
        //PointF bottomLeft = mv.getProjection().toPixels(new LatLng(29.729268922766913, -95.34362554550171), null);
        //int height = (int) Math.abs(topLeft.y) - (int) Math.abs(bottomLeft.y);

        // pathOverlay.getPaint().setShader(new LinearGradient(0, 0, 0, height, Color.GREEN, Color.YELLOW, Shader.TileMode.MIRROR));

    }
}
