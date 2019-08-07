package com.trulden.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.trulden.friends.adapter.PagerAdapter;
import com.trulden.friends.R;
import com.trulden.friends.adapter.TabCounterView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int NEW_INTERACTION_REQUEST = 1;

    //private Toolbar mToolbar;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initTabsAndPageViewer();
    }

    private void initTabsAndPageViewer() {
        mTabLayout = findViewById(R.id.tab_layout);

        TabCounterView tcv0 = new TabCounterView(this, "Meetings", getResources().getStringArray(R.array.meetings_a_while_ago).length);
        TabCounterView tcv1 = new TabCounterView(this, "Texting", getResources().getStringArray(R.array.texting_a_while_ago).length);
        TabCounterView tcv2 = new TabCounterView(this, "Calls", getResources().getStringArray(R.array.call_a_while_ago).length);

        mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv0)); // TODO имена и прочие параметры вкладок должны браться не из констант
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv1));         // Но это когда я настрою бд
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv2));

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initToolbar() {
        //mToolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);  // TODO меню сейчас пустое и бесполезное. Оно мне нужно?
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addMeeting(View view) { // TODO повесить на кнопку несколько действий
        Intent intent = new Intent(this, AddInteractionActivity.class);
        startActivityForResult(intent, NEW_INTERACTION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_INTERACTION_REQUEST){
            if(resultCode == RESULT_OK){
                String reply = data.getStringExtra(AddInteractionActivity.EXTRA_NEW_INTERACTION);
                // TODO save as log entry
            }
        }
    }
}
