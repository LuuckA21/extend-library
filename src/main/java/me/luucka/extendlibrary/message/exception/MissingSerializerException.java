package me.luucka.extendlibrary.message.exception;

public class MissingSerializerException extends RuntimeException {
    private final Class<?> type;

    public MissingSerializerException(Class<?> type) {
        super("Missing serializer for type " + type.getName());
        this.type = type;
    }

    @SuppressWarnings("unused")
    public Class<?> getType() {
        return type;
    }
}
