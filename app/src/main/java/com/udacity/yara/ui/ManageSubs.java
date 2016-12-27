package com.udacity.yara.ui;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.udacity.yara.R;
import com.udacity.yara.adapter.RecyclerViewAdapter;
import com.udacity.yara.helper.OnStartDragListener;
import com.udacity.yara.helper.SimpleItemTouchHelperCallback;
import com.udacity.yara.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ManageSubs extends AppCompatActivity implements OnStartDragListener {

    private static String LOG_TAG = ManageSubs.class.getSimpleName();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ItemTouchHelper mItemTouchHelper;
    final Context c = this;
    SharedPreferences prefs;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.subList)
    RecyclerView subredditList;


    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subs);

        ButterKnife.bind(this);

        manageToolbar();

        prefs = this.getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);


        linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerViewAdapter = new RecyclerViewAdapter(getBaseContext(),this);

        subredditList.setAdapter(mRecyclerViewAdapter);
        subredditList.setLayoutManager(linearLayoutManager);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(subredditList);


        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void manageToolbar(){
        toolbar.setTitle(getString(R.string.manage_subreddits));
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void openDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        mRecyclerViewAdapter.add(mRecyclerViewAdapter.getItemCount(),userInputDialogEditText.getText().toString());
                       // mRecyclerViewAdapter.notifyDataSetChanged();
                        String subs= StringUtil.arrayToString(mRecyclerViewAdapter.getmItems());
                        prefs.edit().putString(getString(R.string.subreddit_pref_key),subs).apply();

                    }
                })

                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
