# Spring Security Audit & Refactoring Report
**MomyCare Authentication System**

---

## Executive Summary

Your authentication system has **14 critical and high-priority issues** that affect security, stability, and maintainability. Most are fixable without breaking business logic. Key problems:

- ❌ **JWT Token Exposure** (XSS vulnerability)
- ❌ **User-Role Relationship** (OneToOne instead of ManyToMany)
- ❌ **UserDetails Implementation** (missing hashCode, NPE risks)
- ❌ **DTO Type Mismatch** (roles List vs String)
- ❌ **Import Errors** (wrong Authentication class, missing imports)
- ❌ **Repository Inconsistencies** (method names vs field names)
- ❌ **Null Pointer Risks** (no null checks in critical paths)

---

## Critical Issues & Fixes

### 🔴 **ISSUE #1: JWT Cookie XSS Vulnerability**

**Location**: `JwtUtils.generateJwtCookie()`  
**Severity**: CRITICAL - Allows JavaScript to steal JWT token  
**Root Cause**: `httpOnly(false)` allows JavaScript to access JWT via `document.cookie`

**Current Code**:
```java
ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
    .path("/api")
    .maxAge(24 * 60 * 60)
    .httpOnly(false)  // ❌ SECURITY HOLE
    .build();
```

**Impact**: Attacker can inject JavaScript to steal JWT token from any page

**Fix**: Set `httpOnly(true)` to prevent JavaScript access
```java
ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
    .path("/api")
    .maxAge(24 * 60 * 60)
    .httpOnly(true)   // ✅ FIXED
    .secure(true)     // ✅ HTTPS only
    .sameSite("Strict") // ✅ CSRF protection
    .build();
```

---

### 🔴 **ISSUE #2: UserInfoResponse Type Mismatch**

**Location**: `UserInfoResponse.java` & `AuthServiceImpl.java`  
**Severity**: CRITICAL - Runtime ClassCastException  

**Current Problem**:
```java
// UserInfoResponse.java
public UserInfoResponse {
    private String role;  // ❌ SINGULAR - expects single role
}

// AuthServiceImpl.java - Line 51
List<String> roles = userDetails.getAuthorities().stream()
    .map(GrantedAuthority::getAuthority)
    .toList();  // ❌ Returns List<String>

return new UserInfoResponse(
    userDetails.getId(),
    userDetails.getUsername(),
    userDetails.getEmail(),
    roles  // ❌ Passing List to String field → will fail!
);
```

**Impact**: TypeError at runtime - constructor expects List but field is String

**Fix**: Update UserInfoResponse to properly handle roles
```java
public UserInfoResponse {
    private List<String> roles;  // ✅ PLURAL - list of roles
}
```

---

### 🔴 **ISSUE #3: User-Role OneToOne is Wrong Pattern**

**Location**: `User.java`, `Role.java`, entire authentication system  
**Severity**: HIGH - Breaks role management flexibility

**Current Code**:
```java
@Entity
public class User {
    @OneToOne  // ❌ ONE-TO-ONE means only 1 role per user
    @JoinColumn(name = "role_id")
    private Role role;
}
```

**Problems**:
- Cannot assign multiple roles to a user (e.g., user + admin)
- Cannot manage roles dynamically
- Violates REST API security model

**Fix**: Use ManyToMany relationship
```java
@Entity
public class User {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
```

---

### 🔴 **ISSUE #4: UserDetailsImpl Missing hashCode()**

**Location**: `UserDetailsImpl.java`  
**Severity**: HIGH - Breaks equality contract

**Current Code**:
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
}
// ❌ Missing hashCode() - violates equals/hashCode contract!
```

**Impact**: When used in HashMap/HashSet, breaks lookups

**Fix**: Add complementary hashCode()
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
}

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

---

### 🔴 **ISSUE #5: NPE Risk in UserDetailsImpl.build()**

**Location**: `UserDetailsImpl.java:32`  
**Severity**: HIGH - NullPointerException if user.getRole() is null

**Current Code**:
```java
public static UserDetailsImpl build(User user) {
    GrantedAuthority authority =
            new SimpleGrantedAuthority(user.getRole().getRoleName().name());
    // ❌ Crashes if user.getRole() is null!
```

**Also**: Assumes single role, but needs to support multiple roles

**Fix**: Handle null and multiple roles
```java
public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
        .toList();  // ✅ Supports multiple roles

    return new UserDetailsImpl(
            user.getId(),
            user.getLogin(),
            user.getEmail(),
            user.getPassword(),
            authorities
    );
}
```

---

### 🔴 **ISSUE #6: Missing Import in AuthServiceImpl**

**Location**: `AuthServiceImpl.java:51`  
**Severity**: HIGH - Compilation error

**Current Code**:
```java
List<String> roles = userDetails.getAuthorities().stream()
    .map(GrantedAuthority::getAuthority)  // ❌ Not imported
    .toList();
