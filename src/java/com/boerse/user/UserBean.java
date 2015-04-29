/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boerse.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Ghost
 */
@ManagedBean(name = "user")
@SessionScoped
public class UserBean {

    private String name;
    private String passwort;
    private String beruf;
    private String arbeitgeber;
    private String ort;
    private String strasse;
    private String email;
    private String guthaben;
    private String gruppe;
    private String dbPasswort;
    private String dbName;
    DataSource ds;

    /**
     * Creates a new instance of User
     */
    public UserBean() {
        try {
            Context initCtx = new InitialContext();                             // Suche nach Datenbank
            Context envCtx = (Context) initCtx.lookup("java:comp/env");         //
            //ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/Börse");     //
            ds = (DataSource) envCtx.lookup("jdbc/Boerse");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public String getDbPasswort() {
        return dbPasswort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getBeruf() {
        return beruf;
    }

    public void setBeruf(String beruf) {
        this.beruf = beruf;
    }

    public String getArbeitgeber() {
        return arbeitgeber;
    }

    public void setArbeitgeber(String arbeitgeber) {
        this.arbeitgeber = arbeitgeber;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGuthaben() {
        return guthaben;
    }

    public void setGuthaben(String guthaben) {
        this.guthaben = guthaben;
    }

    public String getGruppe() {
        return gruppe;
    }

    public void setGruppe(String gruppe) {
        this.gruppe = gruppe;
    }

    public String add() {
        int i = 0;
        if (name != null) {
            PreparedStatement ps = null;
            Connection con = null;
            try {
                if (ds != null) {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "INSERT INTO User(Name, Passwort, Beruf, Arbeitgeber, Ort, Straße, Email, Guthaben) VALUES(?,?,?,?,?,?,?,?)";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, name);
                        ps.setString(2, passwort);
                        ps.setString(3, beruf);
                        ps.setString(4, arbeitgeber);
                        ps.setString(5, ort);
                        ps.setString(6, strasse);
                        ps.setString(7, email);
                        ps.setString(8, guthaben);
                        i = ps.executeUpdate();

                        String sql2 = "INSERT INTO GroupTable(Groups, User_Name)";
                        ps = null;
                        ps = con.prepareStatement(sql2);
                        ps.setString(1, gruppe);
                        ps.setString(2, name);
                        i=0;
                        i = ps.executeUpdate();
                        System.out.println("Daten erfolgreich hinzugefügt!");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    con.close();
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (i > 0) {
            return "success";
        } else {
            return "unsuccess";
        }
    }

    public void dbData(String uName) {
        if (uName != null) {
            PreparedStatement ps = null;
            Connection con = null;
            ResultSet rs = null;

            if (ds != null) {
                try {
                    con = ds.getConnection();
                    if (con != null) {
                        String sql = "select name,passwort from User where name = '"
                                + uName + "'";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                        System.out.println("Result Set = " + rs);
                        dbName = rs.getString("name");
                        dbPasswort = rs.getString("passwort");
                    }
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    public String login() {
        dbData(name);
        if (name.equals(dbName) && passwort.equals(dbPasswort)) {
            return "output";
        } else {
            return "invalid";
        }
    }

    public void logout() {
        FacesContext.getCurrentInstance().getExternalContext()
                .invalidateSession();
        FacesContext.getCurrentInstance()
                .getApplication().getNavigationHandler()
                .handleNavigation(FacesContext.getCurrentInstance(), null, "/login.xhtml");
    }

}
