package com.smartCode.springMvc.service.user;


import com.smartCode.springMvc.model.User;

public interface UserService {

    void register(User user) ;

    User getUser(Long id);

    void login(String email, String password) throws Exception;

    void changePassword(String email,String newPassword,String repeatPassword);

    void deleteAccount(String email);

    void verify(String email, String code);
}
