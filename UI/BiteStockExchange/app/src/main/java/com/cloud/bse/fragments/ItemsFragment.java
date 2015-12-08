package com.cloud.bse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud.bse.DataFactory;
import com.cloud.bse.R;
import com.cloud.bse.model.MenuItem;

import java.util.ArrayList;

/**
 * Created by Rakesh on 12/4/15.
 */
public class ItemsFragment extends Fragment {
    private ArrayList<MenuItem> menuItems;
    private ItemsAdapter itemsAdapter;
    private ListView itemsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        menuItems = (ArrayList<MenuItem>) args.getSerializable("menuItems");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        itemsListView = (ListView) view.findViewById(R.id.items_list_view);
        itemsAdapter = new ItemsAdapter(menuItems);
        itemsListView.setAdapter(itemsAdapter);
        itemsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MenuItem menuItem = itemsAdapter.getItem(position);
                        DataFactory.addItemToOrder(menuItem.getItemId(), menuItem.getItemName(), 1, menuItem.getActualPrice());
                        Toast.makeText(getActivity(), "Item Added to Kart", Toast.LENGTH_LONG);
                    }
                }
        );

        return view;
    }

    private class ItemsAdapter extends ArrayAdapter<MenuItem> {
        public ItemsAdapter(ArrayList<MenuItem> menuItems) {
            super(getActivity(), 0, menuItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.menu_item_list_item, null);
            }

            MenuItem menuItem = getItem(position);
            //Log.e("MenuItem", "menuItemname = " + menuItem.getItemName());
            ((TextView) convertView.findViewById(R.id.menu_item_name)).setText(menuItem.getItemName());
            ((TextView) convertView.findViewById(R.id.menu_item_actual_price)).setText(String.valueOf(menuItem.getActualPrice()));
            ((TextView) convertView.findViewById(R.id.menu_item_high_price)).setText(String.valueOf(menuItem.getItemHighPrice()));
            ((TextView) convertView.findViewById(R.id.menu_item_low_price)).setText(String.valueOf(menuItem.getLowPrice()));

            return convertView;
        }
    }
}
