package com.avilaksh.earningo;

public class NotificationItem {

    String tittle;
    String body;
    String imageUrl;

    public NotificationItem(String tittle, String body, String imageUrl) {
        this.tittle = tittle;
        this.body = body;
        this.imageUrl = imageUrl;
    }
    public NotificationItem() {

    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "NotificationItem{" +
                "tittle='" + tittle + '\'' +
                ", body='" + body + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

//    public static String[] tittle = new String[]{
//            "Offer of the day", "Spin time"
//
//    };
//    public static String[] body = new String[]{
//            "Earn Extra vedio bonus", "Today spin 10 time a day"
//
//    };

}
