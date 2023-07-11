package com.smartCode.springMvc.controller;

import com.smartCode.springMvc.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

    @Autowired
    public UserService userService;

    @RequestMapping(path = "/test2", method = RequestMethod.GET)
    public ModelAndView test2() {
        ModelAndView modelAndView = new ModelAndView("/Secure/home");
        modelAndView.addObject("message", "assfasdasdsad");
        return modelAndView;
    }

}
