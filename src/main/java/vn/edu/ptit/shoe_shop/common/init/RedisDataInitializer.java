package vn.edu.ptit.shoe_shop.common.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import vn.edu.ptit.shoe_shop.common.constant.RedisKeyConstants;
import vn.edu.ptit.shoe_shop.dto.UserCredentialDTO;
import vn.edu.ptit.shoe_shop.repository.UserRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;
    private final Logger log = LoggerFactory.getLogger(RedisDataInitializer.class);

    public RedisDataInitializer(UserRepository userRepository, RedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Redis cache warm-up process...");
        try {
            warmUpUserCredentials();
            log.info("Redis warm-up completed successfully.");
        } catch (Exception e) {
            log.info("ERROR during Redis warm-up: {}", e.getMessage(), e);
        }
    }

    private void warmUpUserCredentials(){
        List<UserCredentialDTO> credentials = this.userRepository.findAllUserCredentials();
        if (credentials.isEmpty()) {
            log.warn("No user data found in Database. Skipping warm-up.");
            return;
        }
        log.info("Found {} users in DB. Preparing to cache...", credentials.size());
        String[] usernames = credentials.stream().map(UserCredentialDTO::getUsername).toArray(String[]::new);
        String[] emails = credentials.stream().map(UserCredentialDTO::getEmail).toArray(String[]::new);
        this.redisTemplate.delete(List.of("users:usernames", "users:emails"));

        this.redisTemplate.opsForSet().add(RedisKeyConstants.USERNAME_SET, usernames);
        this.redisTemplate.opsForSet().add(RedisKeyConstants.EMAIL_SET, emails);

        setRandomTTL("users:usernames", 24 * 60);
        setRandomTTL("users:emails", 24 * 60);
    }

    private void setRandomTTL(String key, long baseMinutes) {
        long randomMinutes = ThreadLocalRandom.current().nextLong(120);
        long finalTtl = baseMinutes + randomMinutes;
        this.redisTemplate.expire(key, finalTtl, TimeUnit.MINUTES);
        log.info("Key [{}] set to expire in {} minutes (~{} hours).", key, finalTtl, String.format("%.1f", finalTtl / 60.0));
    }
}
