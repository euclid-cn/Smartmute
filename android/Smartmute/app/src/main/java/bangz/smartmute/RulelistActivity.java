
/*
 * Copyright (c) 2014 Royer Wang. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package bangz.smartmute;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.app.DialogFragment;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager ;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import bangz.smartmute.content.RulesColumns;
import bangz.smartmute.util.LogUtils;


public class RulelistActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks
        , RulelistFragment.OnRuleItemClickListerner
{


    private static final String TAG = RulelistActivity.class.getSimpleName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rulelist);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));



//        BroadcastReceiver ringReceive = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(getBaseContext(), "ring volumn changed.",Toast.LENGTH_LONG).show();
//            }
//        };
//        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
//        registerReceiver(ringReceive,filter);

    }

    @Override
    public void onCatalogItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, RulelistFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        String[] titles = getResources().getStringArray(R.array.navigation_items);
        mTitle = titles[number-1];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.rulelist, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            //TODO process action add new rule

            DialogFragment chooseRuleTypeDialog = new ChooseRuleTypeDialogFragment();
            chooseRuleTypeDialog.show(getFragmentManager(), "ChooseRuleTypeDialog");
            LogUtils.LOGD(TAG, "Dialog showed.");


            //Intent intent = new Intent(this, WifiEditActivity.class);
            //intent.putExtra(Constants.INTENT_EDITORNEW, Constants.INTENT_NEW);
            //startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRuleItemSelected(long id, int ruletype) {
        Uri uri = ContentUris.withAppendedId(RulesColumns.CONTENT_URI,id);
        Intent intent = new Intent();
        intent.setData(uri);
        switch(ruletype) {
            case RulesColumns.RT_LOCATION:
                intent.setClass(this, LocationRuleEditActivity.class);
                intent.putExtra(Constants.INTENT_EDITORNEW, Constants.INTENT_EDIT);
                break;
            case RulesColumns.RT_WIFI:
                intent.setClass(this, WifiEditActivity.class);
                intent.putExtra(Constants.INTENT_EDITORNEW, Constants.INTENT_EDIT);
                break;
            case RulesColumns.RT_TIME:
                intent.setClass(this, TimeRuleEditActivity.class);
                intent.putExtra(Constants.INTENT_EDITORNEW, Constants.INTENT_EDIT);
                break;
        }
        startActivity(intent);
    }


    public static class ChooseRuleTypeDialogFragment extends DialogFragment {

        public interface NoticeDialogListerner {
            public void onItemSelected(DialogFragment dialog, int which);
        }

        NoticeDialogListerner mListerner ;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("What kind Rule ?")
                    .setItems(R.array.rule_type, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //mListerner.onItemSelected(ChooseRuleTypeDialogFragment.this, which);
                            if (which == 0) {
                                Intent intent = new Intent(getActivity(), LocationRuleEditActivity.class);
                                intent.putExtra(Constants.INTENT_EDITORNEW, Constants.INTENT_NEW);
                                startActivity(intent);
                            } else if (which == 1) {
                                Intent intent = new Intent(getActivity(), TimeRuleEditActivity.class);
                                intent.putExtra(Constants.INTENT_EDITORNEW, Constants.INTENT_NEW);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getActivity(), WifiEditActivity.class);
                                intent.putExtra(Constants.INTENT_EDITORNEW, Constants.INTENT_NEW);
                                startActivity(intent);
                            }

                        }
                    });


            return builder.create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

//            try {
//                mListerner = (NoticeDialogListerner)activity ;
//            } catch(ClassCastException e) {
//                throw new ClassCastException(activity.toString() + " must implement NoticeDialogListerner");
//            }
        }

    }
}
