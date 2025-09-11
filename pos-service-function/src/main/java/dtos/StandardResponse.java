package dtos;

public record StandardResponse(int code, boolean success, String message, Object data) {}