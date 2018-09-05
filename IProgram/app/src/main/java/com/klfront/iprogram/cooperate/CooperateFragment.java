package com.klfront.iprogram.cooperate;

import android.view.View;

import com.klfront.baseui.fragment.BaseFragmentV4;
import com.klfront.iprogram.R;

import org.jetbrains.annotations.NotNull;

public class CooperateFragment extends BaseFragmentV4 {

    public CooperateFragment(){}
    @NotNull
    @Override
    public String getTitle() {
        return getString(R.string.cooperate);
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_cooperate;
    }

    @Override
    public void initControls(@NotNull View view) {

    }
}
