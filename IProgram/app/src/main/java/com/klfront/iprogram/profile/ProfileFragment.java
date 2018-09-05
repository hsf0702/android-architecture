package com.klfront.iprogram.profile;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.klfront.iprogram.user.LoginActivity;
import com.klfront.baseui.fragment.BaseFragmentV4;
import com.klfront.dataprovider.Options;
import com.klfront.iprogram.R;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends BaseFragmentV4 {
    private String nickName;
    private String phone;
    private String token;
    private static int REQUEST_CODE_LOGIN = 111;

    public ProfileFragment(){}
    @NotNull
    @Override
    public String getTitle() {
        return getString(R.string.profile);
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void initControls(@NotNull View view) {
        nickName = Options.getInstance(getContext()).getStringParam("nickName");
        phone = Options.getInstance(getContext()).getStringParam("phone");
        token = Options.getInstance(getContext()).getStringParam("token");
        if(!phone.isEmpty()&&token.isEmpty()){
//            (TextView )view.findViewById(R.id.tv_user_name)
        }

        view.findViewById(R.id.pnl_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), LoginActivity.class);
                it.putExtra("phone",phone);
                startActivityForResult(it,REQUEST_CODE_LOGIN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_CODE_LOGIN){
                if(data!=null) {
                    Options.getInstance(getContext()).setStringParam("phone",data.getStringExtra("phone"));
                    Options.getInstance(getContext()).setStringParam("token",data.getStringExtra("token"));
//                    getuserInfo
                }
            }
        }
    }

//    private void getUserInfo(String phone,){
//
//    }

//    private void setUserInfo(){
//
//    }
}
