package com.time_em.notifications;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.time_em.android.R;
import com.time_em.model.User;
import com.time_em.team.UserListActivity;
import com.time_em.utils.Utils;

import java.util.ArrayList;

public class UserSelectionActivity extends Activity {

    //todo widgets
    private ListView userListView;
    private Button done;
    private UserAdapter adapter;
    private ImageView back, close;
    private TextView headerInfo;
    //todo array list
    private ArrayList<User> userList;
    private ArrayList<String> selectedUserIds;
   //todo variables
    private String selectedIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_selection);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        initScreen();
        setClickListeners();
    }

    private void initScreen() {
        userListView = (ListView)findViewById(R.id.userList);
        done = (Button)findViewById(R.id.done);
        back = (ImageView)findViewById(R.id.back);
        close = (ImageView)findViewById(R.id.close);
        headerInfo = (TextView)findViewById(R.id.info);

        back.setVisibility(View.GONE);
        close.setVisibility(View.VISIBLE);
        headerInfo.setText("Select Users");

        userList = getIntent().getParcelableArrayListExtra("activeUsers");
        selectedIds =  getIntent().getStringExtra("selectedIds");

        selectedUserIds = new ArrayList<String>();
        String[] _users;
        if(selectedIds.trim().equals(""))
            _users = new String[0];
        else
            _users = selectedIds.split(",");

        for(int i = 0; i<_users.length; i++){
            selectedUserIds.add(_users[i]);
        }

        adapter = new UserAdapter(UserSelectionActivity.this);
        userListView.setAdapter(adapter);
    }

    private void setClickListeners() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _users = "", selectedUsers = "";
                for(int i = 0; i < userList.size(); i++){

                    if(selectedUserIds.contains(String.valueOf(userList.get(i).getId()))){
                        if(_users.equals("")){
                            _users = _users + userList.get(i).getId();
                            selectedUsers = selectedUsers + userList.get(i).getFullName();
                        }else {
                            _users = _users + "," +  userList.get(i).getId();
                            selectedUsers =   selectedUsers + "," + userList.get(i).getFullName();
                        }
                    }
                }
                Utils.saveInSharedPrefs(UserSelectionActivity.this, "SelectedIds", _users);
                Utils.saveInSharedPrefs(UserSelectionActivity.this, "SelectedUsers", selectedUsers);
                finish();
            }
        });


        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = userList.get(position);

                if(selectedUserIds.contains(String.valueOf(user.getId()))){
                    selectedUserIds.remove(String.valueOf(user.getId()));
                   // Utils.showToast(UserSelectionActivity.this, user.getId()+" removed");
                }else{
                    selectedUserIds.add(String.valueOf(user.getId()));
                   // Utils.showToast(UserSelectionActivity.this, user.getId()+" added");
                }
                adapter.notifyDataSetChanged();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public class UserAdapter extends BaseAdapter
    {
        private Context context;
        private CheckBox chkUsers;
        private TextView username;

        public UserAdapter(Context ctx)
        {
            context = ctx;
        }

        //		@Override
        public int getCount() {
            // TODO Auto-generated method stub
            return userList.size();
        }

        //		@Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return userList.get(position);
        }

        //		@Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        //		@Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(convertView == null){
                convertView = inflater.inflate(R.layout.row_user_selection, parent, false);
            }

            chkUsers = (CheckBox) convertView.findViewById(R.id.chkUser);
            username = (TextView)convertView.findViewById(R.id.userName);

            username.setText(userList.get(position).getFullName());

            if(selectedUserIds.contains(String.valueOf(userList.get(position).getId())))
                chkUsers.setChecked(true);
            else
                chkUsers.setChecked(false);

            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
}