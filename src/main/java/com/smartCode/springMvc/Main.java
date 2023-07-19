package com.smartCode.springMvc;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.smartCode.springMvc.model.User;
import com.smartCode.springMvc.repository.UserRepository;
import com.smartCode.springMvc.service.user.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args){

        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");

        UserService bean = context.getBean(UserService.class);

//        User user = new User(1l,"User1","lastname",100,"email@com","pass","54654",true,28);

//        ObjectMapper objectMapper = new ObjectMapper();
//        String str = objectMapper.writeValueAsString(user);
//
//        System.out.println(str);

//        XmlMapper xmlMapper = new XmlMapper();
//
//        String s = xmlMapper.writeValueAsString(user);
//
//        System.out.println(s);


        String json = "{\"id\":1,\"name\":\"User1\",\"lastname\":\"lastname\",\"balance\":100.0,\"email\":\"email@com\",\"password\":\"pass\",\"code\":\"54654\",\"age\":28,\"verified\":true}\n";

//        User user1 = objectMapper.readValue(json, User.class);

//        System.out.println(user1);


    }

}
