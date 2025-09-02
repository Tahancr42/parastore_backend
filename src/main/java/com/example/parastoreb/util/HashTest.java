import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashTest {
    public static void main(String[] args) {
        String hash = new BCryptPasswordEncoder().encode("admin");
        System.out.println("Hash BCrypt : " + hash);
    }
}
