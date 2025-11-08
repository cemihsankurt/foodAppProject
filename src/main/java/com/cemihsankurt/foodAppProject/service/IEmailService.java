package com.cemihsankurt.foodAppProject.service;

public interface IEmailService {

    void sendVerificationEmail(String to, String link);
}
