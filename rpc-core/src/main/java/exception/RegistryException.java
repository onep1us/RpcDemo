package exception;

import enums.RegistryErrorEnum;

/**
 * @author wanjiahao
 */
public class RegistryException extends RuntimeException{
    public RegistryException(RegistryErrorEnum registryErrorEnum, String detail) {
        super(registryErrorEnum.getMessage() + ":" + detail);
    }
}
