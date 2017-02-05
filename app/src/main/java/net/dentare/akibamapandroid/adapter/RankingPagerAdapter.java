package net.dentare.akibamapandroid.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.dentare.akibamapandroid.fragment.RankingFragment;
import net.dentare.akibamapandroid.resources.SpotRanking;

import java.util.LinkedList;
import java.util.List;

public class RankingPagerAdapter extends FragmentPagerAdapter{
    private final List<SpotRanking> rankingList = new LinkedList<>();

    public static RankingPagerAdapter getInstance(FragmentManager fragmentManager) {
        return new RankingPagerAdapter(fragmentManager);
    }

    private RankingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (rankingList.size() > position) return rankingList.get(position).getCategoryName();
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        if (rankingList.size() > position) return RankingFragment.getInstance(rankingList.get(position));
        return null;
    }

    @Override
    public int getCount() {
        return rankingList.size();
    }

    public void clear(){
        this.rankingList.clear();
    }

    public void addAll(List<SpotRanking> spotRankings){
        this.rankingList.addAll(spotRankings);
    }

    public void add(SpotRanking spotRanking){
        this.rankingList.add(spotRanking);
    }
}