package tablas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class TablaPedidos{
    
    private boolean[] editable = {false, false, false, false, false};
   
    /**
     * 
     * @param tabla
     */
    public void ver_tabla(JTable tabla){
        
        tabla.setDefaultRenderer(Object.class, new Render());
        
        DefaultTableModel d = new DefaultTableModel(new Object[]{"IdProducto", "Marca", "Modelo" , "Talla" ,"Cantidad" }, 0){
            
            Class[] types = new Class[]{Object.class, Object.class, Object.class, Object.class, Object.class};
            
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