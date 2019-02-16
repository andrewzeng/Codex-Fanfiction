package com.qan.fiction.util.constants;

import android.content.Context;
import android.util.DisplayMetrics;

public class Conversion {

    public static int dp(Context context, int units) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (units * dm.density);
    }

    public static int sp(Context context, int units) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (units * dm.scaledDensity);
    }
}
