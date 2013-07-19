package com.bingzer.android.dbv.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.bingzer.android.dbv.IDatabase;

public class MainActivity extends Activity {

    TextView textView;
    IDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
