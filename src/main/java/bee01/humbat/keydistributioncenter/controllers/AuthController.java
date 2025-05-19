package bee01.humbat.keydistributioncenter.controllers;


import bee01.humbat.keydistributioncenter.dtos.UserDTO;
import bee01.humbat.keydistributioncenter.entities.Session;
import bee01.humbat.keydistributioncenter.entities.User;
import bee01.humbat.keydistributioncenter.services.SessionService;
import bee01.humbat.keydistributioncenter.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;
    @Value("${session.ttl.hours}")
    private int sessionTtlHours;

    public AuthController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }


    @GetMapping()
    public String auth() {
        return "register";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = "SESSION_ID", required = false) String sessionId,
                         HttpServletResponse response) {

        if (sessionId != null) {
            sessionService.deleteByToken(sessionId);
        }

        Cookie cookie = new Cookie("SESSION_ID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }


    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submit(@RequestBody UserDTO userDTO,
                                    HttpServletResponse response) {

        Optional<User> existingUser = Optional.ofNullable(userService.findByUsername(userDTO.username()));

        System.out.println("UserDTO: " + userDTO);

        if (existingUser.isPresent()) {
            Optional<User> userOpt = userService.login(userDTO);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials"));
            }
            User user = userOpt.get();
            Session session = sessionService.createSession(user);
            attachSessionCookie(response, session.getSessionId());

            return ResponseEntity.ok(Map.of("message", "Login successful: " + user.getUsername()));
        }

        User newUser = userService.saveUser(userDTO);
        Session session = sessionService.createSession(newUser);
        attachSessionCookie(response, session.getSessionId());

        return ResponseEntity.ok(Map.of("message", "User registered and logged in: " + newUser.getUsername()));
    }

    private void attachSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie("SESSION_ID", sessionId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(60 * 60 * sessionTtlHours);
        response.addCookie(cookie);
    }


}

