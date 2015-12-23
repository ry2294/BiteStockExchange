package com.cloud.bse.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cloud.bse.DataFactory;
import com.cloud.bse.R;
import com.cloud.bse.model.FriendActivity;

import java.util.ArrayList;

/**
 * Created by Rakesh on 12/4/15.
 */
public class FriendActivityFragment extends Fragment {
    private ActivityAdapter activityAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendactivity, container, false);

        ListView friendActivitiesListView = (ListView) view.findViewById(R.id.friend_activity_listview);
        activityAdapter = new ActivityAdapter(DataFactory.getFriendActivities());
        friendActivitiesListView.setAdapter(activityAdapter);

        return view;
    }

    private class ActivityAdapter extends ArrayAdapter<FriendActivity> {
        public ActivityAdapter(ArrayList<FriendActivity> activities) {
            super(getActivity(), 0, activities);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.friend_activity_list_item, null);
            }

            FriendActivity activity = getItem(position);
            ((TextView) convertView.findViewById(R.id.friend_activity_name)).setText(activity.getName());
            ((TextView) convertView.findViewById(R.id.friend_activity_activity)).setText(activity.getActivity());

            return convertView;
        }
    }
}
