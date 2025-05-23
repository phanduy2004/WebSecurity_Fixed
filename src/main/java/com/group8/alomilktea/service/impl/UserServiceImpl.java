package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.common.enums.Status;
import com.group8.alomilktea.config.security.AuthUser;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.repository.UserRepository;
import com.group8.alomilktea.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
	@Autowired
	UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public long count() {
		return userRepository.count();
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	public User findById(Integer id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteById(Integer id) {
		userRepository.deleteById(id);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Long findShipCIdByUser(String email) {
		return userRepository.findShipCIdByUser(email);
	}

	@Override
	public User updateUser(User model) {
		Integer userId = model.getUserId();
		if (userId != null) {
			Optional<User> userOptional = userRepository.findById(userId);
			if (userOptional.isPresent()) {
				User user = userOptional.get();

				if (model.getFullName() != null && !model.getFullName().isEmpty()) {
					user.setFullName(model.getFullName());
				}
				if (model.getUsername() != null && !model.getUsername().isEmpty()) {
					user.setUsername(model.getUsername());
				}
				if (model.getEmail() != null && !model.getEmail().isEmpty()) {
					user.setEmail(model.getEmail());
				}
				if (model.getAddress() != null && !model.getAddress().isEmpty()) {
					user.setAddress(model.getAddress());
				}
				if (model.getPhone() != null && !model.getPhone().isEmpty()) {
					user.setPhone(model.getPhone());
				}
				model.setPasswordHash(user.getPasswordHash());

				return userRepository.save(user);
			}
		}
		return null;
	}

	@Override
	public User updateAddress(String email, String newAddress) {

		User user = userRepository.findByEmail(email).get();
		user.setAddress(newAddress);
		return userRepository.save(user);
	}

	@Override
	public Page<User> getAll(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 10);
		return userRepository.findAll(pageable);
	}

	public User login(String email, String passwd) {
		User user = userRepository.findByEmail(email).get();
		if (user != null && passwd.equals(user.getPasswordHash())) {
			return user;
		}
		return null;
	}

	@Override
	public Optional<User> getByUserNameOrEmail(String username) {
		return userRepository.findByUsernameOrEmail(username);
	}

	@Override
	public User getUserLogged() {
		Object authen = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (authen instanceof AuthUser) {
			String email = ((AuthUser)authen).getEmail();
			Optional<User> optUser = findByEmail(email);
			if (optUser.isPresent()) {
				User user = optUser.get();
				return user;
			}
		}
		return null;
	}

	@Override
	public List<User> findAllShippers() {
		return userRepository.findAllShippers();
	}

	@Override
	public void saveUserOnline(User user) {
		user.setStatus(Status.ONLINE);
		userRepository.save(user);
	}

	@Override
	public void disconnect(User user) {
		var storedUser = userRepository.findById(user.getUserId()).orElse(null);
		if (storedUser != null) {
			storedUser.setStatus(Status.OFFLINE);
			userRepository.save(storedUser);
		}
	}

	@Override
	public List<User> findConnectedUsers() {
		return userRepository.findAllByStatus(Status.ONLINE);
	}
}