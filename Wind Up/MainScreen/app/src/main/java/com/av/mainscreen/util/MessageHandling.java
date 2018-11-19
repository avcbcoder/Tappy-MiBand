package com.av.mainscreen.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import com.av.mainscreen.broadcastReceiver.CallReceiver;
import com.av.mainscreen.constants.SETTINGS;

import java.util.ArrayList;

/**
 * Created by Ankit on 18-11-2018.
 */

public class MessageHandling {
    private static final String TAG = "MessageHandling";
    private static final int MAX_CONTACT_LENGTH = 9;

    public static void sendMessage(String phoneNumber, String text) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                phoneNumber,
                null,
                text,
                null, null
        );
        //ArrayList<String> parts = smsManager.divideMessage(text);
        //smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
    }

    public static String extractName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) contactName = cursor.getString(0);
            cursor.close();
        }

        if (contactName.length() == 0)
            return phoneNumber;
        else {
            Log.e(TAG, "extractName: " + contactName);
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < contactName.length() && i < MAX_CONTACT_LENGTH; i++) {
                int ch = contactName.charAt(i);
                if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9'))
                    sb.append((char)ch);
            }
            return sb.toString();
        }
    }
}
