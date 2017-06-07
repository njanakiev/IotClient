package com.nikolaijanakiev.iotclient;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikolai on 05.06.2015.
 */
public class InformationDisplay {

    private TextView textView;
    private List<String> informationList;
    private int size;

    public InformationDisplay(TextView textView, int size){
        this.textView = textView;
        this.informationList = new ArrayList<>();
        this.size = size;
    }

    public void update(String... information) {
        for(int i = 0; i < information.length; i++){
            if (informationList.size() >= size) {
                informationList.remove(0);
            }
            informationList.add(information[i]);
        }

        StringBuilder builder = new StringBuilder();
        for (String line : informationList) {
            builder.append(line);
            builder.append("\n");
        }
        textView.setText(builder.toString());
    }
}
