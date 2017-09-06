package baidumapsdk.demo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import org.xutils.x;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        x.Ext.init(this);
        x.Ext.setDebug(true);

    }

}