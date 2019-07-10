/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tablas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class TablaCarrito{
    
    private boolean[] editable = {false, false, false, false, false, false};
   
    /**
     * 
     * @param tabla
     */
    public void ver_tabla(JTable tabla){
        
        tabla.setDefaultRenderer(Object.class, new Render());
        
        DefaultTableModel d = new DefaultTableModel(new Object[]{"IdProducto", "Marca", "Modelo", "Talla", "Precio", "Cantidad"}, 0){
            
            Class[] types = new Class[]{Object.class, Object.class, Object.class, Object.class, Object.class, Integer.class};
            
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
                        
            @Override
            public boolean isCellEditable(int row, int column){
                return editable[column];
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
