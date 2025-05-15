package co.ohmygoods.global.lock;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @see <a href=https://helloworld.kurly.com/blog/distributed-redisson-lock>코드 참고</a>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String LOCK_PREFIX = "lock:";

    private final LockProtectTransaction lockProtectTransaction;
    private final RedissonClient redissonClient;

    @Around("@annotation(co.ohmygoods.global.lock.DistributedLock)")
    public Object processDistributedLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = LockGeyGenerator.generate(LOCK_PREFIX, signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean acquire = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

            if (!acquire) {
                return false;
            }

            return lockProtectTransaction.proceed(joinPoint);
        }
        catch (InterruptedException e) {
            throw new InterruptedException();
        }
        finally {
            rLock.unlock();
        }

    }
}
