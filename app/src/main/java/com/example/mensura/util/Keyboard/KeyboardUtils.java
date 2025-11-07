package com.example.mensura.util.Keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

    public boolean isKeyboardVisible(Activity activity) {
        View rootView = activity.findViewById(android.R.id.content);
        if (rootView == null) return false;

        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);

        int screenHeight = rootView.getRootView().getHeight();
        int keyboardHeight = screenHeight - rect.bottom;

        return keyboardHeight > 100;
    }


    public void hideKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
