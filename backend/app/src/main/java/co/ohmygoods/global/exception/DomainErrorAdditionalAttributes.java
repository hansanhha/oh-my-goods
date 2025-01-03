package co.ohmygoods.global.exception;

import java.net.URI;

public record DomainErrorAdditionalAttributes(URI type,
                                              URI instance) {
}
