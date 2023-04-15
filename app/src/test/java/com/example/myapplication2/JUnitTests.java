package com.example.myapplication2;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.myapplication2.ui.fragment.LoginFragment;

public class JUnitTests {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void validateFirebaseAuthPwFail1() {
        String password = "abcde";
        assertFalse(LoginFragment.validatePassword(password));
    }

    @Test
    public void validateFirebaseAuthPwPass1() {
        String password = "123456";
        assertTrue(LoginFragment.validatePassword(password));
    }

    @Test
    public void validateFirebaseAuthPwFail2() {
        String email = "michaelchiou2010gmailcom";
        String password = "123456";
        assertTrue(LoginFragment.validatePassword(password));
    }

}