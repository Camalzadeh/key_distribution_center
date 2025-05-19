package bee01.humbat.keydistributioncenter.dtos;

import bee01.humbat.keydistributioncenter.entities.User;

public record MessageDTO(User from, Long to, String algorithm, String mode, String key, String text) {

}
