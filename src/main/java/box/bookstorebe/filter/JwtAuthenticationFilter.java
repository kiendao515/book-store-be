package box.bookstorebe.filter;

import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.exception.ResourceNotFoundException;
import box.bookstorebe.service.auth.JwtService;
import box.bookstorebe.util.BeanUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.split(" ")[1].trim();
        TokenDecode tokenDecode = jwtService.extractToken(jwt);
        if (tokenDecode.getAccountId() == null || tokenDecode.getEmail() == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(tokenDecode.getEmail());
        if (!jwtService.isTokenValid(jwt, userDetails)) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        RequestScope requestScope = null;
        try {
            requestScope = BeanUtils.getBean(RequestScope.class);
            requestScope.setAccountId(tokenDecode.getAccountId());
            requestScope.setRole(Role.valueOf(tokenDecode.getRole())); // add role for this function
            requestScope.setEmail(tokenDecode.getEmail());
        } catch (ResourceNotFoundException e) {
            log.error("Error when get bean: {}", e.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
