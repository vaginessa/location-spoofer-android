package com.emmanuelcorrales.locationspoofer.utils;


import android.widget.EditText;

public class EditTextUtils {

    private EditTextUtils() {
        //EditTextUtils is a singleton utility class and should not be instantiated.
    }

    public static boolean validateEmpty(EditText editText, int msgId) {
        return validateEmpty(editText, editText.getContext().getString(msgId));
    }

    public static boolean validateEmpty(EditText editText, String msg) {
        if (editText == null) {
            throw new IllegalArgumentException("Argument 'editText' cannot be null.");
        }

        if (editText.getText().toString().isEmpty()) {
            editText.setError(msg);
            return false;
        }
        return true;
    }
}
