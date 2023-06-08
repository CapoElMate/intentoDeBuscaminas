
package prueba2;
import java.awt.GridLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Prueba2 {

    private static final int N = 9; //N es el tamaño de la matriz
    private static final int anchoPantalla = 600; //N es el tamaño de la matriz
    
    private boolean[][] matrizBombas;     //esta es la matriz, true para que tiene una bomba, false vacio.
    private JButton[][] matrizBotones;
    
    private int cantImagenes = 10;
    private Image[] arregloImagenes;
    
    
    public Image accederImagen(int i, Image[] arr){
        return arr[i];
    }
    
    
    
    private Image[] cargarImagenes(){
        Image[] arr = new Image[cantImagenes];
        
        
        for (int i = 0; i < cantImagenes; i++) {
            arr[i] = buscarImagen(new Integer(i).toString()); // aprovecho que mis imagenes son todas numeros y se las paso asi. en caso contrario tendria que tener una func. que transforma el arreglo en nombre.
            System.out.println();
        }
        
//        System.out.println(arr.length);
        
        return arr;
    }
    
    
    public Image buscarImagen(String nombreImg){//la imagen tiene que estar en .png
        Image Imagen = null; //la inicio en null para q no joda :)
                
        
        File arch = new File("Prueba2.java");
                
        
        try {
            //new File("/src/imagenes/" + nombreImg+".png")
            
            //Recognize file as image
            Imagen = ImageIO.read(getClass().getClassLoader().getResource(nombreImg+".png"));
          
        } 
        catch (Exception e) 
        {
          //Display a message if something goes wrong
          JOptionPane.showMessageDialog( null, e.toString());
        }
        
        return Imagen;        
    }
    
    
    private boolean[][] generarBombas(int cantB){
        boolean M[][] = new boolean[N][N];
        Random rnd = new Random();
        int x,y;
        
        for (int i = 0; i < cantB; i++) {
            
            x = rnd.nextInt(N);
            y = rnd.nextInt(N);
            
            if(!checkBomba(x, y, M))
                M[x][y] = true;
            else
                i--;
            
        }
        
        return M;
    }
        
        
        
    private JButton[][] generarMatrizBotones(boolean[][] mBomba){
        
        JButton[][] mJButton = new JButton[mBomba.length][mBomba[0].length]; // matriz de jbuttons
        
        
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                JButton nueBtn  = new JButton();
                
                final Integer X,Y;
                X = i;
                Y = j;
                
                mJButton[i][j] = nueBtn;
                
                ActionListener al = new ActionListener(){
                    public void actionPerformed(ActionEvent e) { 
                        mostrarCasilla(nueBtn,X,Y);
                    } 
                };
                
                
                
                mJButton[i][j].addActionListener(al);
            }
        }
        
        return mJButton;        
    }
    
    public void mostrarCasilla(JButton jb,int indX, int indY){
        //escalo la imagen a la altura del boton.
        int tamanioPx = jb.getSize().height;        
        
        //por ahora: segun el indice X, pongo un nro o otro.
        
        int nro = chequearCasillas(indX, indY);
        
        System.out.println("nro: " +nro);
        
        jb.setIcon(new ImageIcon(accederImagen(nro,arregloImagenes).getScaledInstance(tamanioPx, tamanioPx, Image.SCALE_SMOOTH)));
    }
    
    //chequeo cuantas bombas hay alrededor de esa casilla
    public int chequearCasillas(int indX, int indY){
        int cantBombas = 0;
        int X,Y;
        
        //System.out.println("hay bomba? " + checkBomba(indX, indY,matrizBombas));
        
        if(!checkBomba(indX, indY,matrizBombas)){
            for (int i = -1; i <=1; i++) {
                for (int j = -1; j <=1; j++) {

                    X = indX + i;
                    Y = indY + j;                

                    if(estaDentroDelArreglo(X, Y)){
                        //System.out.println("esta dentro del arreglo.");
                        cantBombas += checkBomba(X, Y,matrizBombas) ? 1 : 0;                        
                        //System.out.println("cant b. "+cantBombas);                        
                    }
                        
                }
            }
        }
        else
            cantBombas = 9; // 9 = hay una bomba en esa casilla.
        
        return cantBombas;
    }
    
    public boolean checkBomba(int x, int y,boolean M[][]){
        //verifico que en mi arreglo matrizBombas, tenga una bomba en ese lugar        
        return (M[x][y]);
    }
    
    //verifico que la pos. a chekear este dentro del arreglo.
    public boolean estaDentroDelArreglo(int x, int y){
        //CONDICIONES: 
        //(X < N) && (Y<N) TIENEN que estar dentro de N, el tamaño del arreglo.
        //(X >= 0) && (Y >= 0) TIENEN que ser positivos.
        return (x < N) && (y<N) && (x >= 0) && (y >= 0);
    }
    
    
    
    
    
    private GridLayout generarRejilla(JButton[][] matrizBotones,JPanel jp){
        GridLayout grLayout = new GridLayout(N,N);
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                jp.add(matrizBotones[i][j]);
            }
        }
        
        return grLayout;
    }
    
    
    void mostrarTodo(boolean M[][]){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrizBotones[i][j].getActionListeners()[0].actionPerformed(null);
            }
        }
    }
    
    public void ventana(){
        
        
        JFrame jf = new JFrame("busca minas");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        jf.setPreferredSize(new Dimension(anchoPantalla+15,anchoPantalla+100));

        JPanel panelMinas= new JPanel();
        panelMinas.setSize(new Dimension(anchoPantalla,anchoPantalla));
        panelMinas.setPreferredSize(new Dimension(anchoPantalla,anchoPantalla));
        panelMinas.setLayout(new GridLayout(N,N));
        
        JPanel menu = new JPanel();
        JLabel texto = new JLabel();
        
        texto.setText("BUSCAMINAS");
        texto.setFont(new Font("unicode", 1, 20));
        
        
        menu.add(texto);
        jf.add(menu);
        jf.add(panelMinas);

        
        
        matrizBombas = generarBombas(10);
        matrizBotones = generarMatrizBotones(matrizBombas);
                
        jf.setResizable(false);
        generarRejilla(matrizBotones,panelMinas);
        
        //importante, carga las imagenes en memoria:
        arregloImagenes = cargarImagenes();
        
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        //mostrarTodo(matrizBombas);        
        System.out.println("arreglo de: " + arregloImagenes.length);
    }
    
    public static void main(String[] args) {
        new Prueba2().ventana();
    }
    
}
