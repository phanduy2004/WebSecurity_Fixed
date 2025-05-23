package com.group8.alomilktea.controller.admin;

import com.group8.alomilktea.entity.Roles;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.service.IRoleService;
import com.group8.alomilktea.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/user")
public class UsersController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/list")
    public String showUserList(ModelMap model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<User> userPage = userService.findAll(PageRequest.of(page, size));
        List<User> users = userPage.getContent();

        // Map user roles for display
        var userRoleMap = users.stream()
                .collect(Collectors.toMap(
                        User::getUserId,
                        user -> user.getRoles().stream()
                                .map(role -> role.getRole().name()) // Get role name from enum
                                .collect(Collectors.joining(", "))
                ));


        var userStatusMap = users.stream()
                .collect(Collectors.toMap(
                        User::getUserId,
                        user -> (user.getIsEnabled() != null && user.getIsEnabled()) ? "Active" : "Lock"
                ));


        model.addAttribute("users", users);
        model.addAttribute("userRoleMap", userRoleMap);
        model.addAttribute("userStatusMap", userStatusMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "admin/users/apps-ecommerce-user-list";
    }

    @GetMapping("/add")
    public String addUser(ModelMap model) {
        List<Roles> roles = roleService.findAll(); // Get all roles for combobox

        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);  // Pass roles to the view
        model.addAttribute("isEdit", false);

        return "admin/users/apps-ecommerce-user-create";
    }

    @PostMapping("/add/save")
    public String saveUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("roleIds") List<Integer> roleIds, // Danh sách ID của vai trò
            ModelMap model) {

        // Tạo một đối tượng User mới
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setAddress(address);
        user.setPhone(phone);
        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(password);
        user.setPasswordHash(encodedPassword);

        // Lấy danh sách Roles từ danh sách ID
        Set<Roles> roles = roleService.findByIds(roleIds);
        user.setRoles(roles);

        // Lưu User vào cơ sở dữ liệu
        userService.save(user);

        // Gửi thông báo thành công
        model.addAttribute("message", "User created successfully!");
        return "redirect:/admin/user/list";
    }

    @PostMapping("/toggle-status/{userId}")
    public String toggleUserStatus(@PathVariable("userId") Integer userId, ModelMap model) {
        User user = userService.findById(userId);
        if (user != null) {
            // Đảm bảo trạng thái được cập nhật chính xác
            if (user.getIsEnabled() == null || !user.getIsEnabled()) {
                user.setIsEnabled(true); // Nếu trạng thái là null hoặc false, thì đặt thành true
            } else {
                user.setIsEnabled(false); // Ngược lại, đặt thành false
            }

            // Lưu lại thay đổi
            userService.save(user);
            model.addAttribute("message", "User status updated successfully!");
        } else {
            model.addAttribute("errorMessage", "User not found!");
        }
        return "redirect:/admin/user/list";
    }


    @GetMapping("/edit/{userId}")
    public String editUser(@PathVariable Integer userId, ModelMap model) {
        User user = userService.findById(userId);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found!");
            return "redirect:/admin/user/list";
        }

        List<Roles> roles = roleService.findAll(); // Get all roles for combobox

        model.addAttribute("user", user);
        model.addAttribute("roles", roles); // Pass roles to the view
        model.addAttribute("isEdit", true);

        return "admin/users/apps-ecommerce-user-create";
    }

    @PostMapping("/edit/save")
    public String updateUser(
            @RequestParam("id") Integer userId,
            @RequestParam("name") String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("roleIds") List<Integer> roleIds,
            ModelMap model) {

        // Tìm người dùng hiện tại trong cơ sở dữ liệu
        User existingUser = userService.findById(userId);
        if (existingUser == null) {
            model.addAttribute("errorMessage", "User not found!");
            return "redirect:/admin/user/list";
        }

        // Cập nhật các trường được chỉ định
        existingUser.setUsername(username);
        existingUser.setPhone(phone);
        existingUser.setAddress(address);

        // Cập nhật mật khẩu nếu được cung cấp
        if (password != null && !password.trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            existingUser.setPasswordHash(encodedPassword);
            existingUser.setPassword(password);
        }

        // Cập nhật danh sách Role
        Set<Roles> roles = roleService.findByIds(roleIds);
        existingUser.setRoles(roles);

        // Lưu lại thông tin người dùng
        userService.save(existingUser);
        model.addAttribute("message", "User updated successfully!");
        return "redirect:/admin/user/list";
    }


    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Integer userId, ModelMap model) {
        User user = userService.findById(userId);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found!");
        } else {
            userService.deleteById(userId);
            model.addAttribute("message", "User deleted successfully!");
        }
        return "redirect:/admin/user/list";
    }
}
