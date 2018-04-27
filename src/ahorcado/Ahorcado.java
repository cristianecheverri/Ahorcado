package ahorcado;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;

/**
 *
 * @author Cristian Camilo Echeverri 
 * Git: cristianecheverri 
 * Primer proyecto Estructuras de datos 
 * Ingeniería en informática 
 * Tercer semestre 
 * Universidad de Caldas - Colombia
 */
public class Ahorcado extends JFrame {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> cboCategorias; //Categorias de cada array de palabras
    private static JButton[] btnAlfabeto = new JButton[27]; //Letras del alfabeto
    private JButton botonPulsado; //Botón elegido por el usuario
    private JLabel lblEstado = new JLabel(); //Etiqueta de ganó o perdió
    private JLabel lblImagenEstado = new JLabel(); //La imagen del ahorcado
    private int intentosFallidos = 0; //Las veces que no acierta con la letra elegida
    private JLabel[] lblLetras = new JLabel[15]; //Número de letras de la palabra a buscar
    private String palabraSeleccionada; //Palabra seleccionada al azar
    private JLabel cantidadIntentosFallidos; //Muestra la cantidad de intentos fallidos en el juego

    private boolean sonido = true; //Guarda el estado del sonido: activado/desactivado

    //ArrayList que contiene los ArrayList contenedores de las palabras de las categorias
    private final ArrayList<ArrayList<String>> arrayPalabras = new ArrayList<>();

    private final ArrayList<String> pais = new ArrayList<>(); //Colección de palabras de paises
    private final ArrayList<String> sistemaOperativo = new ArrayList<>(); //Colección de palabras de sistemas operativos
    private final ArrayList<String> fruta = new ArrayList<>(); //Colección de palabras de frutas
    private final ArrayList<String> instrumento = new ArrayList<>(); //Colección de palabras de instrumentos musicales
    private final ArrayList<String> nombre = new ArrayList<>(); //Colección de palabras de nombres

    private final String imagenes[] = {"animals1.png", "hangman-1.jpg", "hangman-2.jpg",
        "hangman-3.jpg", "hangman-4.jpg", "hangman-5.jpg", "hangman-6.jpg",
        "hangman-7.jpg", "hangman-8.jpg", "hangman-9.jpg"}; //Imágenes de ahorcado

    private final String paises[] = new String[]{
        "ARGENTINA", "BOLIVIA", "BRASIL", "CHILE", "COLOMBIA", "CUBA", "ECUADOR", "SALVADOR",
        "GRANADA", "GUATEMALA", "GUYANA", "HAITI", "HONDURAS", "JAMAICA", "MEXICO", "NICARAGUA",
        "PARAGUAY", "PANAMA", "PERU", "SURINAM", "URUGUAY", "VENEZUELA"};

    private final String sistemasOperativos[] = new String[]{
        "WINDOWS", "MAC", "LINUX", "UNIX", "SOLARIS", "CHROME", "DEBIAN", "UBUNTU", "MANDRIVA",
        "SABAYON", "FEDORA", "REACTOS", "ANDROID", "IOS", "SYMBIAN", "BADA"};

    private final String frutas[] = new String[]{
        "ARANDANO", "FRAMBUESA", "FRESA", "MANDARINA", "NARANJA", "POMELO", "KIWI", "AGUACATE",
        "CHIRIMOYA", "COCO", "MANGO", "PAPAYA", "PIÑA", "ALBARICOQUE", "CEREZA", "CIRUELA",
        "MANZANA", "MELOCOTON", "NISPERO", "PERA", "UVA", "ALMENDRA", "AVELLANA", "CACAHUETE",
        "NUEZ", "PISTACHO"};

