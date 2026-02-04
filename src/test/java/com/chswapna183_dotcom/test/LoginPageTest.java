package com.chswapna183_dotcom.test;

import com.chswapna183_dotcom.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginPageTest extends UITestBase {

    @Test(groups = "ui")
    public void invalidLogin_showsErrorMessage() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectFailure("invalid_user", "invalid_password");

        Assert.assertTrue(
                loginPage.getFlashMessage().contains("Your username is invalid"),
                "Expected invalid username message."
        );
    }
}
