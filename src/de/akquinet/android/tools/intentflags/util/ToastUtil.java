package de.akquinet.android.tools.intentflags.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil
{
    public static void showToast(Context context, String message) {
        showToast(context, message, false);
    }

    public static void showErrorToast(Context context, String message) {
        showToast(context, message, true);
    }

    private static void showToast(Context context, String message,
            boolean errorToast) {
        Toast toast;

        if (errorToast) {
            /*
             * We want the toast text to be red. The exact layout of the default
             * toast is not specified, so the following may not work in future
             * android versions. Thus, if the layout is not the way we expect,
             * we just leave the text color as is.
             */
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            View view = toast.getView();
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;
                if (linearLayout.getChildCount() > 0) {
                    View child = linearLayout.getChildAt(0);
                    if (child instanceof TextView) {
                        ((TextView) child).setTextColor(
                                Color.rgb(255, 100, 100));
                    }
                }
            }
        }
        else {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }

        toast.show();
    }
}
