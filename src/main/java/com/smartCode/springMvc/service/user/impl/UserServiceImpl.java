package com.smartCode.springMvc.service.user.impl;

import com.smartCode.springMvc.exceptions.UserNotFoundException;
import com.smartCode.springMvc.exceptions.ValidationException;
import com.smartCode.springMvc.exceptions.VerificationException;
import com.smartCode.springMvc.model.User;
import com.smartCode.springMvc.repository.UserRepository;
import com.smartCode.springMvc.service.email.EmailService;
import com.smartCode.springMvc.service.user.UserService;
import com.smartCode.springMvc.util.RandomGenerator;
import com.smartCode.springMvc.util.constants.Message;
import com.smartCode.springMvc.util.encoder.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service  // name = userServiceImpl
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public void register(User user) {
        validationForRegistration(user);
        String code = RandomGenerator.generateNumericString(6);
        user.setPassword(MD5Encoder.encode(user.getPassword()));
        user.setCode(code);
        userRepository.save(user);
        emailService.sendSimpleMessage(user.getEmail(), "Verification", "Your verification code is " + code);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        String mail = "test@gmail.com";
        return userRepository.findByEmail(mail);
    }

    @Override
    public void login(String email, String password) throws Exception {
        validationForLogin(email, password);
        User loginedUser = userRepository.findByEmail(email);

        if (loginedUser == null) {
            throw new UserNotFoundException(Message.USER_NOT_FOUNT);
        }

        if (!loginedUser.isVerified()) {
            throw new VerificationException("Verify your account");
        }

        if (!loginedUser.getPassword().equals(MD5Encoder.encode(password))) {
            throw new ValidationException(Message.WRONG_EMAIL_OR_PASSWORD);
        }
    }

    @Override
    @Transactional
    public void changePassword(String email, String newPassword, String repeatPassword) {
        if (!newPassword.equals(repeatPassword)) {
            throw new ValidationException(Message.PASSWORDS_NOT_MATCHES);
        }
        var user = userRepository.findByEmail(email);
        if (user == null)
            throw new UserNotFoundException(Message.USER_NOT_FOUNT);
        passwordValidation(newPassword);
        user.setPassword(MD5Encoder.encode(newPassword));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(String email) {
        try {
            User byEmail = userRepository.findByEmail(email);
            userRepository.delete(byEmail);
        } catch (Exception e) {
            throw new UserNotFoundException(Message.USER_NOT_FOUNT);
        }
    }

    @Override
    @Transactional
    public void verify(String email, String code) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException(Message.USER_NOT_FOUNT);
        }
        if (!user.getCode().equals(code)) {
            throw new ValidationException("Code is incorrect");
        }
        user.setVerified(true);
        userRepository.save(user);
    }

    private void validationForRegistration(User user) {
        if (user.getEmail() == null ||
                user.getEmail().length() == 0 ||
                !user.getEmail().matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*"
                        + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"))
            throw new ValidationException(Message.EMAIL_OR_PASSWORD_IS_NULL);
        if (user.getPassword() == null ||
                user.getPassword().length() < 8
        )
            throw new ValidationException(Message.PASSWORD_VALIDATION_IS_WRONG);
    }

    private void passwordValidation(String password) {
        if (password.length() < 8)
            throw new ValidationException(Message.PASSWORD_LENGTH_ISSUE);
    }

    private void validationForLogin(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty())
            throw new ValidationException(Message.EMAIL_OR_PASSWORD_IS_NULL);
    }


}
