package team6.iguide;

import android.content.Context;
import android.graphics.Color;

import com.mapbox.mapboxsdk.overlay.TilesOverlay;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBase;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBasic;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;

public class FloorLevel {

    TilesOverlay level_0;
    TilesOverlay level_1;
    TilesOverlay level_2;
/*
    public void loadFloorLevels(Context context, MapView mv){
        // Create level_0 layer
        MapboxTileLayer level0Overlay = new MapboxTileLayer("cammace.nc76p7k8");
        MapTileLayerBase level0OverlayBase = new MapTileLayerBasic(context, level0Overlay, mv);
        level_0 = new TilesOverlay(level0OverlayBase);
        level_0.setDrawLoadingTile(false);
        level_0.setLoadingBackgroundColor(Color.TRANSPARENT);

        // Create level_1 layer
        MapboxTileLayer level1Overlay = new MapboxTileLayer("cammace.nc77c38k");
        MapTileLayerBase level1OverlayBase = new MapTileLayerBasic(context, level1Overlay, mv);
        level_1 = new TilesOverlay(level1OverlayBase);
        level_1.setDrawLoadingTile(false);
        level_1.setLoadingBackgroundColor(Color.TRANSPARENT);

        // Create level_2 layer
        MapboxTileLayer level2Overlay = new MapboxTileLayer("cammace.nc77kk5a");
        MapTileLayerBase level2OverlayBase = new MapTileLayerBasic(context, level2Overlay, mv);
        level_2 = new TilesOverlay(level2OverlayBase);
        level_2.setDrawLoadingTile(false);
        level_2.setLoadingBackgroundColor(Color.TRANSPARENT);
    }


    public void changeFloorLevel(Context context, MapView mv, int level){

        mv.setAccessToken("sk.eyJ1IjoiY2FtbWFjZSIsImEiOiI1MDYxZjA1MDc0YzhmOTRhZWFlODBlNGVlZDgzMTcxYSJ9.Ryw8G5toQp5yloce36hu2A");

        if(level == 0){
            MapboxTileLayer level0Overlay = new MapboxTileLayer("cammace.nc76p7k8");
            MapTileLayerBase level0OverlayBase = new MapTileLayerBasic(context, level0Overlay, mv);
            level_0 = new TilesOverlay(level0OverlayBase);
            level_0.setDrawLoadingTile(false);
            level_0.setLoadingBackgroundColor(Color.TRANSPARENT);

            mv.getOverlays().remove(level_1);
            mv.getOverlays().remove(level_2);
            mv.getOverlays().add(level_0);
            mv.invalidate();

        }


        if(level == 1){
            MapboxTileLayer level1Overlay = new MapboxTileLayer("cammace.nc77c38k");
            MapTileLayerBase level1OverlayBase = new MapTileLayerBasic(context, level1Overlay, mv);
            level_1 = new TilesOverlay(level1OverlayBase);
            level_1.setDrawLoadingTile(false);
            level_1.setLoadingBackgroundColor(Color.TRANSPARENT);

            //loadFloorLevels(context, mv);
            mv.getOverlays().remove(level_0);
            mv.getOverlays().remove(level_2);
            mv.getOverlays().add(level_1);
            mv.invalidate();

        }


        if(level == 2){

            MapboxTileLayer level2Overlay = new MapboxTileLayer("cammace.nc77kk5a");
            MapTileLayerBase level2OverlayBase = new MapTileLayerBasic(context, level2Overlay, mv);
            level_2 = new TilesOverlay(level2OverlayBase);
            level_2.setDrawLoadingTile(false);
            level_2.setLoadingBackgroundColor(Color.TRANSPARENT);

            mv.getOverlays().remove(level_0);
            mv.getOverlays().remove(level_1);
            mv.getOverlays().add(level_2);
            mv.invalidate();

        }
    }*/
}
