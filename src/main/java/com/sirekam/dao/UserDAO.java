package com.sirekam.dao;

import com.sirekam.model.User;
import com.sirekam.model.enums.Role;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements GenericDAO<User> {

    private DatabaseManager dbManager;

    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public boolean save(User user) throws SQLException {
        String sql = "INSERT INTO tb_user (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                user.getUsername(),
                user.getPassword(),
                user.getNamaLengkap(),
                user.getRole().getValue()
        );
        return result > 0;
    }

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM tb_user WHERE id_user = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            return mapResultSetToUser(rs);
        }
        return null;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM tb_user WHERE username = ?";
        ResultSet rs = dbManager.executeQuery(sql, username);
        if (rs.next()) {
            return mapResultSetToUser(rs);
        }
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_user ORDER BY nama_lengkap ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToUser(rs));
        }
        return list;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE tb_user SET username = ?, password = ?, nama_lengkap = ?, role = ? WHERE id_user = ?";
        int result = dbManager.executeUpdate(sql,
                user.getUsername(),
                user.getPassword(),
                user.getNamaLengkap(),
                user.getRole().getValue(),
                user.getIdUser()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_user WHERE id_user = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    @Override
    public List<User> search(String keyword) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_user WHERE username LIKE ? OR nama_lengkap LIKE ? ORDER BY nama_lengkap ASC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%", "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToUser(rs));
        }
        return list;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setIdUser(rs.getInt("id_user"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNamaLengkap(rs.getString("nama_lengkap"));
        user.setRole(Role.fromValue(rs.getString("role")));
        return user;
    }
}