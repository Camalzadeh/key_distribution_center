package bee01.humbat.keydistributioncenter.dtos;

import bee01.humbat.keydistributioncenter.entities.Message;

public record DecryptedMessageDTO(Message message, String decryptedText, String decryptedKey) {
}
