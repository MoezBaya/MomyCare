# Security Audit - Quick Reference Card

**Last Updated**: 2026-05-03  
**Status**: Ready for Implementation

---

## 🚨 CRITICAL Issues (Fix Immediately)

### 1. JWT XSS Vulnerability
**File**: `JwtUtils.java:63`  
**Problem**: `httpOnly(false)` allows JavaScript to steal JWT tokens  
**Fix**: Change to `httpOnly(true)` + `secure(true)` + `sameSite("Strict")`  
**Risk**: Account takeover via XSS

### 2. Type Mismatch: roles
**File**: `UserInfoResponse.java`  
**Problem**: Field is `String role` but code passes `List<String> roles`  
**Fix**: Change to `List<String> roles`  
**Risk**: Runtime TypeError on every login

### 3. Wrong Authentication Import
**Files**: `AuthService.java`, `AuthController.java`  
**Problem**: Uses Tomcat's `Authentication` instead of Spring's  
**Fix**: Import `org.springframework.security.core.Authentication`  
**Risk**: Dependency injection fails

### 4. Missing hashCode()
**File**: `UserDetailsImpl.java`  
**Problem**: Has `equals()` but no `hashCode()`  
**Fix**: Add `hashCode()` method  
**Risk**: Breaks HashMap/HashSet usage

---

## ⚠️ HIGH Priority Issues (Fix Before Production)

| Issue | File | Fix | Time |
|-------|------|-----|------|
| User-Role OneToOne | User.java | Change to ManyToMany | 15 min |
| NPE in UserDetailsImpl.build() | UserDetailsImpl.java | Handle null, support multiple roles | 10 min |
| Missing imports | AuthServiceImpl.java | Add GrantedAuthority import | 2 min |
| Weak passwords | SignupRequest.java | Require min 12 chars + complexity | 5 min |
| Repository field mismatch | UserRepository.java | Use login/email, not userName | 5 min |
| Incomplete WebSecurityConfig | WebSecurityConfig.java | Complete CommandLineRunner | 15 min |

---

## 📋 File-by-File Checklist

### User.java ✅
```java
// CHANGE THIS:
@OneToOne private Role role;

// TO THIS:
@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
private Set<Role> roles = new HashSet<>();
```

### UserDetailsImpl.java ✅
```java
// ADD THIS:
implements Serializable
private static final long serialVersionUID = 1L;

// UPDATE build() METHOD:
List<GrantedAuthority> authorities = user.getRoles().stream()
    .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
    .toList();

// ADD THIS METHOD:
@Override
public int hashCode() {
    return Objects.hash(id);
}
```

### UserInfoResponse.java ✅
```java
// CHANGE THIS:
private String role;

// TO THIS:
private List<String> roles;
```

### SignupRequest.java ✅
```java
// CHANGE THIS:
@Size(min = 6, max = 40)

// TO THIS:
@Size(min = 12, max = 128)
@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{12,}$", ...)
```

### LoginRequest.java ✅
```java
// REMOVE THIS:
@Component
```

### UserRepository.java ✅
```java
// REMOVE THESE:
// Optional<User> findByUserName(String username);
// boolean existsByUserName(String username);

// KEEP/ADD THESE:
Optional<User> findByLogin(String login);
Optional<User> findByEmail(String email);
boolean existsByLogin(String login);
boolean existsByEmail(String email);
```

### AuthService.java ✅
```java
// FIX IMPORT:
import org.springframework.security.core.Authentication;  // NOT org.apache.tomcat...
```

### AuthController.java ✅
```java
// FIX IMPORT:
import org.springframework.security.core.Authentication;  // NOT org.apache.tomcat...
```

### AuthServiceImpl.java ✅
```java
// ADD IMPORT:
import org.springframework.security.core.GrantedAuthority;

// UPDATE login() AND register():
List<String> roles = userDetails.getAuthorities().stream()
    .map(GrantedAuthority::getAuthority)
    .toList();

// Support ManyToMany in register():
Set<Role> userRoles = new HashSet<>();
userRoles.add(role);
user.setRoles(userRoles);
```

### JwtUtils.java ✅
```java
// FIX generateJwtCookie():
ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
    .path("/api")
    .maxAge(24 * 60 * 60)
    .httpOnly(true)        // WAS: false
    .secure(true)          // ADD THIS
    .sameSite("Strict")    // ADD THIS
    .build();

// REMOVE: System.out.println statements
```

### AuthTokenFilter.java ✅
```java
// REPLACE @Autowired with constructor injection:
private final JwtUtils jwtUtils;
private final UserDetailsService userDetailsService;

public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
    this.jwtUtils = jwtUtils;
    this.userDetailsService = userDetailsService;
}
```

### WebSecurityConfig.java ✅
```java
// COMPLETE the initData() CommandLineRunner method
// It's currently incomplete with abrupt end

// Use proper RoleName enum (ROLE_PATIENTE, ROLE_GYNECOLOGUE, ROLE_ADMIN)
// NOT the undefined AppRole enum
```

