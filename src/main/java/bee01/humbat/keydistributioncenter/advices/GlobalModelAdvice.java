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
}
