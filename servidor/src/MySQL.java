/**
* Computación distribuida
* José Fernando Brigido Pablo
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class MySQL{

private static Connection conn = null;

    public void connect(){
        String url = "jdbc:mysql://db/atletismo";
        String user = "root";
        String pass = "234";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        }catch(Exception e){
            System.out.println(e);
        }

        try {
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection to MySQL succesful.");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public String getGlobalRegister(){
        String query = "SELECT tipo, usuario, distancia, tiempo, fecha FROM bitacora ORDER BY tipo, distancia, tiempo";
        String rows = "";
        try{
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                rows += result.getString(1)+" ";
                rows += result.getString(2)+" ";
                rows += result.getString(3)+" ";
                rows += result.getString(4)+" ";
                rows += result.getString(5)+"\n";
            }
        }catch (SQLException e){
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLEstate: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
        }
        return rows;
    }

    public String getUserRegister(DataExchange data){
        String query = "SELECT distancia, tiempo, tipo, fecha FROM bitacora WHERE usuario='"+data.user+"' AND hash='"+data.hash+"'";
        String rows = "";
        try{
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                rows += result.getString(1)+" ";
                rows += result.getString(2)+" ";
                rows += result.getString(3)+" ";
                rows += result.getString(4)+"\n";
            }
        }catch(SQLException e){
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLEstate: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
            return "Hubo un error al obtener tus registros :(";
        }
        return rows;
    }

    public String getUserData(String user, String hash){
        String query = "SELECT usuario, residencia, sexo, edad FROM corredor WHERE usuario='"+user+"' AND hash='"+hash+"'";
        try{
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            String message = "Estos son tus datos personales:\n";
            while(result.next()){
                message += result.getString(1)+" ";
                message += result.getString(2)+" ";
                message += result.getString(3)+" ";
                message += result.getString(4)+"\n";
            }
            return message;
        }catch(SQLException e){
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLEstate: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
            return "Hubo un error al obtener tu información :(\n";
        }
    }

    public String addUserRegister(DataExchange data){
        String query = "INSERT INTO bitacora VALUES(\""+data.user+"\",\""+data.hash+"\",\""+
                        data.distancia+"\",\""+data.tiempo+"\",\""+data.tipo+"\",\""+data.fecha+"\");";
        try{
            Statement statement = conn.createStatement();
            statement.execute(query);
            return "Registro exitoso :)";
        }catch(SQLException e){
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLEstate: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
            return "Registro fallido :(\n";
        }
    }

    public String addUser(DataExchange data){
        String message = "Registro fallido";
        String query = "INSERT INTO corredor VALUES(\""+data.user+"\",\""+data.hash+"\",\""+
                        data.residencia+"\",\""+data.sexo+"\","+data.edad+");";
        try{
            Statement statement = conn.createStatement();
            statement.execute(query);
            System.out.println("Nuevo usuario agregado.");
            return "Registro exitoso.";

        }catch(SQLException e){
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLEstate: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
        }
        return message;
    }

    public boolean searchUser(DataExchange data){
        String query = "SELECT usuario, residencia, sexo, edad FROM corredor WHERE usuario='"+data.user+"' AND hash='"+data.hash+"'";
        String rows = "";

        try{
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                rows += result.getString(1)+" ";
                rows += result.getString(2)+" ";
                rows += result.getString(3)+" ";
                rows += result.getString(4)+"\n";
            }
        }catch(SQLException e){
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLEstate: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
        }
        return !rows.isEmpty();
    }

}
