package com.example.james.commute;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class BusListTabsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    BusStopDetails stopDetails;

    private Context context;
    private String stopid;
    private String stopname;
    private double blat;
    private double blon;
    private double ulat;
    private double ulon;

    private Bundle bundle;
    private Bundle bundle2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list_tabs);

        context=this;
        Intent intent = getIntent();
        stopname = intent.getStringExtra("BusStopName");
        setTitle(stopname);
        stopid = intent.getStringExtra("BusStopID");
        blat=intent.getDoubleExtra("BusStopLat",0);
        blon=intent.getDoubleExtra("BusStopLon",0);
        ulat=intent.getDoubleExtra("CurrLat",0);
        ulon=intent.getDoubleExtra("CurrLon",0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager){
        bundle = new Bundle();
        bundle.putString("stop",stopid);
        bundle2 = new Bundle();
        bundle2.putDouble("blat",blat);
        bundle2.putDouble("blon",blon);
        bundle2.putDouble("ulat",ulat);
        bundle2.putDouble("ulon",ulon);
        bundle2.putString("stopname",stopname);
        ETAListFragment LiveETAs = new ETAListFragment();
        MapFragment BusStopLoc = new MapFragment();

        LiveETAs.setArguments(bundle);
        BusStopLoc.setArguments(bundle2);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(LiveETAs,"Live ETAs");
        adapter.addFragment(BusStopLoc,"Map");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment,String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }
}