    private final String[] instrumentos = new String[]{
        "ARPA", "BAJO", "BALALAICA", "CASCABELES", "CASTAÑUELAS", "CHARANGO", "CITARA", "CLARINETE",
        "CLABEZIN", "CLAVICORDIO", "CONTRABAJO", "CORNO", "FAGOT", "FLAUTA", "GAITA", "GONG",
        "GUITARRA", "LAUD", "LIRA", "MARACAS", "OBOE", "OCARINA", "ORGANO", "PIANO", "PLATILLOS",
        "QUENA", "SAXOFON", "SITAR", "SONAJAS", "TRIANGULO", "TROMBON", "TROMPETA", "VIOLIN",
        "VIOLA", "VIOLONCELO", "XILOFONO"};

    private final String[] nombres = new String[]{
        "LORENA", "GUSTAVO", "CRISTIAN", "VALENTINA", "LUIS", "OMAR", "CAMILO", "OLGA",
        "MICHAEL", "PEDRO", ",MIGUEL", "DEISY", "YADIRA", "JULIANA", "OSCAR", "CARLOS",
        "ALBERTO", "MARTHA", "LUISA", "CESAR", "YORMAN"};

    private final AudioClip teclaIncorrecta = getAudio("/sonidos/incorrecto.wav"); //Audio reproducido al elegir la letra incorrecta
    private final AudioClip teclaCorrecta = getAudio("/sonidos/correcto.wav"); //Audio reproducido al elegir la letra correcta
    private AudioClip ganaste; //Guarda el sonido que se reproducirá al ganar el juego
    private AudioClip perdiste; //Guarda el sonido que se reproducirá al perder el juego

    //Vector que contiene dos sonidos que se generan aleatoriamente cuando se gana el juego
    private final String[] sonidoGana = new String[]{"gano1.wav", "gano2.wav"};

    //Vector que contiene dos sonidos que se generan aleatoriamente cuando se pierde el juego
    private final String[] sonidoPierde = new String[]{"perdio1.wav", "perdio2.wav", "perdio3.wav", "perdio4.wav", "perdio5.wav"};

    /**
     * Constructor por defecto
     */
    public Ahorcado() {
        inicializarComponentes();
    }

