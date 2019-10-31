package com.example.parkchanjin.lecture_registration;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;


/**
 * Created by parkchanjin on 08/04/2019.
 */

public class Pop extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout( (int)(width*0.9), (int)(height*0.85));



    }
}
