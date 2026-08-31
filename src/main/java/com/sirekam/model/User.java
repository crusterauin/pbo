package com.sirekam.model;

import com.sirekam.model.enums.Role;

public class User {
    private int idUser;
    private String username;
    private String password;
    private String namaLengkap;
    private Role role;

    public User() {}

    public User(int idUser, String username, String password, String namaLengkap, Role role) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return namaLengkap + " (" + role.getDisplayName() + ")";
    }
}