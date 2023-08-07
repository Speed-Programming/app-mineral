/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import admin.Admin;
import com.toedter.calendar.JDateChooser;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import main.Login;
import static module.EpochTime.getDateEpoch;
import pelanggan.Pelanggan;

/**
 *
 * @author fsdio
 */
public class methodDB {
    // My Variable Global
    Connection con = null; PreparedStatement pst = null; ResultSet res = null;
    // Method Table
    public void DefaultTabel(JTable newTable, Integer size){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel columnModel = newTable.getColumnModel();
        for (int i = 0; i < size; i++) {
            newTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            columnModel.getColumn(i).setPreferredWidth(200);
            columnModel.getColumn(i).setMinWidth(200);
        }
    }
    
    // Method Class Admin
    public void Login(JTextField user, JPasswordField pass) throws ParseException{
        if (user.getText().equals("")) {
            user.requestFocus();
        } else if (String.valueOf(pass.getPassword()).equals("")) {
            pass.requestFocus();
        }else {
            try {
                String SQLQuery = "SELECT COUNT(*), `type` FROM `user` WHERE `username`=? AND `password`=?;";
                con = (Connection) Connect.configDB();
                pst = con.prepareStatement(SQLQuery);
                pst.setString(1, user.getText());
                pst.setString(2, String.valueOf(pass.getPassword()));
                res = pst.executeQuery();
                
                while (res.next()) {                    
                    if(res.getInt(1)==0){
                        JOptionPane.showMessageDialog(null, "Data kosong.");
                    }else if(res.getString(2).equals("admin")){
                        new Admin().setVisible(true);
                    }else {
                        new Pelanggan().setVisible(true);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                // Close the result set
                if (res != null) {try {res.close();} catch (SQLException e) {}}
                // Close the prepared statement
                if (pst != null) {try {pst.close();} catch (SQLException e) {}}
                // Close the connection
                if (con != null) {try {con.close();} catch (SQLException e) {}}
            }
        }
    }
    
    public void Daftar(JTextField user, JPasswordField pass, JTextField phone){
        if(user.getText().equals("")){
            user.requestFocus();
        }else if(String.valueOf(pass.getPassword()).equals("")){
            pass.requestFocus();
        }else if(phone.getText().equals("")){
            phone.requestFocus();
        }else {
            try {
                String SQLQuery = "INSERT INTO `user` (`username`, `password`, `phone`) VALUES (?,?,?);";
                con = (Connection) Connect.configDB();
                pst = con.prepareStatement(SQLQuery);
                pst.setString(1, user.getText());
                pst.setString(2, String.valueOf(pass.getPassword()));
                pst.setString(3, phone.getText());
                pst.execute();
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan.");
            } catch (SQLIntegrityConstraintViolationException e) {
                JOptionPane.showMessageDialog(null, "Data sudah digunakan.");
            }catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                // Close the result set
                if (res != null) {try {res.close();} catch (SQLException e) {}}
                // Close the prepared statement
                if (pst != null) {try {pst.close();} catch (SQLException e) {}}
                // Close the connection
                if (con != null) {try {con.close();} catch (SQLException e) {}}
            }
        }
    }
    
    public void getDataHistoryDays(JTable nameTable, String date) throws ParseException{
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID TOPUP");
        model.addColumn("USERNAME");
        model.addColumn("NOMINAL");
        model.addColumn("JENIS BAYAR");
        model.addColumn("STATUS");
        model.addColumn("CATATAN");
        
        try {
            String SQLQuery = "SELECT id, username, nominal, type_buy, status, catatan FROM topup WHERE date=?;";
            con = (Connection) Connect.configDB();
            pst = con.prepareStatement(SQLQuery);
            pst.setString(1, String.valueOf(getDateEpoch(date)));
            res = pst.executeQuery();
            
            while (res.next()) {                
                model.addRow(new Object[]{
                    res.getString(1).toUpperCase(),
                    res.getString(2).toUpperCase(),
                    res.getString(3).toUpperCase(),
                    res.getString(4).toUpperCase(),
                    res.getString(5).toUpperCase(),
                    res.getString(6).toUpperCase(),
                });
            }
            nameTable.setModel(model);
            DefaultTabel(nameTable, 6);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            // Close the result set
            if (res != null) {try {res.close();} catch (SQLException e) {}}
            // Close the prepared statement
            if (pst != null) {try {pst.close();} catch (SQLException e) {}}
            // Close the connection
            if (con != null) {try {con.close();} catch (SQLException e) {}}
        }
    }
    
    public void UpdateTopup(String status, JTextField id, JTextField username){
        try {
            String SQLQuery = "UPDATE `topup` SET `status` = ? WHERE `id` = ? AND `username` = ?;";
            con = (Connection) Connect.configDB();
            pst = con.prepareStatement(SQLQuery);
            pst.setString(1, status.toLowerCase());
            pst.setString(2, id.getText());
            pst.setString(3, username.getText());
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Data di"+status+".");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            // Close the result set
            if (res != null) {try {res.close();} catch (SQLException e) {}}
            // Close the prepared statement
            if (pst != null) {try {pst.close();} catch (SQLException e) {}}
            // Close the connection
            if (con != null) {try {con.close();} catch (SQLException e) {}}
        }
    }
    
    // Method Class Data Pelanggan
    public void getDataPelanggan(JTable nameTable){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("USERNAME");
        model.addColumn("PASSWORD");
        model.addColumn("NO HANDPHONE");
        model.addColumn("SALDO");
        model.addColumn("TYPE   ");
        
        try {
            String SQLQuery = "SELECT * FROM `user`;";
            con = (Connection) Connect.configDB();
            pst = con.prepareStatement(SQLQuery);
            res = pst.executeQuery();
            
            while (res.next()) {                
                model.addRow(new Object[]{
                    res.getString(1).toUpperCase(),
                    res.getString(2).toUpperCase(),
                    res.getString(3).toUpperCase(),
                    res.getString(4).toUpperCase(),
                    res.getString(5).toUpperCase(),
                });
            }
            nameTable.setModel(model);
            DefaultTabel(nameTable, 5);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            // Close the result set
            if (res != null) {try {res.close();} catch (SQLException e) {}}
            // Close the prepared statement
            if (pst != null) {try {pst.close();} catch (SQLException e) {}}
            // Close the connection
            if (con != null) {try {con.close();} catch (SQLException e) {}}
        }
    }
    
    public void getDataPelangganId(JTable nameTable, String username){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("USERNAME");
        model.addColumn("PASSWORD");
        model.addColumn("NO HANDPHONE");
        model.addColumn("SALDO");
        model.addColumn("TYPE   ");
        
        try {
            String SQLQuery = "SELECT * FROM `user` WHERE `username`=?;";
            con = (Connection) Connect.configDB();
            pst = con.prepareStatement(SQLQuery);
            pst.setString(1, username);
            res = pst.executeQuery();
            
            while (res.next()) {                
                model.addRow(new Object[]{
                    res.getString(1).toUpperCase(),
                    res.getString(2).toUpperCase(),
                    res.getString(3).toUpperCase(),
                    res.getString(4).toUpperCase(),
                    res.getString(5).toUpperCase(),
                });
            }
            nameTable.setModel(model);
            DefaultTabel(nameTable, 5);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            // Close the result set
            if (res != null) {try {res.close();} catch (SQLException e) {}}
            // Close the prepared statement
            if (pst != null) {try {pst.close();} catch (SQLException e) {}}
            // Close the connection
            if (con != null) {try {con.close();} catch (SQLException e) {}}
        }
    }
    
    public void setDataPelanggan(JTable nameTable, String ...value){
        
        try {
            String SQLQuery = "UPDATE `user` SET `password` = ?, `phone` = ?, `saldo` = ?, `type` = ? WHERE `username` = ?;";
            con = (Connection) Connect.configDB();
            pst = con.prepareStatement(SQLQuery);
            pst.setString(1, value[0]);
            pst.setString(2, value[1]);
            pst.setString(3, value[2]);
            pst.setString(4, value[3]);
            pst.setString(5, value[4]);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Ubah data berhasil.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            // Close the result set
            if (res != null) {try {res.close();} catch (SQLException e) {}}
            // Close the prepared statement
            if (pst != null) {try {pst.close();} catch (SQLException e) {}}
            // Close the connection
            if (con != null) {try {con.close();} catch (SQLException e) {}}
        }
    }
    
    public void deleteDataPelanggan(JTable nameTable, String value){
        
        try {
            String SQLQuery = "DELETE FROM `user` WHERE `username` = ?;";
            con = (Connection) Connect.configDB();
            pst = con.prepareStatement(SQLQuery);
            pst.setString(1, value);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Hapus data berhasil.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            // Close the result set
            if (res != null) {try {res.close();} catch (SQLException e) {}}
            // Close the prepared statement
            if (pst != null) {try {pst.close();} catch (SQLException e) {}}
            // Close the connection
            if (con != null) {try {con.close();} catch (SQLException e) {}}
        }
    }
}
