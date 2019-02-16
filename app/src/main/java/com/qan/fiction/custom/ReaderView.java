package com.qan.fiction.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.util.constants.Settings;

public class ReaderView extends TextView {


    public ReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ReaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet as) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts.ttf");
        setTypeface(tf);
        TypedArray ta = context.obtainStyledAttributes(as, R.styleable.ReaderView);
        if (!ta.getBoolean(R.styleable.ReaderView_font_manual, false)) {
            int id = Settings.getTextDimension(context);
            setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(id));
        }
    }

    public int getOffset(int y) {
        int line = getLayout().getLineForVertical(y);
        return getLayout().getLineStart(line);
    }

    public int getLine(int offset) {
        int line = getLayout().getLineForOffset(offset);
        return getLayout().getLineTop(line);
    }

}
