package com.smartCode.springMvc.controller;

import com.smartCode.springMvc.exceptions.VerificationException;
import com.smartCode.springMvc.model.User;
import com.smartCode.springMvc.service.user.UserService;
import com.smartCode.springMvc.util.constants.Parameter;
import com.smartCode.springMvc.util.constants.Path;
import com.smartCode.springMvc.util.encoder.AESManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class AccountController {

    @Autowired
    @Qualifier("userServiceImpl")
    public UserService userService;

    @PostMapping(value = "/register")
//    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam String name,
                                 @RequestParam String lastname,
                                 @RequestParam Double balance,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam int age) {
        try {
            User user = new User();
            user.setName(name);
            user.setLastname(lastname);
            user.setAge(age);
            user.setEmail(email);
            user.setBalance(balance);
            user.setPassword(password);
            userService.register(user);
            return new ModelAndView(Path.VERIFICATION_PATH);
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView(Path.REGISTER_PATH);
            modelAndView.addObject(Parameter.MESSAGE_ATTRIBUTE, e.getMessage());
            return modelAndView;
        }
    }

    @GetMapping(path = "/")
    public ModelAndView start(@CookieValue(name = Parameter.REMEMBER_COOKIE, required = false) Cookie rememberCookie,
                              HttpSession session,
                              HttpServletResponse response) {
        try {
            if (rememberCookie != null) {
                String encodedValue = rememberCookie.getValue();
                String decrypt = AESManager.decrypt(encodedValue);
                String email = decrypt.split(":")[0];
                String password = decrypt.split(":")[1];

                return login(email, password, "on", session, response);
            } else {
                return new ModelAndView(Path.INDEX_PATH);
            }
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView(Path.INDEX_PATH);
            modelAndView.addObject(Parameter.MESSAGE_ATTRIBUTE, e.getMessage());
            return modelAndView;
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam(required = false, defaultValue = "off") String rememberMe,
                              HttpSession session,
                              HttpServletResponse response) {

        try {
            userService.login(email, password);
            if (rememberMe.equalsIgnoreCase("on")) {
                Cookie cookie = new Cookie(Parameter.REMEMBER_COOKIE, AESManager.encrypt(email + ":" + password));
                cookie.setMaxAge(360000);
                response.addCookie(cookie);
            }
            session.setAttribute(Parameter.EMAIL_PARAMETER, email);
            return new ModelAndView(Path.HOME_PATH);
        } catch (VerificationException e) {
            ModelAndView modelAndView = new ModelAndView(Path.VERIFICATION_PATH);
            modelAndView.addObject(Parameter.MESSAGE_ATTRIBUTE, e.getMessage());
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView(Path.INDEX_PATH);
            modelAndView.addObject(Parameter.MESSAGE_ATTRIBUTE, e.getMessage());
            return modelAndView;
        }
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ModelAndView verify(@RequestParam String email,
                               @RequestParam String code) {

        try {
            userService.verify(email, code);
            return new ModelAndView(Path.INDEX_PATH);
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView(Path.VERIFICATION_PATH);
            modelAndView.addObject(Parameter.MESSAGE_ATTRIBUTE, e.getMessage());
            return modelAndView;
        }
    }


}
