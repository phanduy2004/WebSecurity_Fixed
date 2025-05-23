package com.group8.alomilktea.controller.web;


import com.group8.alomilktea.entity.Roles;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.model.OTPCodeModel;
import com.group8.alomilktea.model.ResetPasswordRequest;
import com.group8.alomilktea.model.SignUpModel;
import com.group8.alomilktea.model.UserModel;
import com.group8.alomilktea.repository.RoleRepository;
import com.group8.alomilktea.service.IUserService;
import com.group8.alomilktea.utils.AppUtil;
import com.group8.alomilktea.utils.Email;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
@Slf4j
public class AuthController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    IUserService userService;
    @Autowired
    AppUtil appUtil;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Email email;

    @RequestMapping(path = "/auth/login", method = RequestMethod.GET)
    public String login(Model model, Principal principal, @RequestParam(name = "message", required = false) String message, HttpServletRequest request) {
        if (principal != null) {
            return "redirect:/";
        }
        String loginStatus = (String) request.getSession().getAttribute("loginStatus");

        if ("error".equals(message) && "failure".equals(loginStatus)) {
            model.addAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng");
        }
        request.getSession().removeAttribute("loginStatus");
        return "web/auth/login";
    }
    @RequestMapping(path = "/auth/login", method = RequestMethod.POST)
    public String loginPost(Model model, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            // Lưu tên người dùng vào session
            request.getSession().setAttribute("username", principal.getName());
            return "redirect:/";  // Chuyển hướng sau khi đăng nhập
        }
        return "web/auth/login";
    }


    @RequestMapping(path = "/auth/login1", method = RequestMethod.GET)
    public String user(Model model) {

        return "web/auth/security";
    }


    @RequestMapping(path = "/auth/register", method = RequestMethod.GET)
    public String signUpForm(Model model, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            return "redirect:/";
        }
        SignUpModel user = new SignUpModel();
        System.out.println("[GET] signUpForm");
        model.addAttribute("user", user);
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if ("error".equals(errorMessage)) {
            model.addAttribute("errorMessage", "Đăng kí thất bại");
        }
        request.getSession().removeAttribute("errorMessage");
        return "web/auth/register";
    }


    @GetMapping(path = "/auth/verify-code")
    public String showVerifyCodePage(Model model) {
        model.addAttribute("verifyCodeRequest", new OTPCodeModel());
        return "web/auth/verify-code";
    }

    @PostMapping(path = "/auth/verify-code")
    public String verifyCode(@ModelAttribute("verifyCodeRequest") OTPCodeModel verifyCodeRequest, BindingResult result, Model model) {
        // Find the user by email
        User user = userService.findByEmail(verifyCodeRequest.getEmail()).orElse(null);

        if (user == null) {
            result.rejectValue("email", null, "Invalid email");
            return "web/auth/verify-code";
        }

        // Check if the entered code matches the generated code
        if (verifyCodeRequest.getCode().equals(user.getCode())) {
            // Update user's isEnabled status
            user.setIsEnabled(true);
            userService.save(user);
            return "redirect:/auth/login";
        } else {
            result.rejectValue("code", null, "Invalid code");
            return "web/auth/verify-code";
        }
    }


    @PostMapping(path = "/auth/register/save")
    public String signUpPostForm(@ModelAttribute("user") @Valid SignUpModel userReq, BindingResult result,
                                 Model model, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (result.hasErrors()) {
            model.addAttribute("user", userReq);
            request.getSession().setAttribute("errorMessage", "error");
            return "redirect:/auth/register";
        }

        User user = userService.findByEmail(userReq.getEmail()).orElse(null);
        if (user != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
            return "redirect:/auth/register";
        }
        Set<Roles> role = new HashSet<>();
        Roles roleuser = roleRepository.findById(2).orElse(null);
        if (roleuser != null) {
            role.add(roleuser);
        }

        user = User.builder()
                .isEnabled(false)
                .address(userReq.getAddress())
                .phone(userReq.getPhone())
                .email(userReq.getEmail())
                .password(userReq.getPasswordHash())
                .fullName(userReq.getFullName())
                .roles(role)
                .build();

        user.setPasswordHash(passwordEncoder.encode(userReq.getPasswordHash()));
        String randomCode = email.getRandom();
        user.setCode(randomCode);
        email.sendEmail(user);

        userService.save(user);
        OTPCodeModel verifyCodeRequest = new OTPCodeModel();
        verifyCodeRequest.setEmail(userReq.getEmail());

        model.addAttribute("verifyCodeRequest", verifyCodeRequest);
        return "redirect:/auth/verify-code?="
                ;
    }

    @GetMapping("/auth/reset-password")
    public String showResetPasswordForm(Model model) {
        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        return "web/auth/reset-password";
    }

    @PostMapping("/auth/reset-password")
    public String processResetPassword(@ModelAttribute("resetPasswordRequest") @Valid ResetPasswordRequest resetPasswordRequest, BindingResult result, Model model, HttpServletRequest request) {
        User user = userService.findByEmail(resetPasswordRequest.getEmail()).orElse(null);

        if (user == null) {
            // Handle invalid email
            result.rejectValue("email", null, "Invalid email");
            return "web/auth/reset-password";
        }
        String randomCode = email.getRandom();
        user.setCode(randomCode);
        email.sendEmail(user);
        userService.save(user);


        request.getSession().setAttribute("resetPasswordRequest", resetPasswordRequest);

        return "redirect:/auth/enter-verification-code";
    }

    @GetMapping("/auth/enter-verification-code")
    public String showEnterVerificationCodeForm(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("resetPasswordRequest", request.getSession().getAttribute("resetPasswordRequest"));
        return "web/auth/enter-verification-code";
    }

    @PostMapping("/auth/enter-verification-code")
    public String processEnterVerificationCode(@ModelAttribute("resetPasswordRequest") @Valid ResetPasswordRequest resetPasswordRequest, BindingResult result, Model model) {
        User user = userService.findByEmail(resetPasswordRequest.getEmail()).orElse(null);
        if (user == null) {
            result.rejectValue("email", null, "Invalid email");
            return "web/auth/reset-password";
        }

        // Check if the entered code matches the generated code
        if (resetPasswordRequest.getCode().equals(user.getCode())) {
            return "redirect:/auth/enter-new-password";
        } else {
            result.rejectValue("code", null, "Invalid code");
            return "web/auth/enter-verification-code";
        }
    }

    @GetMapping("/auth/enter-new-password")
    public String showEnterNewPasswordForm(Model model, HttpServletRequest request) {

        model.addAttribute("resetPasswordRequest", request.getSession().getAttribute("resetPasswordRequest"));

        request.getSession().removeAttribute("resetPasswordRequest");

        return "web/auth/enter-new-password";
    }

    @PostMapping("/auth/enter-new-password")
    public String processEnterNewPassword(@ModelAttribute("resetPasswordRequest") @Valid ResetPasswordRequest resetPasswordRequest, BindingResult result, Model model) {
        User user = userService.findByEmail(resetPasswordRequest.getEmail()).orElse(null);
        if (user == null) {
            // Handle invalid email
            result.rejectValue("email", null, "Invalid email");
            return "web/auth/reset-password";
        }
        user.setPasswordHash(passwordEncoder.encode(resetPasswordRequest.getConfirmPassword()));
        userService.save(user);
        return "redirect:/auth/login";
    }


    @GetMapping("/auth/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth/login";
    }

    @PostMapping("auth/updateinfor")
    @ResponseBody
    public ResponseEntity<?> saveOrUpdate(@Valid @ModelAttribute("user") UserModel userModel, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra lỗi
        if (result.hasErrors()) {
            response.put("status", "error");
            response.put("message", "Có lỗi xảy ra, vui lòng kiểm tra lại thông tin!");
            response.put("errors", result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        User entity = new User();
        BeanUtils.copyProperties(userModel, entity);

        // Cập nhật thông tin người dùng
        userService.updateUser(entity);

        response.put("status", "success");
        response.put("message", userModel.getIsEdit()
                ? "Thông tin người dùng đã được cập nhật thành công!"
                : "Người dùng đã được lưu thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("auth/updateAddress")
    @ResponseBody
    public ResponseEntity<?> updateAddress( @RequestParam("homeaddress") String homeAddress,
                                           @RequestParam("town") String town,
                                           @RequestParam("district") String district,
                                           @RequestParam("city") String city) {

        Map<String, Object> response = new HashMap<>();
        String address;

            // Tạo địa chỉ đầy đủ
            address = String.format("%s, %s, %s, %s", homeAddress, town, district, city);

            // Lấy người dùng hiện tại (ví dụ: qua SecurityContextHolder hoặc session)
        User currentUser = userService.getUserLogged();
        if (currentUser == null) {
            response.put("status", "error");
            response.put("message", "Người dùng không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
        currentUser.setAddress(address);
        userService.updateUser(currentUser);
            // Trả về phản hồi thành công
            response.put("status", "success");
            response.put("message", "Cập nhật địa chỉ thành công!");
            response.put("address", address);
            return ResponseEntity.ok(response);
    }

}