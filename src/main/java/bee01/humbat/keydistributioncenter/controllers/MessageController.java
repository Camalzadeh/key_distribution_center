package bee01.humbat.keydistributioncenter.controllers;

import bee01.humbat.keydistributioncenter.dtos.DecryptedMessageDTO;
import bee01.humbat.keydistributioncenter.dtos.MessageDTO;
import bee01.humbat.keydistributioncenter.entities.Message;
import bee01.humbat.keydistributioncenter.entities.User;
import bee01.humbat.keydistributioncenter.services.MessageService;
import bee01.humbat.keydistributioncenter.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MessageController {

    private final UserService userService;
    private final MessageService messageService;

    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @GetMapping("/message")
    public String messagePage(HttpServletRequest request, Model model) {
        User currentUser = (User) request.getAttribute("user");

        List<User> users = userService.findAll()
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

        model.addAttribute("users", users);
        return "message";
    }

    @GetMapping("/inbox")
    public String inboxPage(HttpServletRequest request, Model model) {
        User currentUser = (User) request.getAttribute("user");

        List<DecryptedMessageDTO> receivedMessages = messageService.findByReceiver(currentUser);
        List<DecryptedMessageDTO> sentMessages = messageService.findBySender(currentUser);

        model.addAttribute("sentMessages", sentMessages);
        model.addAttribute("receivedMessages", receivedMessages);
        return "inbox";
    }

    @PostMapping("/message/send")
    public String sendMessage(
            @RequestParam("receiverId") Long receiverId,
            @RequestParam("algorithm") String algorithm,
            @RequestParam("blockMode") Optional<String> blockMode,
            @RequestParam("key") String key,
            @RequestParam("content") String plainText,
            HttpServletRequest request
    ) {
        User sender = (User) request.getAttribute("user");

        MessageDTO message = new MessageDTO(sender, receiverId, algorithm, blockMode.orElse("NONE"), key, plainText);
        messageService.sendMessage(message);
        return "redirect:/";
    }

}