---

## 🧪 Quick Test After Each Fix

```bash
# Compile check
mvn clean compile

# Run existing tests
mvn test

# Test login with new password rules
POST /api/auth/signup
Body: {
  "login": "testuser",
  "email": "test@example.com",
  "password": "WeakPass123",  # Should be rejected
  "role": "ROLE_PATIENTE"
}
# Should fail: "Password must contain... special character"

# Test login with strong password
POST /api/auth/signup
Body: {
  "login": "testuser",
  "email": "test@example.com",
  "password": "StrongPass@123",  # Should be accepted
  "role": "ROLE_PATIENTE"
}
# Should succeed

# Test JWT security
GET /api/auth/user
# Check response headers - cookie should have HttpOnly, Secure, SameSite
# Open browser DevTools → Application → Cookies
# JWT cookie should NOT be readable by JavaScript
```

---

## 🔒 Security Verification

```javascript
// Open browser console and run this:
// Result should be empty if cookies are properly httpOnly

console.log(document.cookie);
// Should print nothing or only non-JWT cookies
// ✅ If JWT cookie is missing: Security is correct
// ❌ If JWT cookie is visible: httpOnly is not set correctly
```

---

## 📊 Impact Assessment

### Backward Compatibility
| Component | Breaking? | Migration Path |
|-----------|-----------|-----------------|
| User.roles (OneToOne→ManyToMany) | YES | SQL script required |
| UserInfoResponse (String→List) | YES | Frontend must handle List |
| Password validation | Soft | New users get stricter rules |
| Imports | NO | Pure code fix |
| JwtUtils | NO | Transparent to frontend (cookie header only) |

### Performance Impact
- ✅ No performance degradation
- ✅ ManyToMany with eager loading properly configured
- ✅ JWT validation unchanged
- ✅ Encoding/hashing unchanged

### Deployment Checklist

1. ✅ Backup production database
2. ✅ Test on staging environment
3. ✅ Create database migration script
4. ✅ Notify frontend team about UserInfoResponse change
5. ✅ Plan maintenance window if needed (for DB migration)
6. ✅ Rollback plan ready
7. ✅ Verify JWT cookies in browser after deployment
8. ✅ Monitor logs for exceptions
9. ✅ Test login/logout flows
10. ✅ Verify new password requirements work

---

## 🔍 Common Mistakes to Avoid

❌ **Mistake**: Forget to add `@JoinTable` when changing to ManyToMany
```java
@ManyToMany
private Set<Role> roles;  // ❌ Missing @JoinTable annotation
```
✅ **Fix**: Add both annotations
```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "user_role", ...)
private Set<Role> roles;
```

---

❌ **Mistake**: Keep httpOnly=false after "fixing" JWT
```java
.httpOnly(false)  // ❌ Still vulnerable!
```
✅ **Fix**: Set to true
```java
.httpOnly(true)   // ✅ XSS-proof
```

---

❌ **Mistake**: Miss the `@Serial` annotation on UserDetailsImpl
```java
public class UserDetailsImpl implements Serializable {
    // Missing serialVersionUID
}
```
✅ **Fix**: Add proper serialization support
```java
public class UserDetailsImpl implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
```

---

❌ **Mistake**: Update only UserRepository but forget to update UserDetailsServiceImpl
```java
// UserRepository changed to use login
Optional<User> findByLogin(String login);

// But UserDetailsServiceImpl still uses old method names
userRepository.findByUserName(username);  // ❌ Method doesn't exist
```

---

## 📞 When Things Go Wrong

| Problem | Solution |
|---------|----------|
| Login returns 500 error | Check UserRepository methods exist and are called correctly |
| JWT cookie not sent | Verify httpOnly/secure flags are set, check HTTPS in production |
| Password validation always fails | Check regex pattern matches requirements, test with valid password |
| "No property found" exception | Verify UserRepository methods match User entity field names |
| ClassCastException on roles | Verify UserInfoResponse now uses List<String> roles |
| Multiple roles not working | Check User.roles is Set<Role> with @ManyToMany, not @OneToOne |

---

## ✅ Final Verification Checklist

Before marking complete:

- [ ] All imports are corrected
- [ ] JWT has httpOnly=true, secure=true, sameSite=Strict
- [ ] UserDetailsImpl has hashCode() method
- [ ] UserInfoResponse uses List<String> roles
- [ ] SignupRequest validates password complexity
- [ ] UserRepository uses correct field names
- [ ] WebSecurityConfig CommandLineRunner is complete
- [ ] Database migration tested
- [ ] Tests pass with new ManyToMany role system
- [ ] Frontend updated to handle List<String> roles in response
- [ ] JWT cookies verified as httpOnly in browser
- [ ] Login/logout flows work end-to-end

