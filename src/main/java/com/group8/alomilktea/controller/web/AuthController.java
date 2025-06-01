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
import com.group8.alomilktea.utils.Constant;
import com.group8.alomilktea.utils.Email;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String login(Model model, Principal principal,
                        @RequestParam(name = "message", required = false) String message,
                        @RequestParam(name = "attempts", required = false) String attemptsStr,
                        HttpServletRequest request) {
        if (principal != null) {
            return "redirect:/";
        }
        if ("error".equals(message)) {
            if (attemptsStr != null) {
                try {
                    int attemptsMade = Integer.parseInt(attemptsStr);
                    int remainingAttempts = Constant.MAX_LOGIN_FAILED_ATTEMPTS - attemptsMade;
                    if (remainingAttempts >= 0) { // Show remaining attempts only if not locked by attempts yet
                        model.addAttribute("errorMessage",
                                "Tài khoản hoặc mật khẩu không đúng. Bạn còn " + remainingAttempts + " lần thử.");
                    } else {
                        model.addAttribute("errorMessage",
                                "Tài khoản hoặc mật khẩu không đúng.");
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng.");
                }
            } else {
                model.addAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng.");
            }
        } else if ("locked".equals(message)) {
            model.addAttribute("errorMessage", "Tài khoản của bạn đã bị khóa do nhập sai mật khẩu quá nhiều lần. Vui lòng liên hệ quản trị viên.");
        }
        return "web/auth/login";
    }

    @RequestMapping(path = "/auth/login", method = RequestMethod.POST)
    public String loginPost(Model model, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            request.getSession().setAttribute("username", principal.getName());
            String username = principal.getName();
            HttpSession session = request.getSession();
            session.removeAttribute("failedLoginAttempts_" + username);
            return "redirect:/";
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
        User user = userService.findByEmail(verifyCodeRequest.getEmail()).orElse(null);

        if (user == null) {
            result.rejectValue("email", null, "Invalid email");
            return "web/auth/verify-code";
        }

        if (verifyCodeRequest.getCode().equals(user.getCode())) {
            user.setIsEnabled(true);
            user.setCode(null); // Clear verification code after use
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

        if (userService.findByEmail(userReq.getEmail()).isPresent() || userService.getByUserNameOrEmail(userReq.getUsername()).isPresent()) {
            result.rejectValue("email", null, "There is already an account registered with that email or username");
            model.addAttribute("user", userReq);
            model.addAttribute("registrationError", "Email hoặc tên đăng nhập đã được sử dụng.");
            return "web/auth/register";
        }

        Set<Roles> roles = new HashSet<>();
        Roles roleUser = roleRepository.findById(2).orElse(null); // Assuming role ID 2 is USER
        if (roleUser != null) {
            roles.add(roleUser);
        }

        User user = User.builder()
                .isEnabled(false)
                .address(userReq.getAddress())
                .phone(userReq.getPhone())
                .email(userReq.getEmail())
                .password(userReq.getPasswordHash())
                .fullName(userReq.getFullName())
                .username(userReq.getUsername())
                .roles(roles)
                .build();

        user.setPasswordHash(passwordEncoder.encode(userReq.getPasswordHash()));
        String randomCode = email.getRandom();
        user.setCode(randomCode);

        boolean emailSent = email.sendEmail(user);
        if(!emailSent){
            model.addAttribute("user", userReq);
            model.addAttribute("registrationError", "Không thể gửi email xác thực. Vui lòng thử lại.");
            return "web/auth/register";
        }

        userService.save(user);

        model.addAttribute("verifyEmail", userReq.getEmail());
        return "redirect:/auth/verify-code?email=" + userReq.getEmail();
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
            result.rejectValue("email", null, "Invalid email");
            return "web/auth/reset-password";
        }
        String randomCode = email.getRandom();
        user.setCode(randomCode);
        email.sendEmail(user);
        userService.save(user);

        request.getSession().setAttribute("resetEmail", resetPasswordRequest.getEmail());
        return "redirect:/auth/enter-verification-code";
    }

    @GetMapping("/auth/enter-verification-code")
    public String showEnterVerificationCodeForm(Model model, HttpServletRequest request) {
        String emailForVerification = (String) request.getSession().getAttribute("resetEmail");
        if (emailForVerification == null) {
            return "redirect:/auth/reset-password";
        }
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setEmail(emailForVerification);
        model.addAttribute("resetPasswordRequest", resetRequest);
        return "web/auth/enter-verification-code";
    }

    @PostMapping("/auth/enter-verification-code")
    public String processEnterVerificationCode(@ModelAttribute("resetPasswordRequest") @Valid ResetPasswordRequest resetPasswordRequest, BindingResult result, Model model, HttpServletRequest request) {
        User user = userService.findByEmail(resetPasswordRequest.getEmail()).orElse(null);
        if (user == null) {
            result.rejectValue("email", null, "Invalid email");
            model.addAttribute("resetPasswordRequest", resetPasswordRequest);
            return "web/auth/enter-verification-code";
        }

        if (resetPasswordRequest.getCode().equals(user.getCode())) {
            user.setCode(null); // Clear code after successful verification
            userService.save(user);
            request.getSession().setAttribute("resetEmailValidated", user.getEmail()); // Store validated email for next step
            return "redirect:/auth/enter-new-password";
        } else {
            result.rejectValue("code", null, "Invalid code");
            model.addAttribute("resetPasswordRequest", resetPasswordRequest);
            return "web/auth/enter-verification-code";
        }
    }

    @GetMapping("/auth/enter-new-password")
    public String showEnterNewPasswordForm(Model model, HttpServletRequest request) {
        String validatedEmail = (String) request.getSession().getAttribute("resetEmailValidated");
        if(validatedEmail == null){
            return "redirect:/auth/login"; // Or some error page
        }
        ResetPasswordRequest newPasswordRequest = new ResetPasswordRequest();
        newPasswordRequest.setEmail(validatedEmail);
        model.addAttribute("resetPasswordRequest", newPasswordRequest);
        return "web/auth/enter-new-password";
    }

    @PostMapping("/auth/enter-new-password")
    public String processEnterNewPassword(@ModelAttribute("resetPasswordRequest") @Valid ResetPasswordRequest resetPasswordRequest, BindingResult result, Model model, HttpServletRequest request) {
        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Passwords do not match");
        }
        if (result.hasErrors()) {
            model.addAttribute("resetPasswordRequest", resetPasswordRequest);
            return "web/auth/enter-new-password";
        }

        User user = userService.findByEmail(resetPasswordRequest.getEmail()).orElse(null);
        if (user == null) {
            result.rejectValue("email", null, "Invalid email or session expired");
            model.addAttribute("resetPasswordRequest", resetPasswordRequest);
            return "web/auth/enter-new-password";
        }
        user.setPasswordHash(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userService.save(user);
        request.getSession().removeAttribute("resetEmailValidated");
        request.getSession().removeAttribute("resetEmail");
        return "redirect:/auth/login?message=reset_success";
    }


    @GetMapping("/auth/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth/login?logout";
    }

    @PostMapping("auth/updateinfor")
    @ResponseBody
    public ResponseEntity<?> saveOrUpdate(@Valid @ModelAttribute("user") UserModel userModel, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            response.put("status", "error");
            response.put("message", "Có lỗi xảy ra, vui lòng kiểm tra lại thông tin!");
            response.put("errors", result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        User entity = userService.findById(userModel.getUserId());
        if (entity == null) {
            response.put("status", "error");
            response.put("message", "Người dùng không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }

        BeanUtils.copyProperties(userModel, entity, "password", "passwordHash", "passwordSalt", "roles", "isEnabled", "code", "active", "isAdmin");


        userService.updateUser(entity);

        response.put("status", "success");
        response.put("message", "Thông tin người dùng đã được cập nhật thành công!");
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
        address = String.format("%s, %s, %s, %s", homeAddress, town, district, city);
        User currentUser = userService.getUserLogged();
        if (currentUser == null) {
            response.put("status", "error");
            response.put("message", "Người dùng không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
        currentUser.setAddress(address);
        userService.updateUser(currentUser);
        response.put("status", "success");
        response.put("message", "Cập nhật địa chỉ thành công!");
        response.put("address", address);
        return ResponseEntity.ok(response);
    }
}