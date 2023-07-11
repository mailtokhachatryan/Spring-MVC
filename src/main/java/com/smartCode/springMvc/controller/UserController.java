package com.smartCode.springMvc.controller;

import com.smartCode.springMvc.service.user.UserService;
import com.smartCode.springMvc.util.constants.Parameter;
import com.smartCode.springMvc.util.constants.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {


    @Autowired
    public UserService userService;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam(required = false, defaultValue = "off") String remember_me,
                              HttpSession session) {

        try {
            userService.login(email, password);
            session.setAttribute(Parameter.EMAIL_PARAMETER, email);
            return new ModelAndView(Path.HOME_PATH);
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView(Path.INDEX_PATH);
            modelAndView.addObject(Parameter.MESSAGE_ATTRIBUTE, e.getMessage());
            return modelAndView;
        }
    }

}
