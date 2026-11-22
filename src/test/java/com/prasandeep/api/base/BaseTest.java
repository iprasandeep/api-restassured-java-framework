package com.prasandeep.api.base;

import com.prasandeep.api.actions.AuthActions;
import com.prasandeep.api.actions.PostActions;
import com.prasandeep.api.actions.UserActions;
import com.prasandeep.api.client.AuthContext;
import com.prasandeep.api.listeners.TestListener;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners({AllureTestNg.class, TestListener.class})
public abstract class BaseTest {

    protected PostActions posts;
    protected AuthActions auth;
    protected UserActions users;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        AuthContext.clear();
        posts = new PostActions();
        auth = new AuthActions();
        users = new UserActions();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        AuthContext.clear();
    }
}
