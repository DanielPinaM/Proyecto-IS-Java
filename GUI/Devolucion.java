/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import control.ControlCompraVenta;
import exception.ErrorEnParserException;
import exception.ProductoYaExistente;
import exception.TallaNoValidaException;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;

/**
 *
 * @author Death
 */
public class Devolucion extends javax.swing.JFrame {

	
	private ControlCompraVenta control;
	private VentanaPrincipal ventana;
	private Usuario usuario;
	

	/**
	 * 
	 * @param v ventana principal de la aplicacion
	 * @param u Usuario que devuelve
	 * @param controllerCompraVenta control de la compra y venta
	 */
    public Devolucion(VentanaPrincipal v, Usuario u, ControlCompraVenta controllerCompraVenta) {
    	super("Realizar devoluci�n");
    	this.control = controllerCompraVenta;
    	this.ventana = v;
    	this.usuario = u;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        realizarDevolucionLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        marcaLabel = new javax.swing.JLabel();
        modeloLabel = new javax.swing.JLabel();
        tallaLabel = new javax.swing.JLabel();
        cantidadLabel = new javax.swing.JLabel();
        marcaComboBox = new javax.swing.JComboBox<>();
        modeloText = new javax.swing.JTextField();
        tallaText = new javax.swing.JTextField();
        cantidadSpinner = new javax.swing.JSpinner();
        jSeparator2 = new javax.swing.JSeparator();
        botonDevolver = new javax.swing.JButton();
        botonCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        realizarDevolucionLabel.setFont(new java.awt.Font("Dialog", 2, 18)); // NOI18N
        realizarDevolucionLabel.setText("REALIZAR DEVOLUCI�N");

        marcaLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        marcaLabel.setText("Marca");

        modeloLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        modeloLabel.setText("Modelo");

        tallaLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tallaLabel.setText("Talla");

        cantidadLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        cantidadLabel.setText("Cantidad");

        marcaComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nike", "Adidas", "Puma", "New Balance" }));

        botonDevolver.setText("Devolver");
        botonDevolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDevolverActionPerformed(evt);
            }
        });

        botonCancelar.setText("Cancelar");
        botonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelPrincipalLayout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(marcaLabel)
                                    .addComponent(modeloLabel)
                                    .addComponent(tallaLabel)
                                    .addComponent(cantidadLabel))
                                .addGap(87, 87, 87)
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(marcaComboBox, 0, 125, Short.MAX_VALUE)
                                    .addComponent(modeloText)
                                    .addComponent(tallaText)
                                    .addComponent(cantidadSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelPrincipalLayout.createSequentialGroup()
                                .addGap(72, 72, 72)
                                .addComponent(realizarDevolucionLabel)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addComponent(jSeparator2))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(botonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(botonDevolver, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(realizarDevolucionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(marcaLabel)
                    .addComponent(marcaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modeloLabel)
                    .addComponent(modeloText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tallaLabel)
                    .addComponent(tallaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cantidadLabel)
                    .addComponent(cantidadSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonDevolver)
                    .addComponent(botonCancelar))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        this.setLocation(500, 300);
    }// </editor-fold>                        

    /**
     * 
     * @param evt
     */
    private void botonDevolverActionPerformed(java.awt.event.ActionEvent evt) {
    	String marca = (String) this.marcaComboBox.getSelectedItem();
        String modelo = this.modeloText.getText();
        String talla2 = this.tallaText.getText();
        int cantidad  = (int) this.cantidadSpinner.getValue();
        
        if(marca.equals("") || modelo.equals("") || talla2.equals("")) {
        	JOptionPane.showMessageDialog(this,
					"Para devolver un producto debe rellenar todos los campos",
					"Error",
					JOptionPane.ERROR_MESSAGE);
        }
        else if(cantidad == 0 || cantidad < 0) {
        	JOptionPane.showMessageDialog(this,
					"No se pueden devolver 0 unidades o menos",
					"Error",
					JOptionPane.ERROR_MESSAGE);
        }
        
        else {
        	try {
        		Double.parseDouble(talla2);
        		double talla = Double.valueOf(talla2).doubleValue();
        		if(talla < 0)
        			throw new ErrorEnParserException("La talla no puede ser negativa");
        		Producto p = new Producto(marca, modelo, talla, 0, cantidad);
        		try {
        			this.control.realizarDevolucion(p, this.usuario.getNick());
					JOptionPane.showMessageDialog(this,
	    					"Producto devuelto correctamente !",
	    					"�xito",
	    					JOptionPane.INFORMATION_MESSAGE);
	        		this.setVisible(false);
	        		this.ventana.actualizarTablaPrincipal();
	        		this.ventana.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this,
							e.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
        	} catch (NumberFormatException excepcion){
        		JOptionPane.showMessageDialog(this,
        				"La talla debe de ser un valor num�rico",
						"Error",
						JOptionPane.ERROR_MESSAGE);
        	} catch (ErrorEnParserException e) {
        		JOptionPane.showMessageDialog(this,
        				e.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
        	}
        }
    	
    }                                             

    /**
     * 
     * @param evt
     */
    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {                                              
        this.setVisible(false);
        this.ventana.setVisible(true);
    }                                             

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new Devolucion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton botonCancelar;
    private javax.swing.JButton botonDevolver;
    private javax.swing.JLabel cantidadLabel;
    private javax.swing.JSpinner cantidadSpinner;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox<String> marcaComboBox;
    private javax.swing.JLabel marcaLabel;
    private javax.swing.JLabel modeloLabel;
    private javax.swing.JTextField modeloText;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JLabel realizarDevolucionLabel;
    private javax.swing.JLabel tallaLabel;
    private javax.swing.JTextField tallaText;
    // End of variables declaration                   
}