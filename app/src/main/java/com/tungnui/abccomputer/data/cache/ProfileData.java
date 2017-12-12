package com.tungnui.abccomputer.data.cache;

import android.content.Context;

import com.tungnui.abccomputer.data.preference.AppPreference;
import com.tungnui.abccomputer.data.preference.PrefKey;
import com.tungnui.abccomputer.model.UserModel;

/**
 * Created by Nasir on 6/4/17.
 */
public class ProfileData {

    public static void storeProfileData(Context mContext, String customerId, String firstName, String lastName, String emailId, String userName, String photoPath) {

        AppPreference.getInstance(mContext).setString(PrefKey.CUSTOMER_ID, customerId);
        AppPreference.getInstance(mContext).setString(PrefKey.FIRST_NAME, firstName);
        AppPreference.getInstance(mContext).setString(PrefKey.LAST_NAME,  lastName);
        AppPreference.getInstance(mContext).setString(PrefKey.EMAIL,      emailId);
        AppPreference.getInstance(mContext).setString(PrefKey.USER_NAME,  userName);
        AppPreference.getInstance(mContext).setString(PrefKey.USER_AVATAR_URL, photoPath);

    }

    public static UserModel getProfileInfo(Context mContext) {

        String firstName = AppPreference.getInstance(mContext).getString(PrefKey.FIRST_NAME);
        String lastName = AppPreference.getInstance(mContext).getString(PrefKey.LAST_NAME);
        String emailId = AppPreference.getInstance(mContext).getString(PrefKey.EMAIL);
        String userName = AppPreference.getInstance(mContext).getString(PrefKey.USER_NAME);
        String photoPath = AppPreference.getInstance(mContext).getString(PrefKey.AVATAR_URL);

        return new UserModel(firstName, lastName, emailId, userName, photoPath);
    }

}
