package xin.qust.platform.config.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import xin.qust.platform.config.login.filter.EmailLoginFilter;
import xin.qust.platform.config.login.filter.TokenAuthenticationFilter;
import xin.qust.platform.config.login.filter.UserNameLoginFilter;
import xin.qust.platform.config.login.handler.TokenLogoutHandler;
import xin.qust.platform.config.login.handler.UserNameLoginAuthenticationFailureHandler;
import xin.qust.platform.config.login.handler.UserNameLoginAuthenticationSuccessHandler;
import xin.qust.platform.config.login.provider.EmailLoginProvider;
import xin.qust.platform.config.login.provider.UserNameLoginProvider;
import xin.qust.platform.config.login.token.JwtTokenManager;
import xin.qust.platform.config.login.token.TokenManager;
import xin.qust.platform.service.login.UserLoginService;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoginMethodConfig {

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private EmailLoginProvider emailLoginProvider;

    @Autowired
    private UserNameLoginProvider userNameLoginProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenLogoutHandler tokenLogoutHandler() {
        return new TokenLogoutHandler(jwtTokenManager);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(emailLoginProvider);
        providers.add(userNameLoginProvider);
        return new ProviderManager(providers);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(authenticationManager(), jwtTokenManager);
    }

    @Bean
    public EmailLoginFilter emailLoginFilter() {
        EmailLoginFilter emailLoginFilter = new EmailLoginFilter();
        emailLoginFilter.setAuthenticationManager(authenticationManager());
        return emailLoginFilter;
    }

    @Bean
    public UserNameLoginFilter userNameLoginFilter() {
        UserNameLoginFilter userNameLoginFilter = new UserNameLoginFilter();
        userNameLoginFilter.setAuthenticationManager(authenticationManager());
        userNameLoginFilter.setAuthenticationSuccessHandler(new UserNameLoginAuthenticationSuccessHandler(jwtTokenManager));
        userNameLoginFilter.setAuthenticationFailureHandler(new UserNameLoginAuthenticationFailureHandler());
        return userNameLoginFilter;
    }

}
