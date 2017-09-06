/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package baidumapsdk.demo.map;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager2;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import baidumapsdk.demo.AirPortBean;
import baidumapsdk.demo.PointBean;
import baidumapsdk.demo.R;

/**
 * 此Demo用来说明点聚合功能
 */
public class MarkerClusterDemo extends Activity implements OnMapLoadedCallback {

    MapView mMapView;
    BaiduMap mBaiduMap;
    MapStatus ms;


    private final int MAP_STATUS_CHANGE = 100;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MAP_STATUS_CHANGE:
                    MapStatus mapStatus = (MapStatus) msg.obj;
                    if (mapStatus != null) {
                        Log.i("MarkerClusterDemo", "mapStatus=" + mapStatus.toString());
                        // to do :  判断地图状态，进行相应处理
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_cluster_demo);
        mMapView = (MapView) findViewById(R.id.bmapView);
        ms = new MapStatus.Builder().target(new LatLng(35.914935, 120.403119)).zoom(8).build();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));

        // 添加Marker点
        getAirPortList("1,2,3,4");
        getadta();
        // addMarkers();


    }

    /**
     * 解析固定点数据
     *
     * @param onJsonObject 固定点数据来源
     * @return PointBean
     */
    private PointBean encripteFixPoint(JSONObject onJsonObject) {
        PointBean pointBean = new PointBean();
        pointBean.setId(onJsonObject.optInt("id"));
        pointBean.setAltitude(onJsonObject.optInt("altitude"));
        pointBean.setType(onJsonObject.optInt("type"));
        pointBean.setName(onJsonObject.optString("name"));
        pointBean.setLatLng(new LatLng(onJsonObject.optDouble("latitude"), onJsonObject.optDouble("longitude")));

        return pointBean;
    }

    /**
     * 获取所有的机场列表并将机场maker的配置装进集合
     */
    private void getAirPortList(String type) {
        final ClusterManager2<MyItem> mClusterManager= new ClusterManager2<MyItem>(this, mBaiduMap,2);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager2.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                Toast.makeText(MarkerClusterDemo.this,
                        "有" + cluster.getSize() + "个点", Toast.LENGTH_SHORT).show();

                List<MyItem> items = (List<MyItem>) cluster.getItems();
                LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                int i = 0;
                for (MyItem myItem : items) {
                    builder2 = builder2.include(myItem.getPosition());
                    Log.i("map", "log: i=" + i++ + " pos=" + myItem.getPosition().toString());
                }

                LatLngBounds latlngBounds = builder2.build();
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds, mMapView.getWidth(), mMapView.getHeight());
                mBaiduMap.animateMapStatus(u);
                Log.i("map", "log: mBaiduMap.animateMapStatus(u)");

                return false;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager2.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                String showText = "点击单个Item";
                if (item.getBundle() != null) {
                    showText += " index=" + item.getBundle().getString("index");
                }
                Toast.makeText(MarkerClusterDemo.this,
                        showText, Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        mClusterManager.setHandler(handler, MAP_STATUS_CHANGE); //设置handler
        RequestParams params = new RequestParams("https://www.easy-flying.com/fly-mob/adj.do?s=hhService&m=queryNavadataAirPort");
        params.addBodyParameter("airPortType", type);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject jsonObject = result.optJSONObject("result");
                    JSONArray arJsonArray = jsonObject.getJSONArray("list");

                    List<MyItem> items = new ArrayList<MyItem>();
                    if (arJsonArray != null && arJsonArray.length() > 0) {
                        for (int i = 0; i < arJsonArray.length(); i++) {
                            Bundle bundleA = new Bundle();
                            AirPortBean pointBean = new Gson().fromJson(arJsonArray.optJSONObject(i).toString(),AirPortBean.class);

                            bundleA.putInt("type", pointBean.getType());
                            bundleA.putSerializable("data", pointBean);
                            items.add(new MyItem(new LatLng(pointBean.getLatitude(),pointBean.getLongitude()), bundleA));

                        }
                        mClusterManager.addItems(items);
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }
    private void getadta() {
        final ClusterManager<MyItem> mClusterManager= new ClusterManager<MyItem>(this, mBaiduMap,1);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                Toast.makeText(MarkerClusterDemo.this,
                        "有" + cluster.getSize() + "个点", Toast.LENGTH_SHORT).show();

                List<MyItem> items = (List<MyItem>) cluster.getItems();
                LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                int i = 0;
                for (MyItem myItem : items) {
                    builder2 = builder2.include(myItem.getPosition());
                    Log.i("map", "log: i=" + i++ + " pos=" + myItem.getPosition().toString());
                }

                LatLngBounds latlngBounds = builder2.build();
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds, mMapView.getWidth(), mMapView.getHeight());
                mBaiduMap.animateMapStatus(u);
                Log.i("map", "log: mBaiduMap.animateMapStatus(u)");

                return false;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                String showText = "点击单个Item";
                if (item.getBundle() != null) {
                    showText += " index=" + item.getBundle().getString("index");
                }
                Toast.makeText(MarkerClusterDemo.this,
                        showText, Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        mClusterManager.setHandler(handler, MAP_STATUS_CHANGE); //设置handler
        RequestParams params = new RequestParams("https://www.easy-flying.com/fly-mob/adj.do?s=hhService&m=queryFixPoint");
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject jsonObject = result.optJSONObject("result");
                    JSONArray arJsonArray = jsonObject.getJSONArray("list");

                    List<MyItem> items = new ArrayList<MyItem>();
                    if (arJsonArray != null && arJsonArray.length() > 0) {
                        for (int i = 0; i < arJsonArray.length(); i++) {
                            Bundle bundleA = new Bundle();
                            PointBean pointBean = encripteFixPoint(arJsonArray.optJSONObject(i));
                            bundleA.putInt("type", 112);
                            bundleA.putSerializable("data", pointBean);
                            items.add(new MyItem(pointBean.getLatLng(), bundleA));

                        }
                        mClusterManager.addItems(items);
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    /**
     * 向地图添加Marker点
     */
    public void addMarkers() {
        // 添加Marker点
        LatLng llA = new LatLng(35.963175, 120.400244);
        LatLng llB = new LatLng(35.952821, 120.399199);
        LatLng llC = new LatLng(35.939723, 120.425541);
        LatLng llD = new LatLng(35.906965, 120.401394);
        LatLng llE = new LatLng(35.956965, 120.331394);
        LatLng llF = new LatLng(35.886965, 120.441394);
        LatLng llG = new LatLng(35.996965, 120.411394);

        Bundle bundleA = new Bundle();
        bundleA.putString("index", "001");
        Bundle bundleB = new Bundle();
        bundleB.putString("index", "002");
        Bundle bundleC = new Bundle();
        bundleC.putString("index", "003");
        List<MyItem> items = new ArrayList<MyItem>();
        items.add(new MyItem(llA, bundleA));
        items.add(new MyItem(llB, bundleB));
        items.add(new MyItem(llC, bundleC));
        items.add(new MyItem(llD));
        items.add(new MyItem(llE));
        items.add(new MyItem(llF));
        items.add(new MyItem(llG));

       //mClusterManager.addItems(items);

    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private Bundle mBundle;

        public MyItem(LatLng latLng) {
            mPosition = latLng;
            mBundle = null;
        }

        public MyItem(LatLng latLng, Bundle bundle) {
            mPosition = latLng;
            mBundle = bundle;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            int iconId = R.drawable.icon_gcoding;
            if (mBundle != null) {
                if (112 == mBundle.getInt("type")) {
                    iconId = R.drawable.fixpoint;
                }else if (1 == mBundle.getInt("type")){
                    iconId = R.drawable.aiport_plan;
                }else if (2 == mBundle.getInt("type")){
                    iconId = R.drawable.aiport_nong;
                }else if (3 == mBundle.getInt("type")){
                    iconId = R.drawable.aiport_hihang;
                }else if (4 == mBundle.getInt("type")){
                    iconId = R.drawable.aiport_start;
                }
            }

            return BitmapDescriptorFactory
                    .fromResource(iconId);//R.drawable.icon_gcoding);
        }

        public Bundle getBundle() {
            return mBundle;
        }

    }

    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
        ms = new MapStatus.Builder().zoom(9).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

}
