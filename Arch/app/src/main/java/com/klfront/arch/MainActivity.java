package com.klfront.arch;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klfront.httputils.HttpEngineFactory;
import com.klfront.httputils.interfaces.IHttpEngine;
import com.klfront.httputils.interfaces.ResultCallback;
import com.linxiao.framework.toast.ToastAlert;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ///把adapter的生成放在下载完数据之后，否则一开始getitemCount=0 导致不显示。
    ActicleAdapter adapter;
    RecyclerView recyclerView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IHttpEngine engine = HttpEngineFactory.getHttpEngine(this,BuildConfig.HttpEngine);
        if(engine ==null)
        {
            ToastAlert.create("IHttpEngine 初始化错误");
            return;
        }

        engine.getAsyn("http://iprogram.com.cn/api/ArticleData", new ResultCallback() {
            @Override
            public void onError(String requestUrl, Exception e) {
                Toast.makeText(MainActivity.this,String.format("%s 请求错误：%s",requestUrl, e.getMessage()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String responseBody, Exception e) {
                //Toast.makeText(MainActivity.this,responseBody, Toast.LENGTH_SHORT).show();
                if(e==null) {
                    List<Article> list = new Gson().fromJson(responseBody, new TypeToken<List<Article>>() {
                    }.getType());
                    adapter = new ActicleAdapter(MainActivity.this, list);
                    //以下行必须 否则可能无法显示数据
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else{

                }
            }
        });
    }

    static class ActicleAdapter extends RecyclerView.Adapter<ActicleAdapter.ViewHolder> {

        private List<Article> mList = new ArrayList<>();
        private Context context;
        private LayoutInflater inflater;
        public ActicleAdapter(Context context, List<Article> list) {
            this.mList = list;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //只显示1行数据
           //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);

            //显示完整数据
            View view = inflater.inflate(R.layout.item_article, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Article art = mList.get(position);
            holder.tvTitle.setText(art.Title);
            holder.tvContent.setText(art.Content);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            TextView tvContent;

            public ViewHolder(View view) {
                super(view);
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvContent = (TextView) view.findViewById(R.id.tv_content);
            }
        }
    }



    static class Article {
        int Id;
        String Title;
        String Keywords;
        String Content;
        String Text;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
