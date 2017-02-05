package net.dentare.akibamapandroid.resources;

import java.util.List;

public class Spot {
    private long id;
    private String name;
    private String address;
    private String detail;
    private List<SpotImage> images;
    private String url;
    private double lat;
    private double lng;
    private List<Long> categoryId;
    private String userId;
    private SpotAccess access;

    public Spot(long id, String name, String address, String detail, List<SpotImage> images, String url, double lat, double lng, List<Long> categoryId, String userId, SpotAccess access) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.detail = detail;
        this.images = images;
        this.url = url;
        this.lat = lat;
        this.lng = lng;
        this.categoryId = categoryId;
        this.userId = userId;
        this.access = access;
    }

    public Spot() {
    }

    public List<SpotImage> getImages() {
        return images;
    }

    public void setImages(List<SpotImage> images) {
        this.images = images;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SpotAccess getAccess() {
        return access;
    }

    public void setAccess(SpotAccess access) {
        this.access = access;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public List<Long> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<Long> categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
