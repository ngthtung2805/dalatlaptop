package com.tungnui.dalatlaptop.ux.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.ux.MainActivity
import timber.log.Timber

/**
 * Fragment provides options to editing user information and password change.
 */
class AccountEditFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    /**
     * Indicate which fort is active.
     */
    private var isPasswordForm = false

    // Account editing form
    private var accountForm: LinearLayout? = null
    private var nameInputWrapper: TextInputLayout? = null
    private var streetInputWrapper: TextInputLayout? = null
    private var houseNumberInputWrapper: TextInputLayout? = null
    private var cityInputWrapper: TextInputLayout? = null
    private var zipInputWrapper: TextInputLayout? = null
    private var phoneInputWrapper: TextInputLayout? = null
    private var emailInputWrapper: TextInputLayout? = null

    // Password change form
    private var passwordForm: LinearLayout? = null
    private var currentPasswordWrapper: TextInputLayout? = null
    private var newPasswordWrapper: TextInputLayout? = null
    private var newPasswordAgainWrapper: TextInputLayout? = null

   /* private val userFromView: User
        get() {
            val user = User()
            user.name = Utils.getTextFromInputLayout(nameInputWrapper)
            user.street = Utils.getTextFromInputLayout(streetInputWrapper)
            user.houseNumber = Utils.getTextFromInputLayout(houseNumberInputWrapper)
            user.city = Utils.getTextFromInputLayout(cityInputWrapper)
            user.zip = Utils.getTextFromInputLayout(zipInputWrapper)
            user.phone = Utils.getTextFromInputLayout(phoneInputWrapper)
            return user
        }
*/
    /**
     * Check if all input fields are filled.
     * Method highlights all unfilled input fields.
     *
     * @return true if everything is Ok.
     */
    private// Check and show all missing values
    val isRequiredFields: Boolean
        get() {
            val fieldRequired = getString(R.string.Required_field)
            val nameCheck = Utils.checkTextInputLayoutValueRequirement(nameInputWrapper, fieldRequired)
            val streetCheck = Utils.checkTextInputLayoutValueRequirement(streetInputWrapper, fieldRequired)
            val houseNumberCheck = Utils.checkTextInputLayoutValueRequirement(houseNumberInputWrapper, fieldRequired)
            val cityCheck = Utils.checkTextInputLayoutValueRequirement(cityInputWrapper, fieldRequired)
            val zipCheck = Utils.checkTextInputLayoutValueRequirement(zipInputWrapper, fieldRequired)
            val phoneCheck = Utils.checkTextInputLayoutValueRequirement(phoneInputWrapper, fieldRequired)
            val emailCheck = Utils.checkTextInputLayoutValueRequirement(emailInputWrapper, fieldRequired)

            return nameCheck && streetCheck && houseNumberCheck && cityCheck && zipCheck && phoneCheck && emailCheck
        }

    /**
     * Check if all input password fields are filled and entries for new password matches.
     *
     * @return true if everything is Ok.
     */
    private val isRequiredPasswordFields: Boolean
        get() {
            val fieldRequired = getString(R.string.Required_field)
            val currentCheck = Utils.checkTextInputLayoutValueRequirement(currentPasswordWrapper, fieldRequired)
            val newCheck = Utils.checkTextInputLayoutValueRequirement(newPasswordWrapper, fieldRequired)
            val newAgainCheck = Utils.checkTextInputLayoutValueRequirement(newPasswordAgainWrapper, fieldRequired)

            if (newCheck && newAgainCheck) {
                if (Utils.getTextFromInputLayout(newPasswordWrapper) != Utils.getTextFromInputLayout(newPasswordAgainWrapper)) {
                    Timber.d("The entries for the new password must match")
                    newPasswordWrapper!!.isErrorEnabled = true
                    newPasswordAgainWrapper!!.isErrorEnabled = true
                    newPasswordWrapper!!.error = getString(R.string.The_entries_must_match)
                    newPasswordAgainWrapper!!.error = getString(R.string.The_entries_must_match)
                    return false
                } else {
                    newPasswordWrapper!!.isErrorEnabled = false
                    newPasswordAgainWrapper!!.isErrorEnabled = false
                }
            }
            return currentCheck && newCheck && newAgainCheck
        }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("%s - OnCreateView", this.javaClass.simpleName)
        MainActivity.setActionBarTitle(getString(R.string.Account))

        val view = inflater!!.inflate(R.layout.fragment_account_edit, container, false)

        progressDialog = Utils.generateProgressDialog(activity, false)

        // Account details form
        accountForm = view.findViewById<View>(R.id.account_edit_form) as LinearLayout

        nameInputWrapper = view.findViewById<View>(R.id.account_edit_name_wrapper) as TextInputLayout
        streetInputWrapper = view.findViewById<View>(R.id.account_edit_street_wrapper) as TextInputLayout
        houseNumberInputWrapper = view.findViewById<View>(R.id.account_edit_house_number_wrapper) as TextInputLayout
        cityInputWrapper = view.findViewById<View>(R.id.account_edit_city_wrapper) as TextInputLayout
        zipInputWrapper = view.findViewById<View>(R.id.account_edit_zip_wrapper) as TextInputLayout
        phoneInputWrapper = view.findViewById<View>(R.id.account_edit_phone_wrapper) as TextInputLayout
        emailInputWrapper = view.findViewById<View>(R.id.account_edit_email_wrapper) as TextInputLayout

        // Password form
        passwordForm = view.findViewById<View>(R.id.account_edit_password_form) as LinearLayout
        currentPasswordWrapper = view.findViewById<View>(R.id.account_edit_password_current_wrapper) as TextInputLayout
        newPasswordWrapper = view.findViewById<View>(R.id.account_edit_password_new_wrapper) as TextInputLayout
        newPasswordAgainWrapper = view.findViewById<View>(R.id.account_edit_password_new_again_wrapper) as TextInputLayout

        val btnChangePassword = view.findViewById<View>(R.id.account_edit_change_form_btn) as Button
        btnChangePassword.setOnClickListener {
            if (isPasswordForm) {
                isPasswordForm = false
                passwordForm!!.visibility = View.GONE
                accountForm!!.visibility = View.VISIBLE
                btnChangePassword.text = getString(R.string.Change_password)
            } else {
                isPasswordForm = true
                passwordForm!!.visibility = View.VISIBLE
                accountForm!!.visibility = View.GONE
                btnChangePassword.setText(R.string.Cancel)
            }
        }

        // Fill user informations
        /*  User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {
            refreshScreen(activeUser);
            Timber.d("user: %s", activeUser.toString());
        } else {
            Timber.e(new RuntimeException(), "No active user. Shouldn't happen.");
        }

        Button confirmButton = (Button) view.findViewById(R.id.account_edit_confirm_button);
        confirmButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!isPasswordForm) {
                    try {
                        User user = getUserFromView();
                        putUser(user);
                    } catch (Exception e) {
                        Timber.e(e, "Update user information exception.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    }
                } else {
                    changePassword();
                }
                // Remove soft keyboard
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }
        });*/
        return view
    }

    override fun onPause() {
        if (activity.currentFocus != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
        super.onPause()
    }

   /* private fun refreshScreen(user: User) {
        Utils.setTextToInputLayout(nameInputWrapper, user.name)
        Utils.setTextToInputLayout(streetInputWrapper, user.street)
        Utils.setTextToInputLayout(houseNumberInputWrapper, user.houseNumber)
        Utils.setTextToInputLayout(cityInputWrapper, user.city)
        Utils.setTextToInputLayout(zipInputWrapper, user.zip)
        Utils.setTextToInputLayout(emailInputWrapper, user.email)
        Utils.setTextToInputLayout(phoneInputWrapper, user.phone)
    }*/

    /**
     * Volley request for update user details.
     *
     * @param user new user data.
     */
 /*   private fun putUser(user: User) {
        if (isRequiredFields) {
            /*  User activeUser = SettingsMy.getActiveUser();
            if (activeUser != null) {
                JSONObject joUser = new JSONObject();
                try {
                    joUser.put(JsonUtils.TAG_NAME, user.getName());
                    joUser.put(JsonUtils.TAG_STREET, user.getStreet());
                    joUser.put(JsonUtils.TAG_HOUSE_NUMBER, user.getHouseNumber());
                    joUser.put(JsonUtils.TAG_CITY, user.getCity());
                    joUser.put(JsonUtils.TAG_ZIP, user.getZip());
                    joUser.put(JsonUtils.TAG_EMAIL, user.getEmail());
                    joUser.put(JsonUtils.TAG_PHONE, user.getPhone());
                } catch (JSONException e) {
                    Timber.e(e, "Parse new user registration exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    return;
                }

                String url = String.format(EndPoints.USER_SINGLE, SettingsMy.getActualNonNullShop(getActivity()).getId(), activeUser.getId());

                progressDialog.show();
                GsonRequest<User> req = new GsonRequest<>(Request.Method.PUT, url, joUser.toString(), User.class,
                        new Response.Listener<User>() {
                            @Override
                            public void onResponse(@NonNull User user) {
                                SettingsMy.setActiveUser(user);
                                refreshScreen(user);
                                progressDialog.cancel();
                                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                                getFragmentManager().popBackStackImmediate();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) progressDialog.cancel();
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }, getFragmentManager(), activeUser.getAccessToken());
                req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                req.setShouldCache(false);
                MyApplication.getInstance().addToRequestQueue(req, CONST.ACCOUNT_EDIT_REQUESTS_TAG);
            } else {
                LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
            }*/
        } else {
            Timber.d("Missing required fields.")
        }
    }*/

    /**
     * Updates the user's password. Before the request is sent, the input fields are checked for valid values.
     */
    private fun changePassword() {
        if (isRequiredPasswordFields) {
            /*   User user = SettingsMy.getActiveUser();
            if (user != null) {
                String url = String.format(EndPoints.USER_CHANGE_PASSWORD, SettingsMy.getActualNonNullShop(getActivity()).getId(), user.getId());

                JSONObject jo = new JSONObject();
                try {
                    jo.put(JsonUtils.TAG_OLD_PASSWORD, Utils.getTextFromInputLayout(currentPasswordWrapper).trim());
                    jo.put(JsonUtils.TAG_NEW_PASSWORD, Utils.getTextFromInputLayout(newPasswordWrapper).trim());
                    Utils.setTextToInputLayout(currentPasswordWrapper, "");
                    Utils.setTextToInputLayout(newPasswordWrapper, "");
                    Utils.setTextToInputLayout(newPasswordAgainWrapper, "");
                } catch (JSONException e) {
                    Timber.e(e, "Parsing change password exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    return;
                }

                progressDialog.show();
                JsonRequest req = new JsonRequest(Request.Method.PUT, url, jo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Timber.d("Change password successful: %s", response.toString());
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                        if (progressDialog != null) progressDialog.cancel();
                        getFragmentManager().popBackStackImmediate();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) progressDialog.cancel();
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }, getFragmentManager(), user.getAccessToken());

                req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                req.setShouldCache(false);
                MyApplication.getInstance().addToRequestQueue(req, CONST.ACCOUNT_EDIT_REQUESTS_TAG);
            } else {
                LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
            }*/
        }
    }

    override fun onStop() {
        if (progressDialog != null) progressDialog!!.cancel()
        super.onStop()
    }
}
