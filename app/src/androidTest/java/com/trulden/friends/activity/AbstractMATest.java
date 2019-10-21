package com.trulden.friends.activity;

import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.AbstractTest;

import org.junit.Rule;

public abstract class AbstractMATest extends AbstractTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
}
