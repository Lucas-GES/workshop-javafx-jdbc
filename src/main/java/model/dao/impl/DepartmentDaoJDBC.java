package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Department obj) {
        String query = "INSERT INTO department "
                + "(Name, Id) "
                + "VALUES "
                + "(?, ?)";
        try(PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            st.setString(1, obj.getName());
            st.setInt(2, obj.getId());

            int rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
            }else{
                throw new DbException("Unexpected error! No rows affected!");
            }

        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Department obj) {
        String query = "UPDATE department "
                + "SET Name = ?, Id = ? "
                + "WHERE Id = ?";
        try(PreparedStatement st = conn.prepareStatement(query)){

            st.setString(1, obj.getName());
            st.setInt(2, obj.getId());
            st.setInt(3, obj.getId());

            st.executeUpdate();

        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Integer id) {
        String query = "DELETE FROM department WHERE Id = ?";
        try(PreparedStatement st = conn.prepareStatement(query)){

            st.setInt(1, id);
            st.executeUpdate();

        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Department findById(Integer id) {
        String query = "SELECT department.*,department.Name as DepName "
                + "FROM department "
                + "WHERE department.Id = ?";
        try(PreparedStatement st = conn.prepareStatement(query)){
            st.setInt(1, id);
            try(ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Department dep = instantiateDepartment(rs);
                    return dep;
                }
                return null;
            }
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
    }

    @Override
    public List<Department> findAll() {
        String query = "SELECT department.*,department.Name as DepName "
                + "FROM department "
                + "ORDER BY Name";
        try(PreparedStatement st = conn.prepareStatement(query)){
            try(ResultSet rs = st.executeQuery()) {
                List<Department> list = new ArrayList<>();
                Map<Integer, Department> map = new HashMap<>();

                while (rs.next()) {
                    Department dep;
                    dep = instantiateDepartment(rs);
                    list.add(dep);
                }
                return list;
            }

        }catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }
}
