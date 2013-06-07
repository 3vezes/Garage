package com.ericrgon;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ericrgon.model.LogEntry;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ericrgon on 6/4/13.
 */
public class GarageActivity extends CloudBackendActivity {

    private LinearLayout mBuzzButton;
    private TextView mBuzzButtonText;
    private TextView mState;

    private ActivityLogAdapter mActivityLogAdapter;

    private List<CloudEntity> logList = new LinkedList<CloudEntity>();

    private static final String DOCUMENT_NAME = "Buzz";

    public static final String OPEN = "Open";
    public static final String CLOSED = "Closed";
    private static final String CLOSE = "Close";
    private static final String STATE_KEY = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivityLogAdapter = new ActivityLogAdapter(this,R.layout.list_item);

        mBuzzButton = (LinearLayout) findViewById(R.id.buzzButton);
        mBuzzButtonText = (TextView) mBuzzButton.findViewById(R.id.buzzLabel);

        ListView activityLogList = (ListView) findViewById(R.id.activityList);
        activityLogList.setAdapter(mActivityLogAdapter);

        mState = (TextView) findViewById(R.id.status);
    }

    @Override
    protected void onPostCreate() {
        super.onPostCreate();

        CloudCallbackHandler<List<CloudEntity>> handler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                logList.clear();
                logList.addAll(results);
                updateLogs();
            }
        };

        getCloudBackend().listByKind(DOCUMENT_NAME,CloudEntity.PROP_CREATED_AT, CloudQuery.Order.DESC,5,
                CloudQuery.Scope.FUTURE_AND_PAST,handler);
    }

    public void onBuzzButtonPressed(View view) {
        CloudEntity newBuzz = new CloudEntity(DOCUMENT_NAME);

        //Switch state and commit new message.
        String newState = OPEN.equals(getMostRecentState()) ? CLOSED : OPEN;
        newBuzz.put(STATE_KEY,newState);

        CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
            @Override
            public void onComplete(CloudEntity results) {
                //Save result and display
                logList.add(0,results);
                logList.remove(logList.size() -1);
                updateLogs();
                mBuzzButton.setEnabled(true);
            }
        };

        getCloudBackend().insert(newBuzz,handler);

        mBuzzButton.setEnabled(false);
    }


    private void updateLogs() {

        mActivityLogAdapter.clear();
        //We're skipping index 0 because it's already displayed at the top of the view.
        for(int i = 1 ; i < logList.size(); i++){
            CloudEntity entity = logList.get(i);
            LogEntry entry = new LogEntry(entity.getCreatedBy(),entity.getCreatedAt(), (String) entity.get(STATE_KEY));
            mActivityLogAdapter.add(entry);
        }

        final String mostRecentState = getMostRecentState();

        //Move the controls off screen
        ObjectAnimator stateAnimator = ObjectAnimator.ofFloat(mState,View.TRANSLATION_X,mState.getWidth() * 2);
        stateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        stateAnimator.setDuration(200);
        stateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                //Change the view while it's offscreen.
                mState.setText(mostRecentState);

                if (CLOSED.equals(mostRecentState)) {
                    mState.setBackgroundResource(R.color.purple);
                } else {
                    mState.setBackgroundResource(R.color.blue);
                }

                //Bring the view back.
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mState, View.TRANSLATION_X, mState.getWidth(), 0);
                objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                objectAnimator.setStartDelay(50);
                objectAnimator.setDuration(200);
                objectAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        stateAnimator.start();


        //Change the button text and color for state change.
        if(OPEN.equals(mostRecentState)){
            //Close options
            mBuzzButtonText.setText(CLOSE);
            mBuzzButton.setBackgroundResource(R.color.purple);
        }
        else {
            //Open Options
            mBuzzButtonText.setText(OPEN);
            mBuzzButton.setBackgroundResource(R.color.blue);
        }
    }

    private String getMostRecentState(){
        if(logList.isEmpty()){
            return "Unknown";
        }
        return (String) logList.get(0).get(STATE_KEY);
    }
}
