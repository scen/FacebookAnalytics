package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

public class FBLoginButton extends Button {
	
	public FBLoginButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setGravity(Gravity.CENTER);
        this.setTextColor(getResources().getColor(R.color.com_facebook_loginview_text_color));
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.com_facebook_loginview_text_size));
        this.setTypeface(Typeface.DEFAULT_BOLD);
        if (isInEditMode()) {
            // cannot use a drawable in edit mode, so setting the background color instead
            // of a background resource.
            this.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
            // hardcoding in edit mode as getResources().getString() doesn't seem to work in IntelliJ
        } else {
            this.setBackgroundResource(R.drawable.com_facebook_button_blue);
            this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
            this.setCompoundDrawablePadding(
                    getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
            this.setPadding(getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
                    getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
                    getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
                    getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));
        }
        setText("Log in with Facebook");
	}

}
