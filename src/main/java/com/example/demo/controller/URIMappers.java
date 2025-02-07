package com.example.demo.controller;

import com.example.demo.role.MemberRole;

public class URIMappers {
    public static final String MainPageURI ="/MainPage";
    public static final String MainPageHtml ="/MainPage/MainPage.html";

    public static final String LoginPageURI ="/Login";
    public static final String LoginPageHtml ="/MemberController/LoginPage.html";

    public static final String RegisterPageURI ="/Register";
    public static final String RegisterPageHtml ="/MemberController/RegisterPage.html";

    public static final String UserPageURI ="/User";
    public static final String UserPageHtml ="/MemberController/UserPage.html";
    public static final String UserRole = "page_user";

    public static final String AdminPageURI ="/Admin";
    public static final String AdminPageHtml ="/AdminController/AdminPage.html";
    public static final String AdminRole = "page_admin";
}
