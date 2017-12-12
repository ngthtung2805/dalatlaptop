package com.tungnui.abccomputer.data.constant;

/**
 * Created by Nasir on 5/24/17.
 */

public class AppConstants {
    public static final int REQUEST_NONE = 0;
    // Integer constants
    public static final int VALUE_ZERO = 0;
    public static final int INDEX_ZERO = 0;
    public static final String EMPTY_STRING = "";
    public static final String COMMA = ",";
    public static final int VALUE_SELECTED = 1;
    public static final int VALUE_NOT_SELECTED = 0;
    public static final int PASSWORD_MIN_CHAR_LIMIT = 6;
    public static final String CURRENCY = "VNĐ";
    public static final String ORDER_STATUS_PENDING = "pending";
    public static final String ORDER_STATUS_COMPLETED = "completed";
    public static final String ORDER_STATUS_CANCELLED ="cancelled";
    public static final String ORDER_STATUS_FAILED = "failed";
    public static final String ORDER_STATUS_ONHOLD = "on-hold";
    public static final String ORDER_STATUS_REFUNDED = "refunded";
    public static final String ORDER_STATUS_PROCESSING = "processing";
    // ISO 8601 date time format
    public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    // Number of days for pick recent products before
    public static final int NUMBER_OF_DAYS_BEFORE = 30; // @Todo: need to do it value 7

    // search page key
    public static final int INITIAL_PAGE_NUMBER = 1;
    public static final int DEFAULT_PER_PAGE = 10;
    public static final int MAX_PER_PAGE = 100;
    public static final int MAX_POPULAR = 20;
    public static final String SEARCH_KEY = "searchKey";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_ASC = "asc";
    public static final String KEY_DESC = "desc";

    // authentication
    public static final String DEFAULT_COUNTRY_ALPHA = "BD";
    public static final String DEFAULT_PASSWORD = "1";

    // categories ID
    public static final int CATEGORY_TEC_PRODUCTS_ID = 21;

    // String constants
    public static final String PRODUCT_ID = "productId";
    public static final String PAGE_TITLE = "pageTitle";
    public static final String PAGE_TYPE = "pageType";
    public static final String ORDER_ID = "orderId";
    public static final int REQUEST_UPDATE_ADDRESS_CODE = 20;
    public static final int
            TYPE_FEATURED = 1,
            TYPE_RECENT = 2,
            TYPE_POPULAR = 3,
            TYPE_CATEGORY = 4;

    public static final int NO_CATEGORY = -1;


    public static final String REGISTRATION_ERROR_EMAIL_EXIST = "registration-error-email-exists";
    public static final String REGISTRATION_ERROR_USER_EXIST = "registration-error-username-exists";
    public static final String REGISTRATION_MISSING_PARAMS = "rest_missing_callback_param";
    public static final String REGISTRATION_ERROR_ALREADY_TRASHED = "woocommerce_rest_already_trashed";
    public static final String LOGIN_INVALID_ID = "woocommerce_rest_invalid_id";
    public static final String LOGIN_CUSTOMER_INVALID_ARGUMENT = "woocommerce_rest_customer_invalid_argument"; //Username isn't editable

    // profile photo
    public static final int MENU_ITEM_CAMERA    = 1;
    public static final int MENU_ITEM_GALLERY   = 2;
    public static final int CAMERA_REQUEST_CODE = 3;
    public static final int FILE_REQUEST_CODE   = 4;
    public static final String ESHOPPER_FILE_NAME = "EShopper_profile_photo";

    public static final String KEY_LINE_ITEM_LIST = "lineItemList";
    public static final String KEY_EDIT_ONLY = "editOnly";
    public static final String KEY_LOGIN_ORDER = "loginOrder";
    public static final String KEY_ORDER_DETAIL ="orderDetail";

    // Large image view
    public static final String KEY_IMAGE_URL = "large_image_url";
    public static final String KEY_PERMISSION = "perm";

    // order list
    public static final String[] orderTitles = {"Order by", "Order by title", "Order by date"};
    public static final String[] orderValues = {"title", "title", "date"};

    // product attributes
    public static final String ATTRIBUTE_SIZE = "size";

    // Notification keys
    public static final String BUNDLE_KEY_TITLE = "title";
    public static final String BUNDLE_KEY_MESSAGE = "message";
    public static final String BUNDLE_KEY_URL = "url";

    // Notification type
    public static final String NOTIFY_TYPE_MESSAGE = "message";
    public static final String NOTIFY_TYPE_PRODUCT = "product";
    public static final String NOTIFY_TYPE_URL     = "webpage";

    public static final String CALL_NUMBER = "911"; // replace by your support number

    public static final String SMS_NUMBER = "+123456789"; // replace by your support sms number
    public static final String SMS_TEXT = "Send your feedback to improve our service..."; // replace by your message

    public static final String EMAIL_ADDRESS = "products.mcc@gmail.com"; // replace by your support sms number
    public static final String EMAIL_SUBJECT = "Feedback"; // replace by your message
    public static final String EMAIL_BODY = "Send your feedback to improve our service..."; // replace by your message

    public static final int HOME_ITEM_MAX = 6;
    public static final String PAYMENT_METHOD ="payment_method";
    public static final String SHIPPING_METHOD ="shipping_method";
    public static final String CATEGORY_ID ="category_id";
    public static final String CATEGORY_NAME ="category_name";
    public static final String SEARCH_QUERY ="search_query";
    public static final String TYPE ="type";
    public static  final  String NAME="name";
    public static  final String EMAIL="email";
}
