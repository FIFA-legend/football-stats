package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.dto.user.CreateUserDto;
import by.bsuir.football.dto.user.GetUserDto;
import by.bsuir.football.dto.user.UpdateUserDto;
import by.bsuir.football.entity.User;
import by.bsuir.football.entity.enums.Role;
import by.bsuir.football.repository.UserRepository;
import by.bsuir.football.service.UserService;
import by.bsuir.football.service.exceptions.user.DuplicateUsernameException;
import by.bsuir.football.service.exceptions.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getGoogleUserByUsername(String username) {
        String googleUsername = googleUsername(username);
        if (googleUsername == null) return null;
        return userRepository.findByUsername(googleUsername);
    }

    @Override
    public List<GetUserDto> getByPage(int page, int count) {
        Pageable pageable = PageRequest.of(page, count, Sort.by("username"));
        List<User> allUsersByPage = userRepository.findAll(pageable).toList();
        return allUsersByPage
                .stream()
                .map(user -> new GetUserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(CreateUserDto createUserDto) throws DuplicateUsernameException {
        String username = createUserDto.getUsername();
        User foundUser = userRepository.findByUsername(username);
        if (foundUser != null) {
            throw new DuplicateUsernameException();
        }

        String encryptedPassword = encoder.encode(createUserDto.getPassword());
        User user = new User(createUserDto.getUsername(), encryptedPassword, createUserDto.getEmail(), Role.USER);
        userRepository.save(user);
    }

    @Override
    public User saveGoogleUser(String username) {
        User user = new User();
        user.setUsername(googleUsername(username));
        user.setPassword(encoder.encode("GOOGLE_USER"));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    @Override
    public void update(UpdateUserDto updateUserDto) throws UserNotFoundException {
        String encryptedPassword = encoder.encode(updateUserDto.getPassword());
        User user = userRepository.findById(updateUserDto.getId()).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }

        user.setPassword(encryptedPassword);
        user.setEmail(updateUserDto.getEmail());
        userRepository.save(user);
    }

    @Override
    public void updateRole(Long id, Role role) throws UserNotFoundException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }

        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), convertRoles(user));
    }

    private Collection<GrantedAuthority> convertRoles(User user) {
        Set<GrantedAuthority> set = new HashSet<>();
        set.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return set;
    }

    private String googleUsername(String username) {
        String[] credentials = username.split(",");
        String nameString = null;
        for (String str : credentials) {
            if (str.trim().startsWith("name")) {
                nameString = str;
                break;
            }
        }
        if (nameString == null) return null;
        return nameString.substring(nameString.indexOf('=') + 1);
    }
}