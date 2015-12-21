package com.cloud.bse.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.cloud.bse.model.FriendInvite;

import java.util.ArrayList;

/**
 * Created by rakesh on 12/20/15.
 */
public class FriendInviteFragment extends Fragment {
    private InviteAdapter inviteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_invite, container, false);

        ListView friendInvitesListView = (ListView) view.findViewById(R.id.friend_invite_listview);
        inviteAdapter = new InviteAdapter(DataFactory.getFriendInvites());
        friendInvitesListView.setAdapter(inviteAdapter);
        friendInvitesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInvite invite = inviteAdapter.getItem(position);
                Toast.makeText(getActivity(), "Inviting " + invite.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private class InviteAdapter extends ArrayAdapter<FriendInvite> {
        public InviteAdapter(ArrayList<FriendInvite> invites) {
            super(getActivity(), 0, invites);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.friend_invite_list_item, null);
            }

            FriendInvite invite = getItem(position);
            ((TextView) convertView.findViewById(R.id.friend_invite_name)).setText(invite.getName());

            return convertView;
        }
    }
}
