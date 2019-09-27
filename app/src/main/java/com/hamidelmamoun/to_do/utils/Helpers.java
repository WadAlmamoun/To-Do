package com.hamidelmamoun.to_do.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener;
import com.hamidelmamoun.to_do.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helpers {

    public static String EpochToDateString(String epochSeconds, String formatString) {
        Date updatedate = new Date(Long.parseLong(epochSeconds));
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);
    }


    public static Calendar EpochToCalender(String epochSeconds) {
        Date updatedate = new Date(Long.parseLong(epochSeconds));
        Calendar cal = Calendar.getInstance();
        cal.setTime(updatedate);

        return cal;
    }

        public static BubbleShowCaseBuilder addMultipleShowCases(View view, final Activity activity, String title, String description, final Class openAfterActivity, BubbleShowCase.ArrowPosition arrowPosition){
        return new BubbleShowCaseBuilder(activity)
                .title(title) //Any title for the bubble view
                .description(description)
                .arrowPosition(arrowPosition) //You can force the position of the arrow to change the location of the bubble.
                .backgroundColor(activity.getResources().getColor(R.color.colorAccent)) //Bubble background color
                .textColor(Color.WHITE) //Bubble Text color
                .titleTextSize(17) //Title text size in SP (default value 16sp)
                .descriptionTextSize(15) //Subtitle text size in SP (default value 14sp)
//                .image(activity.getDrawable(R.drawable.background_grad)) //Bubble main image
                .closeActionImage(activity.getDrawable(R.drawable.ic_close)) //Custom close action image
                .listener(new BubbleShowCaseListener() {
                    @Override
                    public void onTargetClick(BubbleShowCase bubbleShowCase) {

                    }

                    @Override
                    public void onCloseActionImageClick(BubbleShowCase bubbleShowCase) {
                        if(openAfterActivity != null)
                            activity.startActivity(new Intent(activity,openAfterActivity));
                    }

                    @Override
                    public void onBackgroundDimClick(BubbleShowCase bubbleShowCase) {
                        bubbleShowCase.dismiss();
                        if(openAfterActivity != null)
                            activity.startActivity(new Intent(activity,openAfterActivity));
                    }

                    @Override
                    public void onBubbleClick(BubbleShowCase bubbleShowCase) {

                    }
                })
//                .showOnce("BUBBLE_SHOW_CASE_ID2") //Id to show only once the BubbleShowCase
                .targetView(view); //View to point out
    }
}
