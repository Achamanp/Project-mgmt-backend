package com.projectManagementApp.config;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectManagementApp.entities.Role;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.UnSupportedOAuthProviderException;
import com.projectManagementApp.repositories.RoleRepository;
import com.projectManagementApp.security.service.UserDetailsImpl;
import com.projectManagementApp.services.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Lazy
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtHelper jwtUtils;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        
        String email = "";
        String name = "";
        String username = "";
        String idAttributeKey = "";
        String provider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        
        // Handle different OAuth providers
        if ("google".equals(provider)) {
            email = Optional.ofNullable(attributes.get("email")).map(Object::toString).orElse("");
            name = Optional.ofNullable(attributes.get("name")).map(Object::toString).orElse("");
            username = email.isEmpty() ? "" : email.split("@")[0];
            idAttributeKey = "sub";
        } else if ("github".equals(provider)) {
            // Try to get email from attributes first
            email = Optional.ofNullable(attributes.get("email")).map(Object::toString).orElse("");
            
            // If no email in attributes, fetch it from GitHub API
            if (email.isEmpty()) {
                try {
                    email = fetchGitHubEmail(oAuth2AuthenticationToken);
                } catch (Exception e) {
                    logger.error("Error fetching GitHub email", e);
                    // Create a fallback email using GitHub username
                    username = Optional.ofNullable(attributes.get("login")).map(Object::toString).orElse("");
                    if (!username.isEmpty()) {
                        email = username + "@github.user";
                    }
                }
            }
            
            // Get name and username
            name = Optional.ofNullable(attributes.get("name")).map(Object::toString).orElse("");
            if (name.isEmpty()) {
                name = Optional.ofNullable(attributes.get("login")).map(Object::toString).orElse("");
            }
            username = Optional.ofNullable(attributes.get("login")).map(Object::toString).orElse("");
            idAttributeKey = "id";
        } else {
            // Unknown provider
           logger.error("Unsupported OAuth Provider");
        }
        
        // Verify idAttributeKey is set and exists in attributes
        if (idAttributeKey.isEmpty() || !attributes.containsKey(idAttributeKey)) {
            throw new RuntimeException("Invalid or missing ID attribute for provider: " + provider);
        }
        
        // Make sure we have an email (can be real or fallback)
        if (email.isEmpty()) {
            throw new RuntimeException("Unable to determine email for user authentication");
        }
        
        logger.info("Authenticating OAuth2 user: provider={}, username={}, email={}", provider, username, email);
        
        final String finalEmail = email;
        final String finalUsername = username;
        final String finalIdAttributeKey = idAttributeKey;
        
        userService.findByEmail(finalEmail)
                .ifPresentOrElse(user -> {
                    // Existing user logic
                    DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                            List.of(new SimpleGrantedAuthority(user.getRole().getRoleName())),
                            attributes,
                            finalIdAttributeKey  // Use final variable
                    );
                    Authentication securityAuth = new OAuth2AuthenticationToken(
                            oauthUser,
                            List.of(new SimpleGrantedAuthority(user.getRole().getRoleName())),
                            provider
                    );
                    SecurityContextHolder.getContext().setAuthentication(securityAuth);
                }, () -> {
                    // New user logic
                    User newUser = new User();
                    Optional<Role> userRole = roleRepository.findByRoleName("ROLE_USER");
                    if (userRole.isPresent()) {
                        newUser.setRole(userRole.get());
                    } else {
                        throw new RuntimeException("Default role not found");
                    }
                    newUser.setEmail(finalEmail);
                    newUser.setUsername(finalUsername);
                    newUser.setSignupMethod(provider);
                    userService.registerUser(newUser);
                    
                    DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                            List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName())),
                            attributes,
                            finalIdAttributeKey 
                    );
                    Authentication securityAuth = new OAuth2AuthenticationToken(
                            oauthUser,
                            List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName())),
                            provider
                    );
                    SecurityContextHolder.getContext().setAuthentication(securityAuth);
                });
        
        // JWT TOKEN LOGIC
        Set<SimpleGrantedAuthority> authorities = new HashSet<>(principal.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList()));
                
        User user = userService.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found"));
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName()));
        
        // Create UserDetailsImpl instance
        UserDetailsImpl userDetails = new UserDetailsImpl(
                null,
                username,
                email,
                null,
                authorities
        );
        
        // Generate JWT token
        String jwtToken = jwtUtils.generateToken(userDetails);
        logger.info("Generated JWT token for user: {}", email);
       
        try {
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/home")
                    .build().toUriString();
            
            
            this.setAlwaysUseDefaultTargetUrl(true);
            
            // Option 2: Return token in a cookie (can be read by JavaScript)
          
            // Log redirection
            logger.info("Redirecting to frontend with token: {}", targetUrl);
            
            super.onAuthenticationSuccess(request, response, authentication);
        } catch (ServletException | java.io.IOException e) {
            logger.error("Error during authentication success handling", e);
            // Fallback in case of error
            try {
                response.sendRedirect(frontendUrl);
            } catch (java.io.IOException ioe) {
                logger.error("Failed to execute fallback redirect", ioe);
            }
        }
    }
    
    /**
     * Fetches user email from GitHub API using the OAuth2 access token
     */
    private String fetchGitHubEmail(OAuth2AuthenticationToken token) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token.getName());
        
        if (client == null) {
            throw new IllegalStateException("OAuth2 authorized client not found");
        }
        
        String userEmailsEndpoint = "https://api.github.com/user/emails";
        
        // Create RestTemplate with OAuth2 token
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
            return execution.execute(request, body);
        });
        
        try {
            // Fetch user emails
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    userEmailsEndpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            
            List<Map<String, Object>> emails = response.getBody();
            if (emails != null && !emails.isEmpty()) {
                // Find primary email
                for (Map<String, Object> emailEntry : emails) {
                    Boolean primary = (Boolean) emailEntry.get("primary");
                    Boolean verified = (Boolean) emailEntry.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return emailEntry.get("email").toString();
                    }
                }
                
                // If no primary found, get the first verified email
                for (Map<String, Object> emailEntry : emails) {
                    Boolean verified = (Boolean) emailEntry.get("verified");
                    if (Boolean.TRUE.equals(verified)) {
                        return emailEntry.get("email").toString();
                    }
                }
                
                // If no verified email, take the first one
                if (!emails.isEmpty() && emails.get(0).get("email") != null) {
                    return emails.get(0).get("email").toString();
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching GitHub emails", e);
            throw e;
        }
        
        throw new RuntimeException("No email found in GitHub profile");
    }
}
