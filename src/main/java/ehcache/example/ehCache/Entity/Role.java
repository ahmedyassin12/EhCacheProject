package ehcache.example.ehCache.Entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ehcache.example.ehCache.Entity.Permission.*;

@RequiredArgsConstructor
public enum Role {
    ADMIN(
            Set.of(  // Explicitly use Set.of() for clarity
                    ADMIN_CREATE,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_READ,
                    BOOK_CREATE,
                    BOOK_UPDATE,
                    BOOK_DELETE,
                    BOOK_READ
            )

    ),

    OMK(
            Set.of()
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission())) // Fixed: Added parentheses
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }



}
