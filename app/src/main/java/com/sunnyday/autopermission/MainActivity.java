package com.sunnyday.autopermission;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 权限申请步骤：
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0x11; // 请求权限请求码
    private String[] permissions;
    private boolean isAllGrant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 开发是需要的权限
        permissions = new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    public void normal(View view) {
        isAllGrant = checkIsAllPermissionGranted(permissions, this);
        if (isAllGrant) {
            Toast.makeText(this, "检测到已有权限！", Toast.LENGTH_SHORT).show();
            doWork();

        } else {
            // 没有权限-> 申请权限
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }

    }

    public void frame(View view) {
        startActivity(new Intent(this, FrameActivity.class));
    }


    private void doWork() {
        // todo  有权限时，用户想要做的逻辑
    }

    /**
     * @param permissions 权限数组
     * @return 权限都授权时返回true，否则false
     * @function 检查是否所有的权限都授权
     */
    private boolean checkIsAllPermissionGranted(String[] permissions, Context context) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 权限回调处理，此时用户还拒绝，则提示他去手机的设置-权限管理界面打开权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            isAllGrant = true;
            for (int grant : grantResults) {
                if (grant == PackageManager.PERMISSION_DENIED) {
                    isAllGrant = false;
                    break;
                }
            }
        }
        if (isAllGrant) {
            Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            doWork();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("应用需要您的通讯录和存储权限，请到设置-权限管理中授权。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);
                        }
                    }).setCancelable(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "您没有获得权限，此功能不能正常使用", Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.create().show();
        }
    }
}
