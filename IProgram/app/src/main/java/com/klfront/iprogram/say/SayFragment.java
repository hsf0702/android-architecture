package com.klfront.iprogram.say;

import android.view.View;

import com.klfront.baseui.fragment.BaseFragmentV4;
import com.klfront.iprogram.R;

import org.jetbrains.annotations.NotNull;

public class SayFragment extends BaseFragmentV4 {

    public SayFragment() {
        // Required empty public constructor
    }
    @NotNull
    @Override
    public String getTitle() {
        return getString(R.string.say);
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_say;
    }

    @Override
    public void initControls(@NotNull View view) {

    }
}
