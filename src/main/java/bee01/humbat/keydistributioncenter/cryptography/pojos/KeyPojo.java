package bee01.humbat.keydistributioncenter.cryptography.pojos;

import bee01.humbat.keydistributioncenter.cryptography.keys.Key;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;

public record KeyPojo(Algorithm algorithm, Key key) {
}
