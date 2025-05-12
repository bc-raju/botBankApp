package net.enjoy.springboot.registrationlogin.security;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.enjoy.springboot.registrationlogin.entity.User;
import net.enjoy.springboot.registrationlogin.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		User user = userRepository.findByName(name);
		if (user != null) {
			return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
					user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
							.collect(Collectors.toList()));
		} else {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
	}
}
