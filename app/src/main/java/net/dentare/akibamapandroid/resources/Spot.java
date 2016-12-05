package net.dentare.akibamapandroid.resources;

public class Spot {
    private String name;
    private String address;
    private String detail;
    private Float lat;
    private Float lng;
    private int categoryId;
    private int userId;

    public Spot(String name, String address, String detail, Float lat, Float lng, int categoryId, int userId) {
        this.name = name;
        this.address = address;
        this.detail = detail;
        this.lat = lat;
        this.lng = lng;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
