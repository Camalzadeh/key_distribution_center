package bee01.humbat.keydistributioncenter.advices;

import bee01.humbat.keydistributioncenter.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentUser")
    public User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute("user");
    }

    /**
     * The sidebar highlights the page you are on. Thymeleaf 3.1 removed the
     * #request and #httpServletRequest expression objects, so the path cannot
     * be read from the template any more and has to arrive as model data.
     */
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null ? "/" : path;
    }
}
