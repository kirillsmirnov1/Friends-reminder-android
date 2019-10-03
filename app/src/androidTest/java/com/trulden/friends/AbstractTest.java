package com.trulden.friends;

import androidx.fragment.app.FragmentActivity;

import org.junit.Before;

public class AbstractTest {

    @Before
    public void initDB(){
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());
    }

}
