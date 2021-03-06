package com.cloud.bse.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud.bse.DataFactory;
import com.cloud.bse.NavigationActivity;
import com.cloud.bse.R;
import com.cloud.bse.model.OrderSummaryItem;

import java.util.ArrayList;

/**
 * Created by Rakesh on 12/4/15.
 */
public class OrderSummaryFragment extends Fragment {
    private ItemsAdapter itemsAdapter;
    private ListView itemsListView;
    private Button placeOrderButton;
    private TextView total_price;
    private ProgressDialog pDialog;
    private Button backToMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordersummary, container, false);

        pDialog = new ProgressDialog(getActivity());
        backToMenu = (Button) view.findViewById(R.id.order_summary_menu_button);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = new MenuFragment();
                transaction.replace(R.id.navigation_container, fragment);
                transaction.commit();
            }
        });
        Spinner table = (Spinner) view.findViewById(R.id.table_number_spinner);
        String[] items = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, items);
        table.setAdapter(adapter);

        itemsListView = (ListView) view.findViewById(R.id.order_summary_items_list_view);
        ArrayList<OrderSummaryItem> orderSummaryItems = DataFactory.getOrderSummaryItems();
        itemsAdapter = new ItemsAdapter(orderSummaryItems);
        itemsListView.setAdapter(itemsAdapter);

        placeOrderButton = (Button) view.findViewById(R.id.order_summary_place_order_button);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataFactory.getOrderSummaryItems().size() <= 0) {
                    Toast.makeText(getActivity(), "Please select any items", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!DataFactory.isInner()) {
                    // Toast.makeText(getActivity(), "Order can be placed inside Restaunrant Only", Toast.LENGTH_SHORT).show();
                    // return;
                }

                PlaceOrder placeOrder = new PlaceOrder();
                placeOrder.execute();
                Toast.makeText(getActivity(), "A server will come to confirm your order", Toast.LENGTH_LONG).show();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = new MenuFragment();
                transaction.replace(R.id.navigation_container, fragment);
                transaction.commit();
            }
        });

        total_price = (TextView) view.findViewById(R.id.order_summary_total_price);
        total_price.setText("Total Price: $" + DataFactory.getTotal_price());



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
            totalPriceTextView.setText("$" + String.valueOf(totalPrice));
            Button add = (Button) convertView.findViewById(R.id.order_summary_item_quantity_add_button);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataFactory.addQuantity(orderSummaryItem.getItemId());
                    quantityTextView.setText(String.valueOf(orderSummaryItem.getQuantity()));
                    int totalPrice = orderSummaryItem.getItemPrice() * orderSummaryItem.getQuantity();
                    totalPriceTextView.setText("$" + String.valueOf(totalPrice));
                    total_price.setText("Total Price: $" + DataFactory.getTotal_price());
                }
            });
            Button remove = (Button) convertView.findViewById(R.id.order_summary_item_quantity_remove_button);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataFactory.removeQuantity(orderSummaryItem.getItemId());
                    quantityTextView.setText(String.valueOf(orderSummaryItem.getQuantity()));
                    int totalPrice = orderSummaryItem.getItemPrice() * orderSummaryItem.getQuantity();
                    totalPriceTextView.setText("$" + String.valueOf(totalPrice));
                    total_price.setText("Total Price: $" + DataFactory.getTotal_price());
                }
            });
            return convertView;
        }
    }

    private class PlaceOrder extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Placing order...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                DataFactory.placeOrder();
            } catch (Exception e) {
                publishProgress("Failed to place order. Exception = " + e.toString());
                Log.e("PlaceOrder", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... param) {
        }

        @Override
        protected void onPostExecute(Void param) {
            DataFactory.clearOrderSummaryItems();
            itemsAdapter.clear();
            itemsAdapter.notifyDataSetChanged();
            DataFactory.setTotal_price();
            total_price.setText("Total Price: $0");
            if(pDialog.isShowing()) pDialog.dismiss();
        }
    }
}
