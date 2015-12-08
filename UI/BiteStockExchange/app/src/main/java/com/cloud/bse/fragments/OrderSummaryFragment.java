package com.cloud.bse.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cloud.bse.DataFactory;
import com.cloud.bse.R;
import com.cloud.bse.model.OrderSummaryItem;

import java.util.ArrayList;

/**
 * Created by Rakesh on 12/4/15.
 */
public class OrderSummaryFragment extends Fragment {
    private ItemsAdapter itemsAdapter;
    private ListView itemsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordersummary, container, false);

        Spinner table = (Spinner) view.findViewById(R.id.table_number_spinner);
        String[] items = new String[]{"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, items);
        table.setAdapter(adapter);

        itemsListView = (ListView) view.findViewById(R.id.order_summary_items_list_view);
        ArrayList<OrderSummaryItem> orderSummaryItems = DataFactory.getOrderSummaryItems();
        itemsAdapter = new ItemsAdapter(orderSummaryItems);
        itemsListView.setAdapter(itemsAdapter);

        return view;
    }

    private class ItemsAdapter extends ArrayAdapter<OrderSummaryItem> {
        public ItemsAdapter(ArrayList<OrderSummaryItem> orderSummaryItems) {
            super(getActivity(), 0, orderSummaryItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.order_summary_list_item, null);
            }

            final OrderSummaryItem orderSummaryItem = getItem(position);
            //Log.e("MenuItem", "menuItemname = " + menuItem.getItemName());
            ((TextView) convertView.findViewById(R.id.order_summary_item_name)).setText(orderSummaryItem.getItemName());
            final TextView quantityTextView = ((TextView) convertView.findViewById(R.id.order_summary_item_quantity));
            quantityTextView.setText(String.valueOf(orderSummaryItem.getQuantity()));
            final TextView totalPriceTextView = ((TextView) convertView.findViewById(R.id.order_summary_item_total_price));
            int totalPrice = orderSummaryItem.getItemPrice() * orderSummaryItem.getQuantity();
            totalPriceTextView.setText(String.valueOf(totalPrice));
            Button add = (Button) convertView.findViewById(R.id.order_summary_item_quantity_add_button);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataFactory.addQuantity(orderSummaryItem.getItemId());
                    quantityTextView.setText(String.valueOf(orderSummaryItem.getQuantity()));
                    int totalPrice = orderSummaryItem.getItemPrice() * orderSummaryItem.getQuantity();
                    totalPriceTextView.setText(String.valueOf(totalPrice));
                }
            });
            Button remove = (Button) convertView.findViewById(R.id.order_summary_item_quantity_remove_button);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataFactory.removeQuantity(orderSummaryItem.getItemId());
                    quantityTextView.setText(String.valueOf(orderSummaryItem.getQuantity()));
                    int totalPrice = orderSummaryItem.getItemPrice() * orderSummaryItem.getQuantity();
                    totalPriceTextView.setText(String.valueOf(totalPrice));
                }
            });
            return convertView;
        }
    }
}
