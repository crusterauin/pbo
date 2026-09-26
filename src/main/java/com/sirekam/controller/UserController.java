package com.sirekam.controller;

import com.sirekam.model.User;
import com.sirekam.model.Dokter;
import com.sirekam.model.enums.Role;
import com.sirekam.dao.UserDAO;
import com.sirekam.dao.DokterDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk modul Manajemen Akun milik Admin:
 * menambah akun petugas/dokter/apoteker dan mengubah username/password mereka.
 */
public class UserController extends GenericController<User> {

    private final UserDAO userDAO;
    private final DokterDAO dokterDAO;

    public UserController() {
        super(new UserDAO());
        this.userDAO = (UserDAO) dao;
        this.dokterDAO = new DokterDAO();
    }

    public User findByUsername(String username) throws SQLException {
        return userDAO.findByUsername(username);
    }

    /** Daftar akun petugas/dokter/apoteker (tanpa akun admin) untuk dikelola Admin. */
    public List<User> findAllPetugas() throws SQLException {
        List<User> all = userDAO.findAll();
        List<User> result = new ArrayList<>();
        for (User u : all) {
            if (u.getRole() != Role.ADMIN) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Menambah akun petugas/dokter/apoteker. Jika role = DOKTER, otomatis
     * membuat juga baris di tb_dokter agar dokter tersebut muncul di sistem.
     */
    public boolean tambahAkun(User user, String spesialisasiDokter) throws SQLException {
        int idUser = userDAO.saveAndGetId(user);
        if (idUser <= 0) {
            return false;
        }
        if (user.getRole() == Role.DOKTER) {
            Dokter dokter = new Dokter(0, user.getNamaLengkap(), spesialisasiDokter, idUser);
            dokterDAO.save(dokter);
        }
        return true;
    }

    public boolean ubahAkun(User user) throws SQLException {
        return userDAO.update(user);
    }

    public boolean hapusAkun(int idUser) throws SQLException {
        return userDAO.delete(idUser);
    }
}
