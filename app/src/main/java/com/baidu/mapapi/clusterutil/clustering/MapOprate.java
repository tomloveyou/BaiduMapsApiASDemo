package com.baidu.mapapi.clusterutil.clustering;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;

/**
 * Created by lenovo on 2017/9/6.
 */

public class MapOprate<T extends ClusterItem> implements
        BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener {
    private ClusterManager<T>clusterManager;
    private ClusterManager2<T>clusterManager2;

    public MapOprate(ClusterManager<T> clusterManager, ClusterManager2<T> clusterManager2) {
        this.clusterManager = clusterManager;
        this.clusterManager2 = clusterManager2;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
       
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
