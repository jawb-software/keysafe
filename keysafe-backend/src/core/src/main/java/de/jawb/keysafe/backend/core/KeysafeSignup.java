package de.jawb.keysafe.backend.core;

public record KeysafeSignup(String name, String accessUserName, String accessPassword) {

    @Override
    public String toString() {
        return "KeysafeSignup{" +
                "name='" + name + '\'' +
                ", accessUserName='" + accessUserName + '\'' +
                ", accessPassword='*********'" +
                '}';
    }
}
