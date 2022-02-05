package pashazz.widgetmanager.aspect.provider;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import pashazz.widgetmanager.utils.Utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


/**
 * This aspect calculates the time spent by the method for logging purposes
 */
@Aspect
@Slf4j
@Component
public class MeasureAspect {


  @Pointcut("@annotation(pashazz.widgetmanager.aspect.annotation.Measure)")
  public void measurable() {}


  @Around("measurable()")
  public Object read(ProceedingJoinPoint pjp) throws  Throwable {
    if (log.isTraceEnabled()) {
      log.trace("entering method {}::{}", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName());
      long startTime = System.nanoTime();
      try {
        return pjp.proceed();
      } finally {
        long elapsedTime = System.nanoTime() - startTime;
        log.trace("exiting method {}::{}; elapsed time: {}",
          pjp.getSignature().getDeclaringTypeName(),
          pjp.getSignature().getName(),
          printElapsedTime(elapsedTime));

      }
    } else {
     return pjp.proceed();
    }
  }


  private static String printElapsedTime(long elapsedTime) {
    return Utils.printDuration(Duration.of(elapsedTime, ChronoUnit.NANOS));
  }

}
