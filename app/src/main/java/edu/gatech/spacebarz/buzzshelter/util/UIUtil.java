package edu.gatech.spacebarz.buzzshelter.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UIUtil {
    public static void closeSoftKeyboard(View v, Context c) {
        if (v != null) {
            InputMethodManager im = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null)
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
