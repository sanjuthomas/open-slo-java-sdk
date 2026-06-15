package com.sanjuthomas.openslosdk;

public enum ObjectFormat {
    YAML,
    JSON;

    public void validate() {
        switch (this) {
            case YAML, JSON -> {}
            default -> throw new IllegalArgumentException("unsupported ObjectFormat: " + this);
        }
    }
}
