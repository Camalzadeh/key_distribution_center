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

        // /actuator is served on its own port and therefore in a child
        // context this filter is not registered in. It is listed anyway: if
        // the management port is ever folded back onto the main one, the
        // health probe would otherwise be answered with a redirect to /auth
        // and the container would never report healthy.
        return path.startsWith("/auth") || path.startsWith("/error") ||
                path.startsWith("/actuator") ||
                path.startsWith("/static") || path.startsWith("/css") ||
                path.startsWith("/js") || path.startsWith("/images") ||
                path.equals("/favicon.ico");
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
