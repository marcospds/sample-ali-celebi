package com.example.demo.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = null;
	private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptEncoder; // Fails when injected by the constructor.

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptEncoder) {
        this.userRepository = userRepository;
        this.bCryptEncoder = bCryptEncoder;
        
        System.out.println("bCryptEncoder: " + String.valueOf(bCryptEncoder != null));
    }

    /**
     * Create a new user.
     * @param createUserDto
     * @return
     */
    @Override
    public User save(CreateUserDto createUserDto) {
        User newUser = new User();

        newUser.setEmail(createUserDto.getEmail());
        newUser.setFullName(createUserDto.getFullName());
        newUser.setPassword(bCryptEncoder.encode(createUserDto.getPassword()));
        newUser.setConfirmed(createUserDto.isConfirmed());
        newUser.setEnabled(createUserDto.isEnabled());
        newUser.setRole(createUserDto.getRole());

        return userRepository.save(newUser);
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) throws ResourceNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE);
        }

        return optionalUser.get();
    }

    @Override
    public User findById(String id) throws ResourceNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE);
        }

        return optionalUser.get();
    }

    @Override
    public User update(String id, UpdateUserDto updateUserDto) throws ResourceNotFoundException {
        User user = findById(id);

        if (updateUserDto.getFullName() != null) {
            user.setFullName(updateUserDto.getFullName());
        }

        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
        }

        return userRepository.save(user);
    }

    @Override
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public User updatePassword(String id, UpdatePasswordDto updatePasswordDto) throws ResourceNotFoundException {
        User user = findById(id);

        if (bCryptEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getPassword())) {
            user.setPassword(bCryptEncoder.encode(updatePasswordDto.getNewPassword()));
            return userRepository.save(user);
        }

        return null;
    }

    @Override
    public void updatePassword(String id, String newPassword) throws ResourceNotFoundException {
        User user = findById(id);
        user.setPassword(bCryptEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void confirm(String id) throws ResourceNotFoundException {
        User user = findById(id);
        user.setConfirmed(true);
        userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);

        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        User user = userOptional.get();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), user.isEnabled(), true, true, user.isConfirmed(), getAuthority(user)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority(user.getRole()/*.getName()*/));

        user.allPermissions().forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        return authorities;
    }

}