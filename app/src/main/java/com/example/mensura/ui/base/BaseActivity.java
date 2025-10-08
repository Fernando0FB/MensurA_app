package com.example.mensura.ui.base;


import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private FrameLayout loadingOverlay;

    protected void setLoadingOverlay(FrameLayout overlay) {
        this.loadingOverlay = overlay;
    }

    protected void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    protected void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }
}