package com.smartCode.springMvc;

import com.smartCode.springMvc.model.User;
import com.smartCode.springMvc.repository.UserRepository;
import com.smartCode.springMvc.service.user.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");

        UserService bean = context.getBean(UserService.class);
        bean.register(User.builder().name("asd").email("asdasd@gmail.com").password("asdas5646545d").build());

    }

}
