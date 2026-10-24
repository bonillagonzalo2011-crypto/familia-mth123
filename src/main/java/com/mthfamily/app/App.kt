package com.mthfamily.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_layout, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 200;

        windowManager.addView(floatingView, params);

        configurarArrastre(floatingView.findViewById(R.id.header));
        configurarBotones();
    }

    private void configurarArrastre(View handle) {
        handle.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - touchX);
                        params.y = initialY + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void configurarBotones() {
        final View panel = floatingView.findViewById(R.id.panel_contenido);
        final Button btnMin = floatingView.findViewById(R.id.btn_minimizar);
        Button btnCerrar = floatingView.findViewById(R.id.btn_cerrar);
        Button btnAccion1 = floatingView.findViewById(R.id.btn_accion1);
        Button btnAccion2 = floatingView.findViewById(R.id.btn_accion2);

        btnMin.setOnClickListener(v -> {
            boolean visible = panel.getVisibility() == View.VISIBLE;
            panel.setVisibility(visible ? View.GONE : View.VISIBLE);
            btnMin.setText(visible ? "+" : "—");
        });

        btnCerrar.setOnClickListener(v -> stopSelf());

        btnAccion1.setOnClickListener(v ->
                Toast.makeText(this, "Acción 1 activada", Toast.LENGTH_SHORT).show());

        btnAccion2.setOnClickListener(v ->
                Toast.makeText(this, "Acción 2 activada", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
        }
    }
}
