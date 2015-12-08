package com.cloud.bse.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloud.bse.DataFactory;
import com.cloud.bse.R;
import com.cloud.bse.model.MenuItem;

import java.util.ArrayList;

/**
 * Created by Rakesh on 12/4/15.
 */
public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);


        MenuPagerAdapter menuPagerAdapter = new MenuPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(menuPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setTabsFromPagerAdapter(menuPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private class MenuPagerAdapter extends FragmentStatePagerAdapter {
        public MenuPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ArrayList<MenuItem> menuItems;
            Fragment fragment = new ItemsFragment();
            Bundle args = new Bundle();

            switch(position) {
                case 0: menuItems = DataFactory.getMenuForCategory("Appetizer");
                        break;
                case 1: menuItems = DataFactory.getMenuForCategory("Soups");
                        break;
                case 2: menuItems = DataFactory.getMenuForCategory("Main Course");
                        break;
                case 3: menuItems = DataFactory.getMenuForCategory("Desserts");
                        break;
                default: menuItems = DataFactory.getMenuForCategory("Appetizer");
            }

            args.putSerializable("menuItems", menuItems);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Appetizers";
                case 1: return "Soups";
                case 2: return "Main Course";
                case 3: return "Desserts";
                default: return "Appetizers";
            }
        }
    }
}
