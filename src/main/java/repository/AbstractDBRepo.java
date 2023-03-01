package repository;

import domain.Entity;
import domain.validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDBRepo<E extends Entity<ID>,ID> implements Repository<E, ID>{

    private Validator<E> validator;
    protected Connection connection;
    private String table_name;
    private ArrayList<String> columns_name;

    //--------------------Private functions------------------------
    private ArrayList<String> getColumnsNameFromTable(){
        ArrayList<String> ar = new ArrayList<String>();
        String query = "SELECT column_name\n" +
                "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                "WHERE TABLE_NAME = N'"+this.table_name+"'";
        try {
            PreparedStatement st = connection.prepareStatement(query);
            ResultSet res = st.executeQuery();
            while (res.next()) {
                ar.add(res.getString(1));
            }
        }
        catch (SQLException e){
            //System.out.println(e);
        }
        return ar;
    }

    private String columnsToString(ArrayList<String> columns){
        String str="(";
        for(String column : columns){
            str = str + column +",";
        }
        str = str.substring(0, str.length() - 1);
        str = str+")";
        return str;
    }


    //--------------------Public functions-----------------------
    /*
    Constructor
     */
    public AbstractDBRepo(Validator<E> val, String db_name, String table_name){
        this.validator = val;
        this.table_name = table_name;
        try {
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+db_name,"postgres","postgre");
        }
        catch (SQLException e){
            //System.out.println(e);
            System.out.println(e);
        }
        this.columns_name = getColumnsNameFromTable();
    }

    /*
    Save an entity in the table.
    return type: E
     */
    public E save(E entity){
        System.out.println(columnsToString(columns_name));
        System.out.println(entityToDBString(entity));
        String query = "INSERT INTO " +table_name+columnsToString(columns_name)+" VALUES " + entityToDBString(entity);
        validator.validate(entity);
        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e);
        }

        return entity;
    }


    /*
    Return all entities from table
    return type: Iterable<E>
    */
    public Iterable<E> findAll(){
        String query = "SELECT * FROM " + table_name;
        List<E> entities = new ArrayList<>();
        try {
            PreparedStatement st = connection.prepareStatement(query);
            ResultSet res = st.executeQuery();
            while (res.next()) {
                E entity = resultSetToEntity(res);
                entities.add(entity);
            }
        }
        catch (SQLException e){
            //System.out.println(e);
        }
        return entities;
    }

    /*
    !not the best
    Update an oldentity with a new one
    return type: E
     */
    @Override
    public E update(E newentity, E oldentity) {
        delete(oldentity.getId());
        validator.validate(newentity);
        save(newentity);
        return newentity;
    }

    /*
        Transform a result set from database to entity
        return type: E
         */
    public abstract E resultSetToEntity(ResultSet r);


    /*
    Transform an entity to a string (VALUE db format)
    return type: string
     */
    public abstract String entityToDBString(E entity);

}
