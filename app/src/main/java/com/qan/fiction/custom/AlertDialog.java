package com.qan.fiction.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import static com.qan.fiction.util.constants.Conversion.dp;

public class AlertDialog {
    public android.app.AlertDialog a;

    public AlertDialog(Object o) {
        if (o instanceof android.app.AlertDialog)
            a = (android.app.AlertDialog) o;
    }

    public void show() {
        a.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        a.show();
    }

    public void setView(View v, int a, int b, int c, int d) {
        if (v == null)
            return;
        Context e = v.getContext();
        this.a.setView(v, dp(e, a), dp(e, b), dp(e, c), dp(e, d));
    }

    public void cancel() {
        a.cancel();
    }

    public void setMessage(CharSequence message) {
        a.setMessage(message);
    }

    public void setCancelOperation(final Runnable c) {
        a.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                c.run();
            }
        });
    }
}
