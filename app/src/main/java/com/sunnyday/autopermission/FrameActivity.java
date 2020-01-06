package com.sunnyday.autopermission;

import android.Manifest;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;

//2 实现回调
public class FrameActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    boolean isGrand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
    }

    public void EasyPermission(View view) {
        // 3、判断是否具有权限
        isGrand = EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION);
        if (isGrand) {
            Toast.makeText(this, "已经具备权限", Toast.LENGTH_SHORT).show();
            // todo 具体的逻辑
        } else {
            EasyPermissions.requestPermissions(this,
                    "接下来需要获取WRITE_EXTERNAL_STORAGE权限",
                    0x01,
                    Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 1、接管事件
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 0x11) {
            Toast.makeText(this, "位置和照相机权限获得成功", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog
                .Builder(this)
                .setRationale("此功能需要" + "权限，否则无法正常使用，是否打开设置")
                .setPositiveButton("好")
                .setNegativeButton("不行")
                .build()
                .show();

    }
}
