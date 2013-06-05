package com.ericrgon;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ericrgon on 6/4/13.
 */
public class GarageActivity extends CloudBackendActivity {

    private Button mBuzzButton;
    private TextView mView;
    private TextView mState;

    private List<CloudEntity> logList = new LinkedList<CloudEntity>();

    private static final String DOCUMENT_NAME = "Buzz";

    private static final String OPEN = "open";
    private static final String CLOSED = "closed";

    private enum States {OPEN,CLOSED};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBuzzButton = (Button) findViewById(R.id.buzz);
        mView = (TextView) findViewById(R.id.logs);
        mState = (TextView) findViewById(R.id.status);
    }

    @Override
    protected void onPostCreate() {
        super.onPostCreate();

        CloudCallbackHandler<List<CloudEntity>> handler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                logList = results;
                updateLogs();
            }
        };

        getCloudBackend().listByKind(DOCUMENT_NAME,CloudEntity.PROP_CREATED_AT, CloudQuery.Order.DESC,15,
                CloudQuery.Scope.FUTURE_AND_PAST,handler);
    }

    public void onBuzzButtonPressed(View view) {
        CloudEntity newBuzz = new CloudEntity(DOCUMENT_NAME);

        CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
            @Override
            public void onComplete(CloudEntity results) {
                //Save result and display
                logList.add(0,results);
                updateLogs();
                mBuzzButton.setEnabled(true);
            }
        };

        getCloudBackend().insert(newBuzz,handler);

        mBuzzButton.setEnabled(false);
    }

    private void updateLogs() {
        StringBuilder stringBuilder = new StringBuilder();
        for(CloudEntity entity : logList){
            stringBuilder.append(entity.getCreatedAt() + "\n");
        }
        mView.setText(stringBuilder.toString());
    }

}
