/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tablas;

import java.sql.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;


public class TablaRegistroVentas{
   
	/**
	 * 
	 * @param tabla
	 */
    public void ver_tabla(JTable tabla){
        
        tabla.setDefaultRenderer(Object.class, new Render());
        
        DefaultTableModel d = new DefaultTableModel(new Object[]{"IdProducto", "Marca", "Modelo", "Talla", "Nick", "Dia", "Mes", "a�o", "Cantidad", "Precio"}, 0){
            
            Class[] types = new Class[]{String.class,String.class, String.class, Double.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Double.class};
            
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
                        
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
                      
        tabla.setModel(d);
        
        tabla.setPreferredScrollableViewportSize(tabla.getPreferredSize());
    }
    /*public int getCantidad(int fila){
        return getValue();
    }*/
    /*new Object[][]{{"Nike Huarache", "120 $", "✔", cuadroCantidad}, {"Adidas EQT W", "160 $", "✗", cuadroCantidad}},*/
    
}
