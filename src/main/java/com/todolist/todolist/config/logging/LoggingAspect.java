package com.todolist.todolist.config.logging;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.auth.dto.LoginRequestDTO;
import com.todolist.todolist.features.users.dto.UserDTO;
import com.todolist.todolist.features.users.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.todolist.todolist..*ServiceImpl.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Récupération des arguments en filtrant les données sensibles
        Object[] args = sanitizeArgs(joinPoint.getArgs());

        log.info("[{}#{}] Entrée - args: {}", className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;

            log.info("[{}#{}] Sortie - durée: {}ms", className, methodName, duration);

            return result;

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;

            log.error("[{}#{}] Exception après {}ms - type: {} | message: {}",
                    className, methodName, duration,
                    ex.getClass().getSimpleName(),
                    ex.getMessage());

            // On relance l'exception pour ne pas perturber le comportement normal
            throw ex;
        }
    }

    /**
     * Remplace les objets sensibles ou à risque de lazy loading par une version safe.
     * - Masque les champs password des UserDTO/UserEntity
     * - Évite de toString() des entités JPA qui pourraient déclencher du lazy loading
     */
    private Object[] sanitizeArgs(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    if (arg instanceof LoginRequestDTO dto) return "[LOGIN] username='%s'".formatted(dto.getUsername());
                    if (arg instanceof UserDTO || arg instanceof UserEntity) return "[MASKED USER]";
                    if (arg instanceof AnnonceDTO dto) return sanitizeAnnonceDTO(dto);
                    if (arg instanceof AnnonceEntity) return "[AnnonceEntity]";
                    return arg;
                })
                .toArray();
    }

    private String sanitizeAnnonceDTO(AnnonceDTO dto) {
        return "AnnonceDTO{id=%d, title='%s', status=%s, author_id=%d, category_id=%d}"
                .formatted(dto.getId(), dto.getTitle(), dto.getStatus(),
                        dto.getAuthor_id(), dto.getCategory_id());
    }
}
