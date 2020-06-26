package service;

import bean.Auteur;
import bean.Ouvrage;
import oracle.jdbc.OracleTypes;
import sample.Main;
import util.Connexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OuvrageService {

    public int insert(Ouvrage ouvrage) {
        int result = 1;
        Connection connection = Connexion.getConnection();
        CallableStatement cs = null;
        try {
            cs = connection.prepareCall("{call insert_ouvrage(?,?,?,?,?,?,?)}");
            cs.setString(1, ouvrage.getTitre());
            cs.setString(2, ouvrage.getEditeur());
            cs.setInt(3, ouvrage.getAnnee());
            cs.setString(4, ouvrage.getDomaine());
            cs.setInt(5, ouvrage.getStock());
            cs.setString(6, Connexion.site);
            cs.setLong(7, ouvrage.getAuteur().getId_aut());
            cs.execute();
            result = 1;
        } catch (SQLException e) {
            e.printStackTrace();
            result = -1;
        } finally {
            if (connection != null) {
                try {
                    if (cs != null) cs.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    return result;
                }
            }
        }
        return result;
    }

    public List<Ouvrage> findByTitre(String vTitre) {
        List<Ouvrage> ouvrages=new ArrayList<>();
        ResultSet rs;
        CallableStatement cs;
        Connection connection = Connexion.getConnection();
        String query="begin ? := findOeuvreByTitre(?); end;";
        try {
            cs= connection.prepareCall(query);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2,vTitre);
            cs.execute();
            rs= (ResultSet) cs.getObject(1);
            while(rs.next()){
                ouvrages.add(new Ouvrage(
                        rs.getLong("ID_OUV"),
                        rs.getString("TITRE"),
                        rs.getString("EDITEUR"),
                        rs.getInt("ANNEE"),
                        rs.getString("DOMAINE"),
                        rs.getInt("STOCK"),
                        new Auteur(rs.getString("NOM_AUTEUR")))
                );
            }
            return ouvrages;
        }catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        finally {
            try {
                Connexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ouvrages;
    }

    public int delete(Ouvrage ouvrage) {
        //0: exception , 1: suppresion ok, -1:ouvrage en relation avec des prêts
        int res=0;
        CallableStatement cs;
        Connection connection = Connexion.getConnection();
        String query="begin ? := deleteOuvrage(?); end;";
        try {
            cs= connection.prepareCall(query);
            cs.registerOutParameter(1, OracleTypes.INTEGER);
            cs.setLong(2,ouvrage.getId_ouv());
            cs.executeUpdate();
            res=cs.getInt(1);
        }catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        finally {
            try {
                Connexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return res;
    }

    public int updateStock(Ouvrage ouvrage, int vStock) {
        //0: exception , 1: modif ok,
        int res=0;
        CallableStatement cs;
        Connection connection = Connexion.getConnection();
        String query="begin ? := updateStock(?,?); end;";
        try {
            cs= connection.prepareCall(query);
            cs.registerOutParameter(1, OracleTypes.INTEGER);
            cs.setLong(2,ouvrage.getId_ouv());
            cs.setInt(3,vStock);
            cs.executeUpdate();
            res=cs.getInt(1);
        }catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        finally {
            try {
                Connexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return res;

    }
}
