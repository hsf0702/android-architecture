package com.klfront.iprogram.blog;

import android.view.View;

import com.klfront.baseui.fragment.BaseFragmentV4;
import com.klfront.iprogram.R;

import org.jetbrains.annotations.NotNull;

public class BlogFragment extends BaseFragmentV4 {

    public BlogFragment() {
        // Required empty public constructor
    }
    @NotNull
    @Override
    public String getTitle() {
        return getString(R.string.blog);
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_blog;
    }

    @Override
    public void initControls(@NotNull View view) {

    }
}
