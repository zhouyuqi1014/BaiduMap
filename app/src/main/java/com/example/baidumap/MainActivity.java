package com.example.baidumap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;

import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MapView mMapView = null;
    BaiduMap mBaiduMap ;
    LocationClient mLocationClient;

    TextView tv_Lat;  //纬度
    TextView tv_Lon;  //经度
    TextView tv_Add;  //地址
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_Add = findViewById(R.id.tv_Add);
        tv_Lon = findViewById(R.id.tv_Lon);
        tv_Lat = findViewById(R.id.tv_Lat);
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //设置地图类型
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启地图的定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //从系统获取定位权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            //设置客户端
            mLocationClient = new LocationClient(this);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setIsNeedAddress(true); //设置是否需要地址信息，默认不需要
            mLocationClient.setLocOption(option);
            //设置监听器
            MylocationListener myLocationListener = new MylocationListener();
            mLocationClient.registerLocationListener(myLocationListener);
            //开启地图定位图层
            mLocationClient.start();

            MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
            MyLocationConfiguration mLocationConfiguration = new MyLocationConfiguration(mCurrentMode,true,null,0xAAFFFF88,0xAA00FF00);
            mBaiduMap.setMyLocationConfiguration(mLocationConfiguration);
        }


    }
    private class MylocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mMapView == null){
                return;
            }
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(bdLocation.getDirection())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLatitude())
                    .build();
            mBaiduMap.setMyLocationData(locationData);

            //输出经纬度和地点
            tv_Add.setText(bdLocation.getAddrStr());
            tv_Lat.setText(bdLocation.getLatitude()+" ");
            tv_Lon.setText(bdLocation.getLongitude()+" ");
            LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
        }
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();

    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();

    }

}