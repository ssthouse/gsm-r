package com.xunce.gsmr.view.activity.gaode;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.xunce.gsmr.R;

/**
 * Created by ssthouse on 2015/9/14.
 */
public class GaodePrjEditActivity extends GaodeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_prj_edit_gaode);
        super.init(savedInstanceState);

        initView();
    }


    private void initView(){
        Switch sw = (Switch) findViewById(R.id.id_sw_locate);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    GaodePrjEditActivity.super.showLocate();
                }else {
                    GaodePrjEditActivity.super.hideLocate();
                }
            }
        });
    }
}
