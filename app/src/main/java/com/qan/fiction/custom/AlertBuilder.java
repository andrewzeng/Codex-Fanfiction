package com.qan.fiction.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.qan.fiction.R;
import com.qan.fiction.util.constants.Settings;

public class AlertBuilder {

    private final android.app.AlertDialog.Builder a;

    private AlertBuilder(Context c, int theme) {
        a = new android.app.AlertDialog.Builder(c, theme);
    }

    public AlertBuilder(Context c) {
        this(c, Settings.getDialogStyle(c));
    }

    /**
     * Creates a generic Dialog with title and message, and automatically shows it to the screen.
     *
     * @param c         The {@link Context} to be shown in.
     * @param titleId   The title string id.
     * @param messageId The given message's string resource id.
     */
    public AlertBuilder(Context c, int titleId, int messageId) {
        this(c, titleId, messageId, true);
    }

    /**
     * Creates a generic Dialog with title and message.
     *
     * @param c         The {@link Context} to be shown in.
     * @param titleId   The title string id.
     * @param messageId The given message's string resource id.
     * @param show      If {@code show} is true, displays the {@link AlertDialog} generate by {@link #create()} on the screen.
     *                  If false, does nothing.
     */
    public AlertBuilder(Context c, int titleId, int messageId, boolean show) {
        this(c);
        setTitle(titleId);
        setMessage(messageId);
        if (show) {
            setPositiveButton(c.getString(R.string.ok), null);
            create().show();
        }

    }


    public void setTitle(int resId) {

    }

    public void setTitle(CharSequence s) {
    }

    public void setView(View v) {
        a.setView(v);
    }

    public void setMessage(int res) {
        a.setMessage(res);
    }

    public void setSingleChoiceItems(int id, int checked, DialogInterface.OnClickListener listener) {
        a.setSingleChoiceItems(id, checked, listener);
    }

    public void setMessage(CharSequence s) {
        a.setMessage(s);
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        a.setPositiveButton(text, listener);
    }

    public void setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        a.setNegativeButton(text, listener);
    }

    public void setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        a.setNeutralButton(text, listener);
    }

    public void setIcon(int resource) {
        a.setIcon(resource);
    }

    public AlertDialog create() {
        return new AlertDialog(a.create());
    }
}
