package com.example.demo.user;

import java.util.List;

import com.example.demo.exceptions.ResourceNotFoundException;

public interface UserService {

	/**
	 * Create a new user.
	 * @param createUserDto
	 * @return
	 */
	User save(CreateUserDto createUserDto);

	List<User> findAll();

	void delete(String id);

	User findByEmail(String email) throws ResourceNotFoundException, ResourceNotFoundException;

	User findById(String id) throws ResourceNotFoundException;

	User update(String id, UpdateUserDto updateUserDto) throws ResourceNotFoundException;

	void update(User user);

	User updatePassword(String id, UpdatePasswordDto updatePasswordDto) throws ResourceNotFoundException;

	void updatePassword(String id, String newPassword) throws ResourceNotFoundException;

}
