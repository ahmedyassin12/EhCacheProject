package ehcache.example.ehCache.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_CREATE("admin:create"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),
    BOOK_READ("book:read"),
    BOOK_CREATE("book:create"),
    BOOK_UPDATE("book:update"),
    BOOK_DELETE("book:delete");

    @Getter
    private final String permission;

}


