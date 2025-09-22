package br.com.jv.todolist.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.jv.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // garante que roda cedo
public class FilterTaskAuth extends OncePerRequestFilter {

    private final IUserRepository userRepository;

    public FilterTaskAuth(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Só filtra as rotas de tasks (GET/POST/PUT/DELETE etc.)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getServletPath(); // ex: "/tasks" ou "/tasks/"
        return !(p.equals("/tasks") || p.startsWith("/tasks/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Basic ")) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"Tasks\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // "Basic <base64(username:password)>"
        String base64 = auth.substring(6).trim(); // 6 = "Basic ".length()
        String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        String[] parts = decoded.split(":", 2);   // limita em 2 para senhas com ':'
        if (parts.length != 2) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = parts[0];
        String password = parts[1];

        var user = userRepository.findByUsername(username);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var ok = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!ok.verified) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Autenticado
        chain.doFilter(request, response);
    }
}
