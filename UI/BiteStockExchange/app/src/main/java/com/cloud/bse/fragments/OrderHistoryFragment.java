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
import android.widget.TextView;
import android.widget.Toast;

import com.cloud.bse.DataFactory;
import com.cloud.bse.R;
import com.cloud.bse.model.OrderSummaryItem;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by rakesh on 12/21/15.
 */
public class OrderHistoryFragment extends Fragment {
    private ItemsAdapter itemsAdapter;
    private Button backToMenu;
    private Button payBill;
    private ListView itemsListView;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        pDialog = new ProgressDialog(getActivity());
        ((TextView) view.findViewById(R.id.order_history_total_price)).setText("Total Price: $" + DataFactory.totalOrderPrice);;
        payBill = (Button) view.findViewById(R.id.order_history_paybill_button);
        payBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataFactory.getOrderHistoryItems().size() > 0) {
                    DataFactory.clearHistoryItems();
                    PayBill payBill = new PayBill();
                    payBill.execute();
                    Toast.makeText(getActivity(), "Server is on his way with your bill", Toast.LENGTH_LONG).show();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new MenuFragment();
                    transaction.replace(R.id.navigation_container, fragment);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), "Please select any items", Toast.LENGTH_LONG).show();
                }
            }
        });
        backToMenu = (Button) view.findViewById(R.id.order_history_menu_button);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = new MenuFragment();
                transaction.replace(R.id.navigation_container, fragment);
                transaction.commit();
            }
        });

        itemsListView = (ListView) view.findViewById(R.id.order_history_listview);
        ArrayList<OrderSummaryItem> orderSummaryItems = DataFactory.getOrderHistoryItems();
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
                        .inflate(R.layout.order_history_list_item, null);
            }

            OrderSummaryItem orderSummaryItem = getItem(position);

            ((TextView) convertView.findViewById(R.id.order_history_item_name)).setText(orderSummaryItem.getItemName());
            ((TextView) convertView.findViewById(R.id.order_history_item_quantity)).setText(String.valueOf(orderSummaryItem.getQuantity()));
            ((TextView) convertView.findViewById(R.id.order_history_item_total_price)).setText("$" + orderSummaryItem.getItemPrice());
            return convertView;
        }
    }

    private class PayBill extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Generating the bill...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                DataFactory.payBill();
            } catch (Exception e) {
                publishProgress("Failed to invite. Exception = " + e.toString());
                Log.e("InviteFriend", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... param) {
        }

        @Override
        protected void onPostExecute(Void param) {
            if(pDialog.isShowing()) pDialog.dismiss();
        }
    }
}
