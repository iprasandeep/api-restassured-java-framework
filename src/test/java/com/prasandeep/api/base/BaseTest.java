package com.prasandeep.api.base;

import com.prasandeep.api.actions.PostActions;
import com.prasandeep.api.listeners.TestListener;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners({AllureTestNg.class, TestListener.class})
public abstract class BaseTest {

    protected PostActions posts;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        posts = new PostActions();
    }
}
