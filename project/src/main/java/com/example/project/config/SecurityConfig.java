package com.example.project.config;

import com.example.project.repositories.StudentRepository;
import com.example.project.repositories.TeacherRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


 
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public SecurityConfig(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/signup/**", "/css/**", "/js/**", "/images/**").permitAll() // Allow public access
                        .requestMatchers("/student/**").hasRole("STUDENT") // Student role for student paths
                        .requestMatchers("/teacher/**").hasRole("TEACHER") // Teacher role for teacher paths
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard-redirect", true)
                        .permitAll()
                        .defaultSuccessUrl("/student/dashboard", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            // Try to find as a student
            return studentRepository.findByEmail(email)
                    .map(student -> org.springframework.security.core.userdetails.User.builder()
                            .username(student.getEmail())
                            .password(student.getPassword())
                            .roles("STUDENT")
                            .build())
                    .or(() -> {
                        // If not found as a student, try as a teacher
                        return teacherRepository.findByEmail(email)
                                .map(teacher -> org.springframework.security.core.userdetails.User.builder()
                                        .username(teacher.getEmail())
                                        .password(teacher.getPassword())
                                        .roles("TEACHER")
                                        .build());
                    })
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
