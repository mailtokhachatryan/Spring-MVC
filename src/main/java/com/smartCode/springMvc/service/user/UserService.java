package com.smartCode.springMvc.service.user;


import com.smartCode.springMvc.dto.UserFilter;
import com.smartCode.springMvc.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {

    User register(User user) ;

    User getUser(Long id);

    void login(String email, String password) throws Exception;

    void changePassword(String email,String newPassword,String repeatPassword);

    void deleteAccount(String email);

    void verify(String email, String code);

    List<User> getAllUsers();

    User update(Long id, User user);

    @Transactional
    User updatePartially(Long id, User updatedUser);

    List<User> filter(UserFilter userFilter);
}
