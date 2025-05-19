package bee01.humbat.keydistributioncenter.filters;

import bee01.humbat.keydistributioncenter.entities.Session;
import bee01.humbat.keydistributioncenter.repositories.SessionRepository;
import bee01.humbat.keydistributioncenter.services.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    public SessionFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/auth") || path.startsWith("/error") ||
                path.startsWith("/static") || path.startsWith("/css") ||
                path.startsWith("/js") || path.startsWith("/images");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String sessionId = null;

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionId != null && sessionService.findByToken(sessionId).isPresent()) {
            Session session = sessionService.findByToken(sessionId).get();
            request.setAttribute("user", session.getUser());
            filterChain.doFilter(request, response);
        } else {
            response.sendRedirect("/auth");
        }
    }


}
