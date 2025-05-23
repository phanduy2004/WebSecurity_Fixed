package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {

	void deleteById(Integer id);

	long count();

	// Lưu user
	void save(User user);

	// Xóa user theo ID
	User findById(Integer id);

	List<User> findAll();

	Page<User> findAll(Pageable pageable);

	User updateUser(User model);

	Optional<User> findByEmail(String email);

	Long findShipCIdByUser (String email);

	User updateAddress(String email, String newAddress);

	Page<User> getAll(Integer pageNo);

	Optional<User> getByUserNameOrEmail(String username);

	User getUserLogged();

	List<User> findAllShippers();

	public void saveUserOnline(User user);
	public void disconnect(User user);
	public List<User> findConnectedUsers();

}