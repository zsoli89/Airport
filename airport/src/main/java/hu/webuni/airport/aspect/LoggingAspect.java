package hu.webuni.airport.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    //    amire rateszem a LogCall annotaciot vagy olyan tipuson belul van amin rajta van (osztaly, interface)
    @Pointcut("@annotation(hu.webuni.airport.aspect.LogCall) || @within(hu.webuni.airport.aspect.LogCall)")
    public void annotationLogCall() {
    }

//    mindegy hogy milyen visszaterisu metodus, de a repository package-ben,
//    mindegy melyik tipusban es mindegy melyik metodus milyen parameterei vannak(..)
//    @Before("execution(* hu.webuni.airport.repository.*.*(..))")
    @Before("hu.webuni.airport.aspect.LoggingAspect.annotationLogCall()")
//    azaz az annotationLogCall metodus definialja a szabalyt
    public void logBefore(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        String type = interfaces.length == 0 ? clazz.getName() : interfaces[0].toString();

        System.out.format("Method %s called in class %s%n",
                joinPoint.getSignature(),
                type);
//        a getName proxy nevet ir ki, a getInterfaces()[0]
//        adja vissza a nevet
    }
}
