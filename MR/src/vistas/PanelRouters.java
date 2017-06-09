/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Jose Ignacio
 */
public class PanelRouters extends javax.swing.JFrame {

    private static PanelRouters instanciaPanelRouters;//referencia a si mismo
    
    private ArrayList<DefaultListModel> listaAutenticadosRouter;
                
    public PanelRouters() {
        initComponents();      
        listaAutenticadosRouter = new ArrayList<>();
    }
    public PanelRouters(int nroRouters){
        initComponents();
        listaAutenticadosRouter = new ArrayList<>();
        
        this.textAreaNotif.append("\n");
        
        for(int i=1; i <= nroRouters; i++){
            
            listaAutenticadosRouter.add(new DefaultListModel());
            
            JPanel panel = new JPanel();
            GridLayout gl = new GridLayout(1,2);
            gl.setHgap(5);
            panel.setLayout(gl);
                                
            JPanel col1 = new JPanel();
            GridLayout glSec = new GridLayout(2,1);
            glSec.setVgap(5);
            col1.setLayout(glSec);
            
            
            JPanel fila1 = new JPanel();
            BorderLayout bl = new BorderLayout();
            bl.setHgap(25);
            fila1.setLayout(bl);
            
            JLabel labelIdRouter = new JLabel("Identificador: ");
            JTextField fieldIdRouter = new JTextField(11);
            fieldIdRouter.setText("Router"+i);
            fieldIdRouter.setEditable(false);
            JPanel subFila1 = new JPanel();            
            FlowLayout flowL = new FlowLayout();
            flowL.setAlignment(FlowLayout.LEFT);
            subFila1.setLayout(flowL);            
            subFila1.add(labelIdRouter);subFila1.add(fieldIdRouter);
            
            
            JLabel labelNroMCs = new JLabel("Número de MCs: ");
            JTextField fieldNroMCs = new JTextField(9);
            fieldNroMCs.setText("0");
            fieldNroMCs.setEditable(false);
            JPanel subFila2 = new JPanel();
            FlowLayout flowL2 = new FlowLayout();
            flowL2.setAlignment(FlowLayout.LEFT);
            subFila2.setLayout(flowL2);
            subFila2.add(labelNroMCs); subFila2.add(fieldNroMCs);
            
            fila1.add(subFila1,BorderLayout.NORTH);
            fila1.add(subFila2,BorderLayout.CENTER);
            TitledBorder borderTitle = BorderFactory.createTitledBorder("Informacion del router: ");
            fila1.setBorder(BorderFactory.createCompoundBorder(borderTitle, BorderFactory.createEmptyBorder(25, 0, 0, 0)));
                                    
            JTextArea areaNotificaciones = new JTextArea(60, 30);
            areaNotificaciones.setEditable(false);
            JPanel fila2 = new JPanel();            
            fila2.setLayout(new BorderLayout());            
            fila2.add(areaNotificaciones, BorderLayout.CENTER);
            fila2.setBorder(BorderFactory.createTitledBorder("Notificaciones: "));
            
            col1.add(fila1);
            col1.add(fila2);
                        
            JList listaLlaves = new JList();
            JPanel col2 = new JPanel();
            col2.setLayout(new BorderLayout());
            col2.setBorder(BorderFactory.createTitledBorder("Llaves de sesion con ..."));
            col2.add(listaLlaves);
                        
            panel.add(col1);
            panel.add(col2);
            this.tabPanelRouters.addTab("Router"+i, panel);
        }
    }

    public static PanelRouters getInstanciaPanelRouters(){
        if(instanciaPanelRouters == null){
            instanciaPanelRouters = new PanelRouters();
        }
        return instanciaPanelRouters;
    }
    public static PanelRouters getInstanciaPanelRouters(int nroRouters){
        if(instanciaPanelRouters == null){            
            instanciaPanelRouters = new PanelRouters(nroRouters);
        }
        return instanciaPanelRouters;
    }
    
    /*public void agregarMensaje(String mensaje, int indiceRouter){        
        //this.textAreaNotif.append("Nuevo mensaje para Mesh Router "+indiceRouter+"\n");                
        JPanel jp = (JPanel)this.tabPanelRouters.getComponentAt(indiceRouter);        
        JTextArea ta = (JTextArea)jp.getComponent(0);                
        ta.append(mensaje+"\n");
        
    }*/
    public void agregarRouterAutenticado(int indiceRouter, int indiceAutenticado){
        
        this.listaAutenticadosRouter.get(indiceRouter-1).addElement("Router"+indiceAutenticado);
        JPanel pestana = (JPanel)this.tabPanelRouters.getComponentAt(indiceRouter);
        JPanel col2 = (JPanel) pestana.getComponent(1);
        JList lista = (JList) col2.getComponent(0);
        lista.setModel(this.listaAutenticadosRouter.get(indiceRouter-1));                                
        
    }
    public void agregarAutenticado(int idRouter, String idAutenticado){
        
        this.listaAutenticadosRouter.get(idRouter-1).addElement(idAutenticado);
        JPanel pestana = (JPanel)this.tabPanelRouters.getComponentAt(idRouter);
        JPanel col2 = (JPanel) pestana.getComponent(1);
        JList lista = (JList) col2.getComponent(0);
        lista.setModel(this.listaAutenticadosRouter.get(idRouter-1));      
        
        JPanel col1 = (JPanel) pestana.getComponent(0);
        JPanel fila1 = (JPanel) col1.getComponent(0);
        JPanel subFila2 = (JPanel) fila1.getComponent(1);
        JTextField txtField = (JTextField) subFila2.getComponent(1);
        
        String strNro = txtField.getText();
        int nro = Integer.parseInt(strNro);
        nro++;
        txtField.setText(nro+"");
        
        String mensaje = "Autenticación terminada con "+idAutenticado+".";
        agregarNotificacion(idRouter, mensaje);        
    }
    public void agregarNotificacion(int idRouter, String mensaje){
                
        JPanel pestana = (JPanel)this.tabPanelRouters.getComponentAt(idRouter);
        JPanel col1 = (JPanel) pestana.getComponent(0);
        JPanel fila2 = (JPanel) col1.getComponent(1);
        JTextArea txtArea = (JTextArea) fila2.getComponent(0);
        txtArea.append(mensaje+"\n");        
    }
    public void agregarNotificacion(String mensaje){
        this.textAreaNotif.append(mensaje+"\n");        
    }    
    
    /*public void agregarAutenticado(String nombreAut, String fecha, int indiceRouter){
        
        JPanel jp = (JPanel)this.tabPanelRouters.getComponentAt(indiceRouter);
        JPanel jp2 = (JPanel)jp.getComponent(1);
        JTextArea ta = (JTextArea)jp2.getComponent(0);
        ta.append(nombreAut+fecha+"\n");
    }*/
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tabPanelRouters = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaNotif = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Panel Mesh Routers");

        textAreaNotif.setEditable(false);
        textAreaNotif.setBackground(new java.awt.Color(240, 240, 240));
        textAreaNotif.setColumns(5);
        textAreaNotif.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaNotif.setRows(20);
        textAreaNotif.setText("Bienvenido al panel de control de los Mesh Routers");
        textAreaNotif.setAutoscrolls(false);
        textAreaNotif.setBorder(javax.swing.BorderFactory.createTitledBorder("Notificaciones"));
        jScrollPane3.setViewportView(textAreaNotif);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        tabPanelRouters.addTab("General", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanelRouters)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabPanelRouters)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane tabPanelRouters;
    private javax.swing.JTextArea textAreaNotif;
    // End of variables declaration//GEN-END:variables
}