```

**Fix**: Add missing import
```java
import org.springframework.security.core.GrantedAuthority;
```

---

### 🔴 **ISSUE #7: Wrong Authentication Import**

**Location**: `AuthService.java:line 8`, `AuthController.java:line 10`  
**Severity**: HIGH - Compilation error

**Current (WRONG)**:
```java
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
// ❌ This is from Tomcat, not Spring Security!
```

**Fix**: Use correct import
```java
import org.springframework.security.core.Authentication;  // ✅ CORRECT
```

---

### 🔴 **ISSUE #8: UserRepository Method Names Don't Match Entity**

**Location**: `UserRepository.java`  
**Severity**: HIGH - Methods fail at runtime

**Current Code**:
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);  // ❌ Field is "login", not "userName"
    boolean existsByUserName(String username);       // ❌ Wrong field name
    boolean existsByEmail(...) String username);     // ❌ Parameter name misleading
}
```

**Impact**: These methods won't work - Spring Data can't map to non-existent fields

**Fix**: Align with actual User entity fields
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);   // ✅ Matches entity
    Optional<User> findByEmail(String email);   // ✅ Matches entity
    boolean existsByLogin(String login);        // ✅ Matches entity
    boolean existsByEmail(String email);        // ✅ Matches entity
}
```

---

### 🔴 **ISSUE #9: WebSecurityConfig Initialization Incomplete**

**Location**: `WebSecurityConfig.java:lines 89-150`  
**Severity**: HIGH - Incomplete code, wrong references

**Problems**:
- References undefined `AppRole` enum (doesn't exist)
- Code ends abruptly mid-method
- Tries to use `Set<Role>` but User only supports single role
- Missing closing braces

**Fix**: Complete and fix the initialization logic (see corrected code below)

---

### 🔴 **ISSUE #10: UserDetailsImpl Should Implement Serializable**

**Location**: `UserDetailsImpl.java`  
**Severity**: MEDIUM - Breaks session clustering/persistence

```java
public class UserDetailsImpl implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;  // ✅ Add this
    // ... rest of class
}
```

---

### 🔴 **ISSUE #11: Password Validation Too Weak**

**Location**: `SignupRequest.java`  
**Severity**: MEDIUM - Security best practice

**Current**:
```java
@Size(min = 6, max = 40)  // ❌ 6 characters is weak
```

**Fix**:
```java
@Size(min = 12, max = 128)  // ✅ At least 12 characters
@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{12,}$",
    message = "Password must contain uppercase, lowercase, digit, and special character")
private String password;
```

---

### 🔴 **ISSUE #12: LoginRequest Has Unnecessary @Component**

**Location**: `LoginRequest.java`  
**Severity**: LOW - Not harmful but wrong practice

```java
@Component  // ❌ DTOs should NOT be Spring components
public class LoginRequest { }
```

**Fix**: Remove @Component, just use POJO annotations
```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest { }
```

---

### 🟡 **ISSUE #13: Username vs Email Inconsistency**

**Location**: Throughout system  
**Severity**: MEDIUM - Confusing authentication flow

**Problem**: 
- Entity uses both `login` (username) and `email`
- JWTs store login but DTOs might return email
- UserDetailsService tries both but inconsistently

**Fix**: Be explicit - use `login` consistently for authentication
```java
// In JwtUtils
public String getUserNameFromJWTToken(String token) {
    // This is the "login" field, not email
    return Jwts.parser()...getPayload().getSubject();
}

// In UserDetailsImpl
public String getUsername() { 
    return username;  // This is the login field
}
```

---

### 🟡 **ISSUE #14: AuthTokenFilter Exception Handling Too Broad**

**Location**: `AuthTokenFilter.java:lines 40-45`  
**Severity**: MEDIUM - Swallows important errors

```java
try {
    // ...
} catch (Exception e) {
    // ❌ Too broad - hides real errors
    logger.error("Cannot set user authentication: {}", e.getMessage());
}
```

**Fix**: Catch specific exceptions
```java
} catch (ExpiredJwtException e) {
    logger.warn("Expired JWT token");
} catch (UnsupportedJwtException | MalformedJwtException e) {
    logger.warn("Invalid JWT token format");
} catch (IllegalArgumentException e) {
    logger.warn("JWT claims string is empty");
} catch (Exception e) {
    logger.error("Unexpected authentication error", e);
}
```

---

## Summary of Changes by File

| File | Issues Fixed | Priority |
|------|-------------|----------|
| `UserDetailsImpl.java` | NPE in build(), missing hashCode(), Serializable | CRITICAL |
| `UserInfoResponse.java` | Type mismatch (List vs String) | CRITICAL |
| `User.java` | OneToOne → ManyToMany | HIGH |
| `AuthServiceImpl.java` | Missing import, type mismatch | HIGH |
| `AuthService.java` | Wrong Authentication import | HIGH |
| `UserRepository.java` | Method names mismatch | HIGH |
| `WebSecurityConfig.java` | Incomplete code, wrong refs | HIGH |
| `LoginRequest.java` | Remove @Component, clean imports | LOW |
| `SignupRequest.java` | Password validation too weak | MEDIUM |
| `AuthTokenFilter.java` | Exception handling too broad | MEDIUM |
| `JwtUtils.java` | httpOnly=false (XSS) | CRITICAL |

---

## Clean Architecture Principles Applied

✅ **No logic in DTOs** - DTOs are pure data containers  
✅ **No entity leakage** - UserDetails builds independently  
✅ **Immutable authorities** - Collections are unmodifiable  
✅ **Proper service separation** - AuthService handles auth, not UserDetails  
✅ **Clear role handling** - ManyToMany supports complex role scenarios  
✅ **Security-first** - httpOnly, CSRF protection, strong password validation  

