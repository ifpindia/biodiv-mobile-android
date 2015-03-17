package com.mobisys.android.ibp.widget;


import com.mobisys.android.ibp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableLayout extends RelativeLayout implements Checkable {
    private boolean mChecked;
    private Context mContext;
    
    public CheckableLayout(Context context) {
        super(context);
        mContext=context;
    }

    public CheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    
	public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundColor(mChecked?mContext.getResources().getColor(R.color.light_gray):mContext.getResources().getColor(android.R.color.transparent));
       // setBackgroundDrawable(mChecked?mContext.getResources().getDrawable(R.drawable.list_item_bg):new ColorDrawable(0));
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

}
