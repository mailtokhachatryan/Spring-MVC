package com.smartCode.springMvc.service.user.impl;

import com.smartCode.springMvc.dto.UserFilter;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

@Slf4j
@Service  // name = userServiceImpl
public class UserServiceImpl implements UserService {

//    Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User register(User user) {

        entityManager.flush();
        validationForRegistration(user);
        String code = RandomGenerator.generateNumericString(6);
        user.setPassword(MD5Encoder.encode(user.getPassword()));
        user.setCode(code);
        User save = userRepository.save(user);
        emailService.sendSimpleMessage(user.getEmail(), "Verification", "Your verification code is " + code);
        return save;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void login(String email, String password) {
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

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User update(Long id, User updatedUser) {
        User user1 = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user1.setEmail(updatedUser.getEmail());
        user1.setName(updatedUser.getName());
        user1.setLastname(updatedUser.getLastname());
        user1.setAge(updatedUser.getAge());
        user1.setPassword(updatedUser.getPassword());
        user1.setBalance(updatedUser.getBalance());
        return userRepository.save(user1);
    }

    @Override
    @Transactional
    public User updatePartially(Long id, User updatedUser) {
        User user1 = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        user1.setEmail(nonNull(updatedUser.getEmail()) ? updatedUser.getEmail() : user1.getEmail());
        user1.setName(nonNull(updatedUser.getName()) ? updatedUser.getName() : user1.getName());
        user1.setLastname(nonNull(updatedUser.getLastname()) ? updatedUser.getLastname() : user1.getLastname());
        user1.setAge(updatedUser.getAge() != 0 ? updatedUser.getAge() : user1.getAge());
        user1.setPassword(nonNull(updatedUser.getPassword()) ? updatedUser.getPassword() : user1.getPassword());
        user1.setBalance(updatedUser.getBalance() != 0 ? updatedUser.getBalance() : user1.getBalance());

        return userRepository.save(user1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> filter(UserFilter userFilter) {
        log.info("filtering users");
        return userRepository.findAll(filtration(userFilter));
    }


    private Specification<User> filtration(UserFilter userFilter) {
        return Specification.where((root, criteriaQuery, criteriaBuilder) -> {

            var predicates = new ArrayList<Predicate>();
            Predicate filterPredicate;

            if (nonNull(userFilter.getIsVerified())) {
                filterPredicate = criteriaBuilder.equal(root.get("isVerified"), userFilter.getIsVerified());
                predicates.add(filterPredicate);
            }
            if (nonNull(userFilter.getStartAge())) {
                filterPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("age"), userFilter.getStartAge());
                predicates.add(filterPredicate);
            }
            if (nonNull(userFilter.getEndAge())) {
                filterPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("age"), userFilter.getEndAge());
                predicates.add(filterPredicate);
            }

            criteriaQuery.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

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
