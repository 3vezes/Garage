package com.ericrgon;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ericrgon on 6/4/13.
 */
public class GarageActivity extends CloudBackendActivity {

    private Button buzzButton;

    private List<CloudEntity> logList = new LinkedList<CloudEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buzzButton = (Button) findViewById(R.id.buzz);
    }


    public void onBuzzButtonPressed(View view) {
        CloudEntity newBuzz = new CloudEntity("Buzz");

        CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
            @Override
            public void onComplete(CloudEntity results) {
                //Save result and display
                logList.add(0,results);

                buzzButton.setEnabled(true);
            }
        };

        getCloudBackend().insert(newBuzz,handler);

        buzzButton.setEnabled(false);
    }
}