    /**
     * Crea el panel principal que agrega los tres paneles del juego. Usa
     * BorderLayout como Grid
     */
    private void inicializarComponentes() {
        setTitle("Ahorcado");
        add(getPanelOpciones(), BorderLayout.NORTH);
        add(getPanelLetras(), BorderLayout.CENTER);
        add(getPanelEstado(), BorderLayout.PAGE_END);

        this.setBackground(new Color(255, 255, 255));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.NORMAL);
        setPreferredSize(new Dimension(700, 600));
        pack();
        setVisible(true);
    }

    /**
     * Recupera el audio que se va a guardar en la variable
     *
     * @param sonido ruta en la que está ubicado el sonido a guardar
     * @return sonido manejable para java
     */
    public AudioClip getAudio(String sonido) {
        return Applet.newAudioClip(getClass().getResource(sonido));
    }

    /**
     * Si el sonido está activado lo desactiva y si está desactivado lo activa
     */
    private void cambiarEstadoSonido() {
        sonido = !sonido;
    }

    /**
     * Crea el panel opciones que permite elegir la categoría de palabras que se
     * va a adivinar
     *
     * @return panelOpciones devuelve el panel opciones que contiene la lista
     * elegible de categorias
     */
    private JPanel getPanelOpciones() {
        JPanel panelOpciones = new JPanel(); //Crea el panel opciones como una instancia de JPanel
        panelOpciones.setBorder(BorderFactory.createLineBorder(Color.BLACK)); //Añade un borde al panelOpciones

        JButton btnActivarSonido = new JButton("Desactivar Sonido"); //Crea un botón que activa o desactiva el sonido

        btnActivarSonido.setBackground(new Color(244, 244, 244));

        btnActivarSonido.addActionListener((ActionEvent ev) -> {
            cambiarEstadoSonido(); //Cada vez que se presiona el botón llama la función cambiarEstadoSonido

            if (sonido) {
                btnActivarSonido.setText("Desactivar Sonido");
            } else {
                btnActivarSonido.setText("Activar Sonido");
            }
        });

        panelOpciones.add(btnActivarSonido);

        panelOpciones.add(new JLabel("Categoría")); // Agrega una instancia anónima de JLabel

        String categorias[] = new String[]{ //Contiene las categorias que se eligen en el JComboBox
            "Paises latinoamericanos", "Sistemas operativos", "Frutas", "Instrumentos musicales", "Nombres"};

        pais.addAll(Arrays.asList(paises));

        fruta.addAll(Arrays.asList(frutas));

        sistemaOperativo.addAll(Arrays.asList(sistemasOperativos));

        instrumento.addAll(Arrays.asList(instrumentos));

        nombre.addAll(Arrays.asList(nombres));

        //Añade al ArrayList de ArrayList las colecciones de palabras de todas las categorias
        arrayPalabras.add(pais);
        arrayPalabras.add(sistemaOperativo);
        arrayPalabras.add(fruta);
        arrayPalabras.add(instrumento);
        arrayPalabras.add(nombre);

        cboCategorias = new JComboBox<>(categorias);//Añade las categorias al JComboBox

        cboCategorias.setBackground(new Color(244, 244, 244));

        panelOpciones.add(cboCategorias); //Se agrega a panelOpciones el objeto cboCategorias

        //Se agrega la instancia btnJugar de tipo JButton, usando el constructor para asignar el texto "Jugar"
        JButton btnJugar = new JButton("Jugar");
        //Se usa la notacion lambda para agregar un oyente de eventos a btnJugar
        btnJugar.addActionListener((ActionEvent evento) -> {
            inicializarEstado();
        });

        btnJugar.setBackground(new Color(244, 244, 244));
        panelOpciones.add(btnJugar); //se agrega a panel opciones el boton btnJugar

        panelOpciones.setBackground(new Color(255, 255, 255));

        return panelOpciones; //retore el panelOpciones
    }

    /**
     * Crea el panel que contiene los botones y los crea a cada uno con un array
     * de botones
     *
     * @return panelLetras devuelve un panel con los botones de las letras del
     * alfabeto
     */
    private JPanel getPanelLetras() {
        GridLayout layout = new GridLayout(3, 9, 10, 10);
        JPanel panelLetras = new JPanel(layout);
        String[] letra = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "Ñ", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"
        };//Contiene las letras del alfabeto que se van a asignar a los botones

        for (int i = 0; i < btnAlfabeto.length; i++) {

            btnAlfabeto[i] = new JButton(letra[i]); //en cada entrada agrega una letra del alfabeto a cada botón
            btnAlfabeto[i].setBackground(new Color(120, 31, 25));
            btnAlfabeto[i].setEnabled(false);

            btnAlfabeto[i].setForeground(Color.WHITE);

            btnAlfabeto[i].addActionListener((ActionEvent evento) -> {
                botonPulsado = (JButton) evento.getSource();
                evaluar();
            }); //Añade un evento a cada botón para detectar cuando este es pulsado

            panelLetras.add(btnAlfabeto[i]);
        }

        panelLetras.setBackground(new Color(255, 255, 255));
        panelLetras.setBorder(BorderFactory.createEmptyBorder());
        panelLetras.setPreferredSize(new Dimension(650, 300));

        return panelLetras; //Retorna el panel que contiene los botones con las letras del alfabeto
    }

    /**
     * Analizar si la letra que se seleccionó está contenida en la palabra que
     * se está adivinando
     */
    private void evaluar() {
        String letraElegida = botonPulsado.getText(); //Se guarda en letra elegida el texto del botón pulsado
        if (palabraSeleccionada.contains(letraElegida)) {//Si la letra que se eligió esta contenida en la palabra a adivinar...
            botonPulsado.setEnabled(false); //Se desabilita el botón pulsado
            boolean gana = true;
            if (sonido) {
                teclaCorrecta.play(); //Reproduce un sonido cuando la letra seleccionada est� en la palabra
            }

            for (int i = 0; i < palabraSeleccionada.length(); i++) {
                if (palabraSeleccionada.charAt(i) == letraElegida.charAt(0)) {
                    lblLetras[i].setText(letraElegida); //A�ade la letra seleccionada
                }
                if ("_".equals(lblLetras[i].getText())) {
                    gana = false;
                }
            }

            if (gana) {
                cambiarEstadoBotones(false);
                lblEstado.setText("¡Ganaste! :D");

                //Elige aleatoriamente uno de los dos sonidos especiales para el estado de ganar
                int idSonido = ThreadLocalRandom.current().nextInt(0, sonidoGana.length);
                String sonidoWin = "/sonidos/" + sonidoGana[idSonido]; //Contiene la ruta del audio que se va a reproducir
                ganaste = getAudio(sonidoWin); //Guarda el audio
                if (sonido) {
                    ganaste.play(); //Reproduce el audio
                }

            }
        } else {
            intentosFallidos++;
            botonPulsado.setEnabled(false); //Se desabilita el botón pulsado
            mostrarImagen(intentosFallidos);
            if (sonido) {
                teclaIncorrecta.play(); //Reproduce un sonido por haber elegido una letra incorrecta
            }
            cantidadIntentosFallidos.setText("   Intentos Fallidos:  " + intentosFallidos);

            if (intentosFallidos == 9) {
                cambiarEstadoBotones(false);
                lblEstado.setText("¡Perdiste!  :'(");

                for (int i = 0; i < palabraSeleccionada.length(); i++) {
                    lblLetras[i].setText("" + palabraSeleccionada.charAt(i)); //Muestra la palabra que no se logró adivinar
                }

                //Elige aleatoriamente uno de los cinco sonidos especiales para el estado de perder
                int idSonido = ThreadLocalRandom.current().nextInt(0, sonidoPierde.length);
                String sonidoLose = "/sonidos/" + sonidoPierde[idSonido];//Guarda la ruta del audio que se va a reproducir
                perdiste = getAudio(sonidoLose);
                if (sonido) {
                    perdiste.play(); //Reproduce un audio al perder
                }
            }
        }
    }

    /**
     * Muestra la imagen que contiene el estado del ahorcado
     *
     * @param idImagen Recibe la posición que contiene la imagen que se va a
     * mostrar
     */
    private void mostrarImagen(int idImagen) {
        URL url = this.getClass().getResource("/imagenes/" + imagenes[idImagen]);
        ImageIcon icono = new ImageIcon(url);
        lblImagenEstado.setIcon(icono);
    }

    /**
     * Crea y agrega tres paneles al panel estado. 1. contiene la etiqueta
     * (perdiste/ganaste). 2. modifica la imagen del ahorcado segun el estado
     * del juego 3. agrega las letras de la palabra que se esta adivinando
     *
     * @return panelEstado devuelve el panel que contiene la imagen y el estado
     * del juego
     */
    private JPanel getPanelEstado() {
        JPanel panelEstado = new JPanel(new BorderLayout(5, 5)); // Por defecto center
        panelEstado.setBorder(BorderFactory.createEtchedBorder());

        lblEstado = new JLabel(" "); // <-- es un espacio no una cadena nula
        lblEstado.setFont(new Font("Tahoma", 1, 22));
        lblEstado.setForeground(new Color(255, 51, 0)); // <-- Establece el color de fuente de la letra
        lblEstado.setHorizontalAlignment(JLabel.CENTER);

        panelEstado.add(lblEstado, BorderLayout.NORTH); //se agrega la etiqueta lblEstado a la parte norte del contenedor

        lblImagenEstado = new JLabel(); //Inicializa la etiqueta que va a guardar la imagen del ahorcado
        lblImagenEstado.setHorizontalAlignment(JLabel.CENTER);

        panelEstado.add(lblImagenEstado, BorderLayout.CENTER); //se agrega la etiqueta lblEstado a la parte norte del contenedor

        mostrarImagen(intentosFallidos); //puede ser una imagen inicial distinta

        cantidadIntentosFallidos = new JLabel("   Intentos Fallidos:  " + intentosFallidos);
        cantidadIntentosFallidos.setFont(new Font("Tahoma", 1, 25));
        panelEstado.add(cantidadIntentosFallidos, BorderLayout.WEST);

        JPanel panelPalabra = new JPanel();
        for (int i = 0; i < lblLetras.length; i++) {
            lblLetras[i] = new JLabel(" ");
            lblLetras[i].setFont(new Font("Tahoma", 1, 35));
            panelPalabra.add(lblLetras[i]);
        }
        panelEstado.add(panelPalabra, BorderLayout.SOUTH); //se agrega panelPalabra a panelEstado en la parte sur del contenedor

        panelEstado.setBackground(new Color(255, 255, 255));
        panelPalabra.setBackground(new Color(255, 255, 255));
        panelEstado.setBorder(BorderFactory.createEmptyBorder());

        //Devuelve el panel que contiene la imagen del ahorcado, el panel de las letras y el del estado de perder o ganar
        return panelEstado;
    }

    /**
     * Activa o desactiva los botones del panelLetras
     *
     * @param estado Recibe un booleano usado para activar o desactivar los
     * botones
     */
    private void cambiarEstadoBotones(boolean estado) {

        for (JButton boton : btnAlfabeto) {
            boton.setEnabled(estado);
        }
    }

    /**
     * Inicia el juego al pulsar el boton jugar, activa los botones y agrega
     * cantidad de guiones proporcional a la cantidad de letras de la palabra a
     * adivinar
     */
    private void inicializarEstado() {
        lblEstado.setText(" ");
        cambiarEstadoBotones(true);

        for (JLabel lblLetra : lblLetras) {
            lblLetra.setText("_");
            lblLetra.setVisible(false);
            lblLetra.setForeground(Color.BLACK);
        }

        intentosFallidos = 0;
        cantidadIntentosFallidos.setText("   Intentos Fallidos:  " + intentosFallidos);
        mostrarImagen(intentosFallidos); //Muestra la primera imagen del ahorcado
        int idCategoria = cboCategorias.getSelectedIndex(); //Toma la categoria elegida para selecionar aleatoriamente una palabra
        int idPalabra = ThreadLocalRandom.current().nextInt(0, arrayPalabras.get(idCategoria).size());//Selecciona una palabra aleatoriamente
        palabraSeleccionada = arrayPalabras.get(idCategoria).get(idPalabra); //Guarda la palabra seleccionada

        for (int i = 0; i < palabraSeleccionada.length(); i++) {
            lblLetras[i].setVisible(true); //Deja visible solo la cantidad de guiones proporcional a la cantidad de letras de la palabra
        }
    }

    public static void main(String[] args) {

        //Ejemplo de como capturar una tecla pulsada a nivel global
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher((KeyEvent e) -> {

            //Obviar la gestion del evento cuando se suelta la tecla
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                return false;
            }
            //forzar un clic cuando se pulse una tecla imprimible...
            if ((e.getKeyCode() != 0) && (e.getKeyChar() != '\uFFFF')) {
                //se garantiza que la tecla pulsada está en mayuscula para buscar
                //dicha letra en el array de botones del alfabeto y forzar un clic sobre el
                String letra = ("" + e.getKeyChar()).toUpperCase();
                for (JButton btnLetra : btnAlfabeto) {
                    if (btnLetra.getText().equals(letra)) {
                        btnLetra.doClick();
                    }
                }
            }
            return false;
        });
        EventQueue.invokeLater(() -> {
            new Ahorcado().setVisible(true);
        });
    }
}
