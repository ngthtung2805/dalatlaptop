package com.tungnui.abccomputer.model;

/**
 * Created by Ashiq on 7/27/16.
 */
public class NotificationModel {

    public int id;
    public String notificationType;
    public String title;
    public String message;
    public int productId;
    public String url;
    public boolean isRead;

    public NotificationModel(int id, String notificationType, String title, String message, int productId, String url, boolean isRead) {
        this.id = id;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.productId = productId;
        this.url = url;
        this.isRead = isRead;
    }
}
