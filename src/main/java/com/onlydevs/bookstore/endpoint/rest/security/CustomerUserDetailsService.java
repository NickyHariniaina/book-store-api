package com.onlydevs.bookstore.endpoint.rest.security;

import com.onlydevs.bookstore.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {
  private final CustomerRepository customerRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Customer not found: " + email));

    return User.builder()
        .username(customer.getEmail())
        .password(customer.getPassword())
        .roles(customer.getRole())
        .build();
  }
}
