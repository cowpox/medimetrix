// src/main/java/.../web/mvc/advice/RoleAdvice.java
package com.mmx.medimetrix.web.mvc.advice;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@ControllerAdvice
public class RoleAdvice {

    @ModelAttribute("role")
    public String exposeRole(
            @RequestParam(value = "role", required = false) String roleParam,
            HttpSession session) {

        if (roleParam != null && !roleParam.isBlank()) {
            session.setAttribute("role", roleParam.toLowerCase()); // guarda na sessão
        }
        Object saved = session.getAttribute("role");
        return saved != null ? saved.toString() : "gestor"; // fallback
    }
}
