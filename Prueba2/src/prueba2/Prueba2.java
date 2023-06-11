
package prueba2;
import java.awt.GridLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

    private static final int N = 10; //N es el tamaño de la matriz
    private static final int cantBombas = 11;
    private static final int anchoPantalla = 600; //N es el tamaño de la matriz
    
    private boolean[][] matrizBombas;     //esta es la matriz, true para que tiene una bomba, false vacio.
    private boolean[][] matrizBanderas;   //esta es la matriz, true para que tiene una bandera, false vacio.
    private JButton[][] matrizBotones;
    
    private int cantImagenes = 11;
    private Image[] arregloImagenes;
    
    //funcion por implementar
    private int cantBanderas = cantBombas;
    
    
    
    
    
    public Icon accederImagen(int i){
        return new ImageIcon(arregloImagenes[i]);
    }
    
    private Image[] cargarImagenes(){
        Image[] arr = new Image[cantImagenes];
        
        //escalo la imagen a la altura del boton.
        int tamanioPx = matrizBotones[0][0].getSize().height;
        
        System.out.println("tam "+tamanioPx);
        
        for (int i = 0; i < cantImagenes; i++) {
            arr[i] = buscarImagen(""+i).getScaledInstance(tamanioPx, tamanioPx, Image.SCALE_SMOOTH); // aprovecho que mis imagenes son todas numeros y se las paso asi. en caso contrario tendria que tener una func. que transforma el arreglo en nombre.
        }
        
        return arr;
    }
    
    
    public Image buscarImagen(String nombreImg){//la imagen tiene que estar en .png
        Image Imagen = null; //la inicio en null para q no joda :)
                        
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
        
        if(cantB > N*N)
            cantB = N*N;
        
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
                
                
                //ahora uso un mouse adapter para tambien detectar el click derecho:
                mJButton[i][j].addMouseListener(new MouseAdapter(){
                    
                    boolean pressed;
                    
                    @Override
                    public void mousePressed(MouseEvent e) {
                        nueBtn.getModel().setArmed(true);
                        nueBtn.getModel().setPressed(true);
                        pressed = true;
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        nueBtn.getModel().setArmed(false);
                        nueBtn.getModel().setPressed(false);

                        if (pressed) {
                            if (SwingUtilities.isRightMouseButton(e)) {
                                //comportamiento click derecho:
                                banderita(nueBtn,X,Y);
                                comprobarSiGane();
                                
                                actualizarCantBanderines();
                                
                            }
                            else {
                                //comportamiento click izquierdo:
                                procesarClickIzquierdo(nueBtn,X,Y);
                            }
                        }
                        pressed = false;

                    }
                });
            }
        }
        
        return mJButton;        
    }
    
    private void procesarClickIzquierdo(JButton nueBtn,int X, int Y){
        //ImageIcon im = new ImageIcon(arregloImagenes[10]);                                
                                
        if(nueBtn.getIcon() == null){
            //si no habia nada:
            deshabilitarMouseListener(nueBtn);
            mostrarCasilla(nueBtn,X,Y);
        }
    }
    
    public void banderita(JButton jb,int indX, int indY){
            
        
        //por ahora: segun el indice X, pongo un nro o otro.
        
        boolean bandera = matrizBanderas[indX][indY];
        
        //ImageIcon im = new ImageIcon(arregloImagenes[10]);
        
        //if ((jb.getIcon() != null) && !jb.getIcon().equals(im)){
        if (bandera){
            //si haces click derecho en una banderita:
            jb.setIcon(null);
            matrizBanderas[indX][indY] = false;
            cantBanderas++;
        }else{
            //si no habia nada:
            jb.setIcon(accederImagen(10));
            matrizBanderas[indX][indY] = true;   
            cantBanderas--;            
        }
            
    }
    
    private void esperar(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(Prueba2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void perder(int x, int y){
        
        recorrerMatrizCaracol2(x,y);
        
    }
    
    private void comprobarSiGane(){
        
        int coincidencias = cantBombas ;
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(matrizBombas[i][j])
                    coincidencias -= (matrizBombas[i][j] == matrizBanderas[i][j]) ? 1: 0;
            }
        }
        
        System.out.println("coinc:" + coincidencias);
        
        if(coincidencias == 0){
            ganar();
        }
    }
    
    private void ganar(){
        System.out.println("ganaste!");
        recorrerMatrizCaracol(N/2,N/2);
        
        Icon icono = new ImageIcon(buscarImagen("9").getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        
        JOptionPane.showMessageDialog(jf,
        "Ganaste el buscaminas!",
        "Felicitaciones!",
        JOptionPane.PLAIN_MESSAGE,
        icono);
        }
    
    
    
    public void recorrerMatrizCaracol2(int x , int y){
        
        int tam = N*2; //tamaño del caracol
        
        System.out.println("tam:" + tam);
        
        mostrarCasillaConCheck(x, y);
        
        for (int a = 2; a < tam; a+=2) {
            
            x++;
            y--;
            
            for(int i = 0; i<a; i++){
                y++;
                mostrarCasillaConCheck(x, y);
            }
            
            for(int i = 0; i<a ; i++){
                x--;
                mostrarCasillaConCheck(x, y);
            }
            
            for(int i = 0; i<a ; i++){
                y--;
                mostrarCasillaConCheck(x, y);
            }
            
            for(int i = 0; i<a ; i++){
                x++;
                mostrarCasillaConCheck(x, y);
            }
            
            esperar(50);
            
        }
    }
    
    public void mostrarCasillaConCheck(int x, int y){
        if (estaDentroDelArreglo(x,y)){
            mostrarCasillaPerder(x,y);
            deshabilitarMouseListener(matrizBotones[x][y]);
        }
        
    }
    
    //FUNCIONA NO LO PUEDO CREER
    public void deshabilitarMouseListener(JButton jb){
        MouseListener[] mls = jb.getMouseListeners();
        
        jb.getModel().setArmed(false);
        jb.getModel().setPressed(false);
        
        for (int i = 0; i < mls.length; i++) {
            jb.removeMouseListener(mls[i]);
        }
        
    }
    
    
    public void recorrerMatrizCaracol(int x, int y) throws ArrayIndexOutOfBoundsException{
        
        int C = 7;
        int Z = C * 2;
        
        int offstetX = x  - Z;
        int offstetY = y - Z;
                
        
        for(int a = Z; a >= C ; a--){
           for (int i = a; i < N - a; i++) {
                if (estaDentroDelArreglo(a + offstetX  , i +offstetY))
                    mostrarCasillaPerder(a + offstetX  , i +offstetY);
            }
            
            for (int i = a; i < N - a; i++) {
                if (estaDentroDelArreglo(i + offstetX  , N - a -1 + offstetY ))
                    mostrarCasillaPerder(i + offstetX  , N - a -1 + offstetY);
            }
            
            for (int i = N - a - 1; i >= a ; i--) {
                if (estaDentroDelArreglo(N - a -1  + offstetX , i +offstetY))
                    mostrarCasillaPerder(N - a -1  + offstetX , i +offstetY);
            }
            
            for (int i = N - a -1; i >= a; i--) {
                if (estaDentroDelArreglo(i + offstetX  , a +offstetY))
                    mostrarCasillaPerder(i + offstetX , a +offstetY);
            }
            
        }
    }
    
    public void recorrerMatrizCaracol(int n) {
        
        
        for (int a = 0; a <= (n / 2) +1; a++) {
            
            for (int i = a; i < n - a; i++) {
                
                mostrarCasillaPerder(a , i);
            }
            
            for (int i = a; i < n - a; i++) {
                
                mostrarCasillaPerder(i , n - a -1);
            }
            
            for (int i = n - a - 1; i >= a ; i--) {
                
                mostrarCasillaPerder(n - a -1 , i);
            }
            
            for (int i = n - a -1; i >= a; i--) {
                
                mostrarCasillaPerder(i , a);
            }
            
            esperar(400);
            
        }
        if (n % 2 == 1) {
            mostrarCasillaPerder(n / 2 + 1 , n / 2 + 1);
        }
    }
    
    
    public void mostrarCasillaPerder(int indX, int indY){
        
        JButton jb = matrizBotones[indX][indY];
        
        
        //escalo la imagen a la altura del boton.
        int tamanioPx = jb.getSize().height;        
        
        //por ahora: segun el indice X, pongo un nro o otro.
        
        int nro = bombasAlrededor(indX, indY);
        
       // System.out.println("nro: " +nro);
               
        jb.setIcon(accederImagen(nro));
        jb.update(jb.getGraphics()); // updateo los graficos del boton
                
    }
    
    public void mostrarCasilla(JButton jb,int indX, int indY){
        //escalo la imagen a la altura del boton.
        int tamanioPx = jb.getSize().height;        
        
        //por ahora: segun el indice X, pongo un nro o otro.
        int nro = bombasAlrededor(indX, indY);
        
        System.out.println("nro: " +nro);
        
    
        if(nro != 9)        
            jb.setIcon(accederImagen(nro));
        
        else
            perder(indX, indY);
        
        if(nro == 0)
            casillaBlanca(indX, indY);            
    }
    
    
    
    //chequeo cuantas bombas hay alrededor de esa casilla
    public int bombasAlrededor(int indX, int indY){
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
                //matrizBotones[i][j].getActionListeners()[0].actionPerformed(null);
                mostrarCasilla(matrizBotones[i][j], i , j);
            }
        }
    }
    
    
    private void casillaBlanca(int indX, int indY){
         
        int X,Y;
        
        for (int i = -1; i <=1; i++) {
                for (int j = -1; j <=1; j++) {

                    X = indX + i;
                    Y = indY + j;                

                    if(estaDentroDelArreglo(X, Y)){
                        procesarClickIzquierdo(matrizBotones[X][Y], X, Y);
                    }
                        
                    
                }
            }
    }
    
    
    private void actualizarCantBanderines(){
        texto.setText("minas restantes: " + cantBanderas);
    }
    
    
    JLabel texto = new JLabel();
    JFrame jf = new JFrame("busca minas");
    
    public void ventana(){
        
        
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        jf.setPreferredSize(new Dimension(anchoPantalla+15,anchoPantalla+100));

        JPanel panelMinas= new JPanel();
        panelMinas.setSize(new Dimension(anchoPantalla,anchoPantalla));
        panelMinas.setPreferredSize(new Dimension(anchoPantalla,anchoPantalla));
        panelMinas.setLayout(new GridLayout(N,N));
        
        JPanel menu = new JPanel();
        
        
        texto.setText("minas restantes: " + cantBanderas);
        texto.setFont(new Font("unicode", 1, 20));
        
        
        menu.add(texto);
        jf.add(menu);
        jf.add(panelMinas);
        
        
        
        matrizBombas = generarBombas(cantBombas);
        matrizBanderas = new boolean[N][N];
        matrizBotones = generarMatrizBotones(matrizBombas);
                
        jf.setResizable(false);
        generarRejilla(matrizBotones,panelMinas);
        
        
        //cargo todo antes de iniciar el arreglo de imagenes, importante.
        jf.pack();
        
        //importante, carga las imagenes en memoria:
        arregloImagenes = cargarImagenes();
        
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        //mostrarTodo(matrizBombas);        
        System.out.println("arreglo de: " + arregloImagenes.length);
    }
    
    public static void main(String[] args) {
        new Prueba2().ventana();
    }
    
}
