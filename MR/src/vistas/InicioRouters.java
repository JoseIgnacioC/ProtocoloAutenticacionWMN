/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;
import interfaces.*;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import meshrouters.*;
/**
 *
 * @author Jose Ignacio
 */
public class InicioRouters extends javax.swing.JFrame {

    private ConexionMR conexion = new ConexionMR();
    
    /**
     * Creates new form InicioRouters
     */
    public InicioRouters() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        buttonIniciar = new javax.swing.JButton();
        boxNumberRouters = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Iniciar Mesh Routers");

        buttonIniciar.setText("Iniciar");
        buttonIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonIniciarActionPerformed(evt);
            }
        });

        boxNumberRouters.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3", "4", "5", "6", "7" }));
        boxNumberRouters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxNumberRoutersActionPerformed(evt);
            }
        });

        jLabel2.setText("Números de mesh routers en la red:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(buttonIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(boxNumberRouters, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(66, 66, 66))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxNumberRouters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(buttonIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonIniciarActionPerformed
                        
        
        String boxSeleccionado = (String)this.boxNumberRouters.getSelectedItem();
        int nroRouters = Integer.parseInt(boxSeleccionado);        
        PanelRouters.getInstanciaPanelRouters(nroRouters).setVisible(true);
        
        //Conectar con WMN
        try{
            if(conexion.comunicarWMNRMI()){
                boolean ingresado = conexion.solicitarRegistroMR_WMN(nroRouters);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(InicioRouters.class.getName()).log(Level.SEVERE, null, ex);                
        } catch (Exception ex) {
            Logger.getLogger(InicioRouters.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        PanelRouters.getInstanciaPanelRouters().setVisible(true);
        this.setVisible(false);               
        
        

    }//GEN-LAST:event_buttonIniciarActionPerformed

    private void boxNumberRoutersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxNumberRoutersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_boxNumberRoutersActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxNumberRouters;
    private javax.swing.JButton buttonIniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
