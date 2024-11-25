package box.bookstorebe.service.auth;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.filter.TokenDecode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public TokenDecode extractToken(String token) {
        TokenDecode tokenDecode = new TokenDecode();
        try {
            tokenDecode.setAccountId(extractClaim(token, claims -> claims.get("account_id", String.class)));
            tokenDecode.setEmail(extractClaim(token, claims -> claims.get("email", String.class)));
            tokenDecode.setRole(extractClaim(token, claims -> claims.get("role", String.class)));
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }

        return tokenDecode;
    }


    public String generateToken(AccountDocument user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("account_id", user.getId());
        extraClaims.put("email", user.getEmail());
        extraClaims.put("role", user.getRole());
        return generateToken(extraClaims, user);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            AccountDocument userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuer(userDetails.getId())
                .setSubject(userDetails.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
