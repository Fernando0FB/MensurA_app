package com.example.mensura.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mensura.util.Keyboard.KeyboardUtils;
import com.example.myapplication.R;

public abstract class BaseActivity extends AppCompatActivity {
    private KeyboardUtils keyboardUtils = new KeyboardUtils();
    private FrameLayout loadingOverlay;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        View v = findViewById(R.id.loadingOverlay);
        if (v instanceof FrameLayout) {
            loadingOverlay = (FrameLayout) v;
        }
    }

    // Mantém o setter se quiser setar manualmente
    protected void setLoadingOverlay(FrameLayout overlay) { this.loadingOverlay = overlay; }

    public void showLoading(Activity activity) {
        if (keyboardUtils.isKeyboardVisible(activity)) {
            keyboardUtils.hideKeyboard(activity);
        }
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }
    public void hideLoading() { if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE); }
}
