package com.smartCode.springMvc;

import com.smartCode.springMvc.model.User;
import com.smartCode.springMvc.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");

        UserRepository bean = context.getBean(UserRepository.class);
        User byEmail = bean.findByEmail("test@gmail.com");
        System.out.print(byEmail);

    }

}
