/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake;

/**
 * se a añadido sonido de fondo, ademas se modificaron los audios 
   para que fueran menos pesados.
   se añadio pause a la musica de fondo al momento de game over tanto al momento de reiniciar el juego
 * 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;



public class SnakeGame extends JPanel implements ActionListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int TILE_SIZE = 20;
    private static final int DELAY = 200;
    
     private long ultimoTiempoPresionado = 0; // Tiempo de la última tecla presionada
     private final long tiempoDeEspera = 199; // Tiempo de espera en milisegundos (100 ms en este caso)
    
    
  private boolean canKeyPress = true;

    
    private LinkedList<Point> obstaculo; //se añade la lista que contendra los obstaculos
    private LinkedList<Point> snake;
    private Point fruit;
    private int direction;
    private  boolean isGameOver = false;
    
    //AQUI ALMACENAREMOS EL SONIDO IMPORTADO
    private Clip clip;
    private Clip clipFondo;
    
    private BufferedImage snakeHeadImage;
    private BufferedImage manzana;
       private BufferedImage cuerposerpiente;
    
    public SnakeGame() throws LineUnavailableException {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        
        
        
        
     

// En el constructor o en algún lugar donde se inicialicen tus recursos
try {
    cuerposerpiente = ImageIO.read(getClass().getResourceAsStream("/images/cuerposerpiente.png"));
    System.err.println("al parecer el cuerpo cargo correctamente");
} catch (IOException e) {
    e.printStackTrace();
}

        
        
        
        
        //añadimo es temporsizador para ver si podemos apretar la proxima tecla
        canKeyPress = false;
    Timer timer2 = new Timer(210, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            canKeyPress = true;
            ((Timer)e.getSource()).stop(); // Detener el temporizador
        }
    });
    timer2.setRepeats(false); // El temporizador solo se ejecutará una vez
    timer2.start();

        
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                
                 long tiempoActual = System.currentTimeMillis();

        // Verifica si ha pasado suficiente tiempo desde la última tecla presionada
        if (tiempoActual - ultimoTiempoPresionado >= tiempoDeEspera) {
            // Tu código para manejar la tecla presionada aquí
            
                int key = e.getKeyCode();
                if (!canKeyPress) {
                 return;
                }
   
                if ((key == KeyEvent.VK_LEFT) && (direction != KeyEvent.VK_RIGHT)) {
                    direction = KeyEvent.VK_LEFT;
                } else if ((key == KeyEvent.VK_RIGHT) && (direction != KeyEvent.VK_LEFT)) {
                    direction = KeyEvent.VK_RIGHT;
                } else if ((key == KeyEvent.VK_UP) && (direction != KeyEvent.VK_DOWN)) {
                    direction = KeyEvent.VK_UP;
                } else if ((key == KeyEvent.VK_DOWN) && (direction != KeyEvent.VK_UP)) {
                    direction = KeyEvent.VK_DOWN;
                }
                
              
                // AQUI AÑADIMOS EL METODO PARA REINICIAR EL JUEGO CON LA TECLA F2
                 if(key == KeyEvent.VK_F2){
                     restartGame();
               
                }
            
            

            // Actualiza el tiempo de la última tecla presionada
            ultimoTiempoPresionado = tiempoActual;
        }
                
                
                
            
                
                
            }
        });
        
        
        
        
        
        
                 // Carga el sonido
                
        
         loadImages();
         loadImages2();
         
            //metodo para el sonido de fondo 
            
            cargarSonidoFondo();
         
         
        
     
         
         
         //se añade un obstaculo
          setupObstacles();

        snake = new LinkedList<>();
        snake.add(new Point(5, 5));
        snake.add(new Point(4, 5)); //añadimos un segundo segmento al snake 
        fruit = generateFruit();
        direction = KeyEvent.VK_RIGHT;
       // isGameOver = false;

        Timer timer3 = new Timer(DELAY, this);
        timer3.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            try {
                move();
            } catch (LineUnavailableException ex) {
                Logger.getLogger(SnakeGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            checkCollision();
            repaint();
        }
    }

    private void move() throws LineUnavailableException { //throws hace referencia que el codigo que llame este metodo tendra que implementar las execpiones en este caso las de sonido
        Point head = snake.getFirst();
        Point newHead = head;

        switch (direction) {
            case KeyEvent.VK_LEFT:
                 
               newHead = new Point(head.x - 1, head.y);
                break;
            case KeyEvent.VK_RIGHT:
                newHead = new Point(head.x + 1, head.y);
                break;
            case KeyEvent.VK_UP:
                newHead = new Point(head.x, head.y - 1);
                break;
            case KeyEvent.VK_DOWN:
                newHead = new Point(head.x, head.y + 1);
                break;
        }

        snake.addFirst(newHead);

        if (newHead.equals(fruit)) {
               cargarSonido();
            clip.start();
            fruit = generateFruit();
        } else {
            snake.removeLast();
        }
    }
    
    

    private void checkCollision() {
        Point head = snake.getFirst();
        
        //collision entre la misma serpiente
        for (int i = 1; i < snake.size(); i++) {
        if (head.equals(snake.get(i))) {
            isGameOver = true;
            return; // Sal del método inmediatamente si hay colisión
        }
    }
        
        
        // si la cabeza esta en la posision de alguno de los obsataculos entoces isGameover sera verdadero 
         if (obstaculo.contains(head)) {
        isGameOver = true;
        return; // Sal del método inmediatamente si hay colisión
        }
        
        if (head.x <0 || head.y <0 || head.x >= 20 || head.y >= 20 ) {
            isGameOver = true;
            
        }

       /* if (head.x < 0 || head.x >= WIDTH / TILE_SIZE ||
                head.y < 0 || head.y >= HEIGHT / TILE_SIZE ||
                snake.contains(head)) {
          //  isGameOver = true;
       }   */
    }

  private Point generateFruit() {
    Point newFruit;
    do {
        int x = (int) (Math.random() * (WIDTH / TILE_SIZE));
        int y = (int) (Math.random() * (HEIGHT / TILE_SIZE));
        newFruit = new Point(x, y);
    }while (snake.contains(newFruit) || obstaculo.contains(newFruit));// Verificar que la fruta no esté en el
                                                                         // cuerpo de la serpiente ni del obstaculo

    return newFruit;
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isGameOver) {
            clipFondo.stop();
            gameOver(g);
            return;
        }

        //este metodo habilita la trasparencia de la imagen png
        Graphics2D g2d = (Graphics2D) g;
       g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // 1.0f es opacidad completa
        
        // dibuja el punto rojo g.setColor(Color.RED);
        
       // g.fillRect(fruit.x * TILE_SIZE, fruit.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
      // g.drawImage(manzana, manzana.fruit.x * TILE_SIZE, manzana.fruit.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
       g.drawImage(manzana, fruit.x * TILE_SIZE, fruit.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);


          // Dibuja los obstáculos
    for (Point obstacle : obstaculo) {
        g.setColor(Color.gray); // Cambia el color o usa una imagen para los obstáculos
        g.fillRect(obstacle.x * TILE_SIZE, obstacle.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

      //  g.fillRect(obstacle.getX() * TILE_SIZE, obstacle.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
       
       
       
       
        g.setColor(Color.red);
       
       /* for (Point segment : snake) {
            g.fillRect( segment.x * TILE_SIZE, segment.y * TILE_SIZE, TILE_SIZE, TILE_SIZE );
             
        } */
       
       // Dibuja el cuerpo de la serpiente
        for (Point segment : snake) {
            g.drawImage(cuerposerpiente, segment.x * TILE_SIZE, segment.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
        }

       
    }

    private void gameOver(Graphics g) {
        String message = "Game Over";
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString(message, (WIDTH - g.getFontMetrics().stringWidth(message)) / 2, HEIGHT / 2);
    }
    
    //metodo para cargar las imagen de la manzana
    private void loadImages() {
    try {
        manzana = ImageIO.read(getClass().getResourceAsStream("/images/manzana3.png"));
       // manzana = ImageIO.read(new File("/images/manzana.png"));
        
    } catch (IOException e) {
        System.err.println("No cargo la imagen de la manzana");
        e.printStackTrace();
    }
    
    
}
    
      private void loadImages2() {
    try {
        manzana = ImageIO.read(getClass().getResourceAsStream("/images/manzana3.png"));
       // manzana = ImageIO.read(new File("/images/manzana.png"));
        
    } catch (IOException e) {
        System.err.println("No cargo la imagen de la manzana");
        e.printStackTrace();
    }
      }
      
      
    
    
   
      
      
      
      // añadimos el siguiente metodo para restablecer el juego 
      // Restablece todas las variables del juego a su estado inicial
      private void restartGame() {
    clipFondo.stop();
    snake.clear();
    snake.add(new Point(5, 5));
    snake.add(new Point(4, 5));
    direction = KeyEvent.VK_RIGHT;
    cargarSonidoFondo();
      setupObstacles();
    
    isGameOver = false;
    fruit = generateFruit();
  

    // Repinta el juego
    repaint();
      }
      
   //AÑADIMOS EL METODO PARA EL OBSTACULO   
      private void setupObstacles() {
    obstaculo = new LinkedList<>();  // Creamos una nueva lista de obstáculos
    
    // Añadimos los puntos que representan el obstáculo uno por uno
    
    obstaculo.add(new Point(9, 6));
    obstaculo.add(new Point(8, 6));
    obstaculo.add(new Point(7, 6));
    obstaculo.add(new Point(6, 6));
    obstaculo.add(new Point(11, 6));
    obstaculo.add(new Point(12, 6));
    obstaculo.add(new Point(13, 6));
    obstaculo.add(new Point(14, 6));
    
        // Añadimos los puntos que representan el obstáculo uno por uno
    obstaculo.add(new Point(10, 9));
    obstaculo.add(new Point(9, 9));
    obstaculo.add(new Point(8, 9));
    obstaculo.add(new Point(7, 9));
    obstaculo.add(new Point(6, 9));
    obstaculo.add(new Point(11, 9));
    obstaculo.add(new Point(12, 9));
    obstaculo.add(new Point(13, 9));
    obstaculo.add(new Point(14, 9));
    
       obstaculo.add(new Point(10, 9));
    obstaculo.add(new Point(9, 12));
    obstaculo.add(new Point(8, 12));
    obstaculo.add(new Point(7, 12));
    obstaculo.add(new Point(6, 12));
    obstaculo.add(new Point(11, 12));
    obstaculo.add(new Point(12, 12));
    obstaculo.add(new Point(13, 12));
    obstaculo.add(new Point(14, 12));
}
      
      //METODO PARA AGREGAR EL SONIDO CUANDO LA SERPIENTE SE COMA UNA FRUTA
    private void cargarSonido() {
          try {
        // Carga el archivo de sonido desde el paquete de recursos
              System.out.println("Al parecer el sonido cargo CORRECTACMENTE");
        InputStream inputStream = getClass().getResourceAsStream("/sonidos/sonidoComiendo.wav");
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
         clip.start();
    } catch (Exception e) {
        System.err.println("Al parecer no se cargó el sonido");
        e.printStackTrace();
    }
    }
    
    
    
    //CARGAR SONIDO DE FONDO
    
     private void cargarSonidoFondo() {
          try {
        // Carga el archivo de sonido desde el paquete de recursos
              System.out.println("Al parecer el sonido cargo CORRECTACMENTE");
        InputStream inputStream = getClass().getResourceAsStream("/sonidos/sonido.wav");
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
        clipFondo = AudioSystem.getClip();
        clipFondo.open(audioInputStream);
         clipFondo.start();
    } catch (Exception e) {
        System.err.println("Al parecer no se cargó el sonido");
        e.printStackTrace();
    }
    }


    public static void main(String[] args) throws LineUnavailableException {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame snakeGame = new SnakeGame();
        frame.add(snakeGame);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


