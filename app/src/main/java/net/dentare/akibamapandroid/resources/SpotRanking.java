package net.dentare.akibamapandroid.resources;

public class SpotRanking {
    private long rankingId;
    private long categoryId;
    private String categoryName;

    public SpotRanking(long rankingId, long categoryId, String categoryName) {
        this.rankingId = rankingId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public SpotRanking() {
    }

    public long getRankingId() {
        return rankingId;
    }

    public void setRankingId(long rankingId) {
        this.rankingId = rankingId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}