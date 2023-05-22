package com.ru.gbox.service;

import com.ru.gbox.config.JwtTokenUtil;
import com.ru.gbox.models.AuthRes;
import com.ru.gbox.models.SignUpReq;
import com.ru.gbox.models.User;
import com.ru.gbox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private ProjectionFactory projectionFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public AuthRes signUp(SignUpReq signUpReq) {

        if (!StringUtils.hasLength(signUpReq.getFullName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full Name is mandatory information");
        }

        if (!StringUtils.hasLength(signUpReq.getEmail()) || !StringUtils.hasLength(signUpReq.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please Provide a valid email and password");
        }

        if (Objects.isNull(signUpReq.getMobile())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please Provide a valid mobile number");
        }

        if (userRepository.findByEmail(signUpReq.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists, please login");
        }
        if (userRepository.findByMobile(signUpReq.getMobile()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile  already exists, please login");
        }

        User user = new User();
        user.setFullName(signUpReq.getFullName());
        user.setMobile(signUpReq.getMobile());
        user.setEmail(signUpReq.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(signUpReq.getPassword()));
        user.setCreatedAt(new Date());
        userRepository.save(user);

        return jwtTokenUtil.generateAuthToken(user.getEmail());
    }

    public AuthRes signin(String email, String password) {

        if (!StringUtils.hasLength(email) || !StringUtils.hasLength(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please Provide a valid email and password");
        }

        try {
            UserDetails userDetails = (UserDetails) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)).getPrincipal();
        } catch (Exception be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");
        }

        return jwtTokenUtil.generateAuthToken(email);
    }


}
