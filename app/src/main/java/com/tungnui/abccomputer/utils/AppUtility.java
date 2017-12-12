package com.tungnui.abccomputer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.activity.BaseActivity;
import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.listener.ListDialogActionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Nasir on 5/24/17.
 */

public class AppUtility {

    private static AppUtility mAppUtility = null;

    // create single instance
    public static AppUtility getInstance() {
        if (mAppUtility == null) {
            mAppUtility = new AppUtility();
        }
        return mAppUtility;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void noInternetWarning(View view, final Context context) {
        if (!isNetworkAvailable(context)) {
            Snackbar snackbar = Snackbar.make(view, context.getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(context.getString(R.string.connect), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            snackbar.show();
        }
    }


    // common custom toast method
    public static void showCustomToast(Activity activity, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();

        // Call custom_toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.custom_toast, null);
        TextView tvToastMsg = (TextView) toastRoot.findViewById(R.id.tvToastMsg);
        tvToastMsg.setText(message);
        Toast toast = new Toast(activity);
        // Set layout to toast
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, AppConstants.VALUE_ZERO, AppConstants.VALUE_ZERO);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    // common snack bar bellow different view and necessary message
    public static void showSnackBarMsg(Context context, View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    public static void showSortingAttributesDialog(Activity activity, String title, String[] sortingAttributes, final ListDialogActionListener listDialogActionListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        if (title != null) {
            alertDialogBuilder.setTitle(title);
        }

        alertDialogBuilder.setItems(sortingAttributes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (listDialogActionListener != null) {
                    listDialogActionListener.onItemSelected(position);
                }
            }
        });
        alertDialogBuilder.show();
    }

    public static String getRecentProductDateTime(int numOfBeforeDays) {
        DateFormat dfISO8601 = new SimpleDateFormat(AppConstants.DATE_FORMAT_ISO8601);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -numOfBeforeDays);

        try {
            return dfISO8601.format(calendar.getTime());

        } catch (Exception e) {
            Log.e("TAG", "Error in Parsing Date : " + e.getMessage());
        }
        return null;
    }

    public static void shareApp(Activity activity) {
        try {
            final String appPackageName = activity.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.share_text) + " https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            activity.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rateThisApp(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makePhoneCall(Activity activity, String phoneNumber) {
        if (phoneNumber != null) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (PermissionUtils.isPermissionGranted(activity, PermissionUtils.CALL_PERMISSIONS, PermissionUtils.REQUEST_CALL)) {
                activity.startActivity(callIntent);
            }
        }
    }

    public static void sendSMS(Activity activity, String phoneNumber, String text) {
        if (phoneNumber != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
            intent.putExtra("sms_body", text);
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendEmail(Activity activity, String email, String subject, String body) {
        if (email != null) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email)); // add more email like: , xyz@gmail.com
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);

            // another way
            /*Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.setType("message/rfc822");*/
            try {
                activity.startActivity(Intent.createChooser(intent, "Send mail..."));
                //activity.startActivity(createEmailOnlyChooserIntent(activity.getApplicationContext(), intent, email, "Send mail..."));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void invokeMessengerBot(Activity activity) {
        try {
            if (isPackageInstalled(activity.getApplicationContext(), "com.facebook.orca")) {

                /**
                 * get id of your facebook page from here:
                 * https://findmyfbid.com/
                 *
                 * Suppose your facebook page url is: http://www.facebook.com/hiponcho
                 *
                 * Visit https://findmyfbid.com/ and put your url and click on "Find Numeric Id"
                 * You will get and ID like this: 788720331154519
                 *
                 * Append an extra 'l' (L in lower case) with the number and please bellow
                 * So, final ID: 788720331154519l
                 */

                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging/" + 1429272380516271l))); // replace id
            } else {
                showToast(activity.getApplicationContext(),
                        activity.getApplicationContext().getResources().getString(R.string.install_messenger));
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.facebook.orca")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPackageInstalled(Context context, String packagename) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(packagename, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned showHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


}