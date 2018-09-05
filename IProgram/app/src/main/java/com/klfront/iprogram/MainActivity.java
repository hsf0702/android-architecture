package com.klfront.iprogram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.klfront.appcore.upgrade.UpgradeMgr;
import com.klfront.baseui.activity.BaseActivity;
import com.klfront.baseui.activity.BaseSimpleActivity;
import com.klfront.baseui.fragment.BaseFragmentV4;
import com.klfront.iprogram.blog.BlogFragment;
import com.klfront.iprogram.cooperate.CooperateFragment;
import com.klfront.iprogram.profile.ProfileFragment;
import com.klfront.iprogram.say.SayFragment;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends BaseSimpleActivity {

    final static int REQ_CODE_PERMISSION = 101;
    private TextView tvTitle;
    private ViewPager viewPager;
    private FragmentsAdapter adapter;
    private PageNavigationView navView;
    private NavigationController navCtrl;

    private String[] tabs;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initContentView() {
        initView();
        init();

        // 测试内存泄漏。不会马上弹出提示，运行一段时间会弹出。
//        LeakSingle.getInstance(this).setRetainedTextView(tvTitle);
    }

    @Override
    protected boolean onGoBack() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_PERMISSION) {
            boolean permisionOk = false;
            if (grantResults.length > 0) {
                permisionOk = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permisionOk = false;
//                        if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//                        {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("系统提示")
                                .setMessage("您拒绝了应用必需的权限，程序即将退出！")
                                .setCancelable(false)
                                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setPositiveButton(R.string.to_set, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, 10011);
                                    }
                                }).create().show();
//                        }
                        break;
                    } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    }
                }
            }
            if (permisionOk) {
                initContent();
            }
        } else {
            //Fragment的权限请求
            if (adapter != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    adapter.getItem(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // onDestroy()被调用之后，如果Activity 没有被销毁,手机通知列表中会提示Leak的Activity,桌面会多出一个Leak的图标（刚安装时没有这个图标）。
        // 注意：通知管理中为该应用开启允许通知、列表优先显示、角标和悬浮通知等。有时要延迟几十秒到一分钟才收到通知，有的手机可能长时间收不到通知。
        // 注意：有的手机在Logcat中没有打印出详细的日志。
//        08-05 17:44:47.569 1524-1545/? I/ActivityManager: Displayed com.klfront.iprogram/com.squareup.leakcanary.internal.DisplayLeakActivity: +97ms
//        08-05 17:44:47.983 1524-1545/? I/Timeline: Timeline: Activity_windows_visible id: ActivityRecord{19cf899 u0 com.klfront.iprogram/com.squareup.leakcanary.internal.DisplayLeakActivity t4979} time:27006117
//        08-05 17:45:40.139 16984-16984/com.klfront.iprogram D/LeakCanary: Could not show leak toast, the window token has been canceled
//        android.view.WindowManager$BadTokenException: Unable to add window -- token null is not valid; is your activity running?
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        viewPager = findViewById(R.id.viewpager);
        navView = findViewById(R.id.navView);
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<String>();
            if (getPackageManager().checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                list.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
//            if (getPackageManager().checkPermission("android.permission.READ_CONTACTS", getPackageName()) != PackageManager.PERMISSION_GRANTED)
//            {
//                list.add("android.permission.READ_CONTACTS");
//            }
            if (list.size() > 0) {
                Log.i("requestPermissions", "yes");
                ActivityCompat.requestPermissions(this, list.toArray(new String[0]), REQ_CODE_PERMISSION);
            } else {
                Log.i("requestPermissions", "no");
                initContent();
            }
        } else {
            initContent();
        }
    }

    private void initContent() {
        int[] icons = new int[]{R.drawable.ic_blog, R.drawable.ic_say, R.drawable.ic_cooperate, R.drawable.ic_profile};
        tabs = new String[]{getString(R.string.blog), getString(R.string.say), getString(R.string.cooperate), getString(R.string.profile)};

        List<Fragment> fragments = new ArrayList();
        fragments.add(new BlogFragment());
        fragments.add(Fragment.instantiate(this, SayFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, CooperateFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ProfileFragment.class.getName()));
        adapter = new FragmentsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                ((BaseFragmentV4) adapter.getItem(position)).onActivated();
                navCtrl.setSelect(position);
                tvTitle.setText(tabs[position]);
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });


        PageNavigationView.MaterialBuilder builder = navView.material();
        for (int i = 0; i < adapter.getCount(); i++) {
            builder.addItem(icons[i], tabs[i]);
        }
        navCtrl = builder.build();
        navCtrl.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                viewPager.setCurrentItem(index);
            }

            @Override
            public void onRepeat(int index) {

            }
        });

        tvTitle.setText(tabs[0]);

        checkNewVersion("2.0");
    }

    private void checkNewVersion(String appver) {
//       new UpgradeMgr(this, appver, "更新描述", "http://iprogram.com.cn/Upgrade/klprj.apk").updateApk();
    }

    static class FragmentsAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments = new ArrayList();

        public FragmentsAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments != null) {
                return fragments.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            if (fragments != null) {
                return fragments.size();
            }
            return 0;
        }
    }
}
