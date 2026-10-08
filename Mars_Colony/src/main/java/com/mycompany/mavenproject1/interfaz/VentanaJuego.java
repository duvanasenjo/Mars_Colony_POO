package com.mycompany.mavenproject1.interfaz;

import com.mycompany.mavenproject1.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * PROGRAMA 1: la ventana del juego (JFrame Form de NetBeans).
 *
 * Las pantallas están DISEÑADAS en la pestaña Design: son 4 paneles
 * (panelInicio, panelNueva, panelCargar, panelPreparacion) puestos uno
 * encima del otro con CardLayout, que muestra uno a la vez, como un
 * mazo de cartas. Cada botón tiene su método xxxActionPerformed, que
 * NetBeans crea al hacer doble clic en el botón en Design.
 *
 * Lo único que se crea por código son las casillas de los dos mapas
 * (preparación y batalla; no se pueden arrastrar una por una). Esas
 * casillas usan ActionCommand: llevan "CASILLA " o "BATALLA " + número
 * y esta clase las escucha con su propio actionPerformed. El Timer
 * de la batalla también llega ahí, con el comando "TIC".
 *
 * Las partes grises que dicen "Generated Code" las escribe NetBeans
 * a partir de Design: NO se editan a mano.
 *
 * @author dylnr
 */
public class VentanaJuego extends javax.swing.JFrame implements ActionListener {

    public static final int TAMAÑO_CASILLA = 26;   // píxeles de cada botón del mapa
    public static final String CASILLA = "CASILLA ";   // casillas del mapa de preparación
    public static final String BATALLA = "BATALLA ";   // casillas del mapa de batalla
    public static final String TIC = "TIC";            // cada "tic" del reloj de la batalla
    public static final int REFRESCO = 200;            // milisegundos entre refrescos de pantalla

    // ---------- Datos ----------
    private Partida partida;
    private ArchivoPartidas archivoPartidas;
    private ArchivoCatalogo archivoCatalogo;
    private JButton[][] casillas;            // los 625 botones del mapa de preparación
    private JButton[][] casillasBatalla;     // los 625 botones del mapa de batalla
    private ArrayList<Defensa> plantillas;   // misma posición que en cmbDefensas
    private Timer reloj;                     // refresca la pantalla durante la batalla
    private UnidadCombate unidadSeleccionada;  // la que se muestra en el registro (null = ninguna)
    // Íconos ya creados (misma posición en las dos listas), para no reducir la imagen cada vez
    private ArrayList<String> rutasIconos;
    private ArrayList<ImageIcon> iconos;

    public VentanaJuego() {
        initComponents();   // arma todo lo diseñado en Design (lo escribe NetBeans)
        this.partida = null;
        this.archivoPartidas = new ArchivoPartidas();
        this.archivoCatalogo = new ArchivoCatalogo();
        this.plantillas = new ArrayList<Defensa>();
        this.unidadSeleccionada = null;
        this.rutasIconos = new ArrayList<String>();
        this.iconos = new ArrayList<ImageIcon>();
        // El reloj llama a actionPerformed (de esta clase) cada REFRESCO milisegundos
        this.reloj = new Timer(REFRESCO, this);
        this.reloj.setActionCommand(TIC);
        crearCasillas();
        mostrarInicio();
    }

    public static void main(String[] args) {
        VentanaJuego ventana = new VentanaJuego();
        ventana.setVisible(true);
    }

    // =====================================================================
    //  CAMBIAR DE PANTALLA
    // =====================================================================

    // CardLayout muestra solo una de las "cartas" (paneles) a la vez.
    private void mostrarPantalla(String nombre) {
        CardLayout cartas = (CardLayout) getContentPane().getLayout();
        cartas.show(getContentPane(), nombre);
    }

    public void mostrarInicio() {
        lblMensajeInicio.setText("");
        mostrarPantalla("inicio");
    }

    public void mostrarNuevaPartida() {
        txtNombre.setText("");
        lblMensajeNueva.setText("");
        mostrarPantalla("nueva");
        txtNombre.requestFocusInWindow();   // el cursor queda listo para escribir
    }

    public void mostrarCargarPartida() {
        cmbPartidas.removeAllItems();
        ArrayList<String> nombres = archivoPartidas.listar();
        for (String nombre : nombres) {
            cmbPartidas.addItem(nombre);
        }
        lblMensajeCargar.setText("");
        btnAbrir.setEnabled(nombres.size() > 0);
        if (nombres.size() == 0) {
            lblMensajeCargar.setText("Todavía no hay partidas guardadas.");
        }
        mostrarPantalla("cargar");
    }

    public void mostrarPreparacion() {
        lblComandante.setText("Comandante: " + partida.getNombreComandante());
        lblMision.setText(textoMision(partida));
        lblMensajePreparacion.setText("");

        // Primero la lista de plantillas y después el combo, en el mismo orden
        plantillas = partida.defensasDisponibles();
        cmbDefensas.removeAllItems();
        for (Defensa plantilla : plantillas) {
            cmbDefensas.addItem(plantilla.getNombre() + "  (costo " + plantilla.getCosto() + ")");
        }
        actualizarInfoDefensa();
        actualizarPreparacion();
        mostrarPantalla("preparacion");
    }

    // =====================================================================
    //  EVENTOS DE LOS BOTONES DISEÑADOS (los crea NetBeans con doble clic)
    // =====================================================================

    private void btnNuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaActionPerformed
        mostrarNuevaPartida();
    }//GEN-LAST:event_btnNuevaActionPerformed

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
        mostrarCargarPartida();
    }//GEN-LAST:event_btnCargarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();   // cierra la ventana y termina el programa
    }//GEN-LAST:event_btnSalirActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        crearPartida();   // Enter dentro del campo = Comenzar
    }//GEN-LAST:event_txtNombreActionPerformed

    private void btnComenzarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComenzarActionPerformed
        crearPartida();
    }//GEN-LAST:event_btnComenzarActionPerformed

    private void btnVolverNuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverNuevaActionPerformed
        mostrarInicio();
    }//GEN-LAST:event_btnVolverNuevaActionPerformed

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        abrirPartida();
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnVolverCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverCargarActionPerformed
        mostrarInicio();
    }//GEN-LAST:event_btnVolverCargarActionPerformed

    private void cmbDefensasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDefensasActionPerformed
        actualizarInfoDefensa();   // se eligió otra defensa en la lista
    }//GEN-LAST:event_cmbDefensasActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        iniciarBatalla();
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirPreparacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirPreparacionActionPerformed
        if (archivoPartidas.guardar(partida)) {
            mostrarInicio();
            lblMensajeInicio.setText("Partida guardada.");
        } else {
            lblMensajePreparacion.setText("No se pudo guardar la partida.");
        }
    }//GEN-LAST:event_btnSalirPreparacionActionPerformed

    private void btnVerResultadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerResultadoActionPerformed
        mostrarResultado();
    }//GEN-LAST:event_btnVerResultadoActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        mostrarPreparacion();   // el perímetro ya se reconstruyó en mostrarResultado
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnRepetirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepetirActionPerformed
        mostrarPreparacion();   // misma misión, mismo perímetro, vida llena
    }//GEN-LAST:event_btnRepetirActionPerformed

    private void btnTerminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerminarActionPerformed
        mostrarInicio();
        lblMensajeInicio.setText("Campaña guardada. Puedes continuarla con Cargar partida.");
    }//GEN-LAST:event_btnTerminarActionPerformed

    // =====================================================================
    //  CASILLAS DEL MAPA (creadas por código)
    // =====================================================================

    // Crea los botones de los dos mapas una sola vez.
    private void crearCasillas() {
        casillas = crearMapaDeBotones(panelMapa, CASILLA);
        casillasBatalla = crearMapaDeBotones(panelMapaBatalla, BATALLA);
    }

    // 625 botones dentro de un panel con GridLayout 25 x 25.
    private JButton[][] crearMapaDeBotones(JPanel panel, String prefijo) {
        JButton[][] botones = new JButton[Mapa.TAMAÑO][Mapa.TAMAÑO];
        for (int fila = 0; fila < Mapa.TAMAÑO; fila++) {
            for (int columna = 0; columna < Mapa.TAMAÑO; columna++) {
                JButton casilla = new JButton();
                casilla.setMargin(new Insets(0, 0, 0, 0));   // sin espacio interno, para que quepa la imagen
                // Cada casilla lleva su número: fila * 25 + columna
                casilla.setActionCommand(prefijo + (fila * Mapa.TAMAÑO + columna));
                casilla.addActionListener(this);   // "this" = esta ventana escucha el clic
                botones[fila][columna] = casilla;
                panel.add(casilla);
            }
        }
        return botones;
    }

    // Aquí llegan los clics de las casillas de los dos mapas y los "tic" del reloj.
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if (comando.equals(TIC)) {
            refrescarBatalla();
        } else if (comando.startsWith(CASILLA)) {
            // "CASILLA 137" -> 137 -> fila 137 / 25 = 5, columna 137 % 25 = 12
            int numero = Integer.parseInt(comando.substring(CASILLA.length()));
            clicEnCasilla(numero / Mapa.TAMAÑO, numero % Mapa.TAMAÑO);
        } else if (comando.startsWith(BATALLA)) {
            int numero = Integer.parseInt(comando.substring(BATALLA.length()));
            seleccionarUnidad(numero / Mapa.TAMAÑO, numero % Mapa.TAMAÑO);
        }
    }

    // =====================================================================
    //  ACCIONES
    // =====================================================================

    private void crearPartida() {
        String nombre = txtNombre.getText().trim();   // trim quita espacios al inicio y al final
        if (nombre.equals("")) {
            lblMensajeNueva.setText("Escribe el nombre del comandante.");
            return;
        }
        if (archivoPartidas.existeComandante(nombre)) {
            lblMensajeNueva.setText("Ya existe una partida con ese nombre.");
            return;
        }
        Catalogo catalogo = archivoCatalogo.cargar();
        if (catalogo == null) {
            lblMensajeNueva.setText("No se encontró catalogo.dat: ejecuta GeneradorDatosPrueba.");
            return;
        }
        Partida nueva = new Partida(nombre);
        nueva.cargarPlantillas(catalogo);
        nueva.prepararBatalla();
        archivoPartidas.guardar(nueva);
        partida = nueva;
        mostrarPreparacion();
    }

    private void abrirPartida() {
        String nombre = (String) cmbPartidas.getSelectedItem();
        if (nombre == null) {
            return;
        }
        Partida cargada = archivoPartidas.cargar(nombre);
        if (cargada == null) {
            lblMensajeCargar.setText("No se pudo cargar la partida.");
            return;
        }
        if (cargada.getBatallaActual() == null) {
            cargada.prepararBatalla();
        }
        partida = cargada;
        mostrarPreparacion();
    }

    private void guardar() {
        if (archivoPartidas.guardar(partida)) {
            lblMensajePreparacion.setText("Partida guardada.");
        } else {
            lblMensajePreparacion.setText("No se pudo guardar.");
        }
    }

    // Defensa elegida en la lista (null si la lista está vacía).
    private Defensa defensaElegida() {
        int indice = cmbDefensas.getSelectedIndex();
        if (indice < 0 || indice >= plantillas.size()) {
            return null;
        }
        return plantillas.get(indice);
    }

    private void clicEnCasilla(int fila, int columna) {
        Batalla batalla = partida.getBatallaActual();
        Mapa mapa = batalla.getMapa();
        UnidadCombate unidad = mapa.obtener(fila, columna);
        Defensa elegida = defensaElegida();

        if (unidad == mapa.getNucleo()) {
            lblMensajePreparacion.setText("Ese es el núcleo.");
        } else if (unidad != null) {
            // Buscar qué defensa de la batalla es la que está en esa casilla
            for (Defensa defensa : batalla.getDefensas()) {
                if (defensa == unidad) {
                    batalla.quitarDefensa(defensa);
                    lblMensajePreparacion.setText("Quitaste " + defensa.getNombre() + ".");
                }
            }
        } else if (mapa.esBorde(fila, columna)) {
            lblMensajePreparacion.setText("En el borde no se puede construir.");
        } else if (elegida == null) {
            lblMensajePreparacion.setText("No hay defensas disponibles.");
        } else if (elegida.getCosto() > partida.capacidadRestante(batalla)) {
            lblMensajePreparacion.setText("No alcanza la capacidad.");
        } else if (partida.colocarDefensa(batalla, elegida.copiar(), fila, columna)) {
            // Se coloca una COPIA: la plantilla sigue en la lista para usarla otra vez
            lblMensajePreparacion.setText("Colocaste " + elegida.getNombre() + " en (" + fila + ", " + columna + ").");
        } else {
            lblMensajePreparacion.setText("No se pudo colocar ahí.");
        }
        actualizarPreparacion();
    }

    private void actualizarInfoDefensa() {
        Defensa elegida = defensaElegida();
        if (elegida == null) {
            lblInfoDefensa.setText("No hay defensas disponibles en esta misión.");
            return;
        }
        lblInfoDefensa.setText("Vida " + elegida.getVidaMaxima() + "  -  Daño " + elegida.getDaño()
                + "  -  Costo " + elegida.getCosto());
    }

    // Vuelve a pintar las 625 casillas y la barra de capacidad.
    private void actualizarPreparacion() {
        Batalla batalla = partida.getBatallaActual();
        Mapa mapa = batalla.getMapa();
        for (int fila = 0; fila < Mapa.TAMAÑO; fila++) {
            for (int columna = 0; columna < Mapa.TAMAÑO; columna++) {
                JButton casilla = casillas[fila][columna];
                UnidadCombate unidad = mapa.obtener(fila, columna);
                if (unidad == null) {
                    casilla.setIcon(null);
                    casilla.setToolTipText(null);
                } else {
                    casilla.setIcon(icono(unidad.getRutaImagenNormal()));
                    casilla.setToolTipText(unidad.getNombre());   // nombre al dejar el mouse encima
                }
                if (mapa.esBorde(fila, columna)) {
                    casilla.setBackground(new Color(255, 205, 205));   // rosado
                } else {
                    casilla.setBackground(Color.WHITE);
                }
            }
        }
        barraCapacidad.setMaximum(partida.getCapacidadTotal());
        barraCapacidad.setValue(partida.capacidadUsada(batalla));
        barraCapacidad.setString(partida.capacidadUsada(batalla) + " / " + partida.getCapacidadTotal());
    }

    // =====================================================================
    //  BATALLA
    // =====================================================================

    // Botón "Iniciar batalla": anota el perímetro, crea el ejército enemigo
    // en el borde y arranca los hilos y el reloj de la pantalla.
    private void iniciarBatalla() {
        Batalla batalla = partida.getBatallaActual();
        if (batalla.isEnCurso()) {
            return;
        }
        partida.guardarPerimetro();
        int enemigos = partida.generarEjercito(batalla);
        if (enemigos == 0) {
            lblMensajePreparacion.setText("No se pudo generar el ejército enemigo.");
            return;
        }
        unidadSeleccionada = null;
        txtRegistro.setText("");
        lblUnidad.setText("Haz clic en una unidad del mapa para ver su registro.");
        lblTituloBatalla.setText("BATALLA - " + textoMision(partida));
        lblMensajeBatalla.setText("Llegan " + enemigos + " criaturas...");
        btnVerResultado.setEnabled(false);
        refrescarBatalla();
        mostrarPantalla("batalla");
        batalla.iniciar();   // arranca un hilo por unidad + el hilo del mapa
        reloj.start();       // la pantalla se refresca sola cada REFRESCO ms
    }

    // Lo llama el reloj: vuelve a pintar el mapa y los datos de la batalla.
    private void refrescarBatalla() {
        Batalla batalla = partida.getBatallaActual();
        Mapa mapa = batalla.getMapa();
        for (int fila = 0; fila < Mapa.TAMAÑO; fila++) {
            for (int columna = 0; columna < Mapa.TAMAÑO; columna++) {
                pintarCasillaBatalla(casillasBatalla[fila][columna], mapa, fila, columna);
            }
        }
        Nucleo nucleo = mapa.getNucleo();
        barraNucleo.setMaximum(nucleo.getVidaMaxima());
        barraNucleo.setValue(nucleo.getVidaActual());
        barraNucleo.setString(nucleo.getVidaActual() + " / " + nucleo.getVidaMaxima());
        lblTiempo.setText("Tiempo: " + batalla.getSegundos() + " s  (límite " + Batalla.LIMITE_SEGUNDOS + " s)");

        int criaturasVivas = 0;
        for (Criatura criatura : batalla.getCriaturas()) {
            if (criatura.estaViva()) {
                criaturasVivas = criaturasVivas + 1;
            }
        }
        int defensasVivas = 0;
        for (Defensa defensa : batalla.getDefensas()) {
            if (defensa.estaViva()) {
                defensasVivas = defensasVivas + 1;
            }
        }
        lblCriaturasVivas.setText("Criaturas vivas: " + criaturasVivas + " de " + batalla.getCriaturas().size());
        lblDefensasVivas.setText("Defensas vivas: " + defensasVivas + " de " + batalla.getDefensas().size());
        actualizarRegistro();

        if (reloj.isRunning() && !batalla.isEnCurso()) {
            reloj.stop();   // la batalla terminó: los hilos ya se apagaron solos
            if (batalla.ganaronLasDefensas()) {
                lblMensajeBatalla.setText("¡Ganaron las defensas! El núcleo sigue en pie.");
            } else {
                lblMensajeBatalla.setText("El núcleo fue destruido.");
            }
            btnVerResultado.setEnabled(true);
        }
    }

    private void pintarCasillaBatalla(JButton casilla, Mapa mapa, int fila, int columna) {
        UnidadCombate unidad = mapa.obtener(fila, columna);
        if (unidad == null) {
            casilla.setIcon(null);
            casilla.setToolTipText(null);
            if (mapa.esBorde(fila, columna)) {
                casilla.setBackground(new Color(255, 205, 205));   // rosado: borde
            } else {
                casilla.setBackground(Color.WHITE);
            }
            return;
        }
        casilla.setIcon(icono(unidad.getRutaImagenNormal()));
        casilla.setToolTipText(unidad.getNombre() + " - vida " + unidad.getVidaActual() + "/" + unidad.getVidaMaxima());
        // Color de fondo según el bando (lo responde la propia unidad: polimorfismo)
        if (unidad == unidadSeleccionada) {
            casilla.setBackground(Color.YELLOW);                // la que se está viendo
        } else if (unidad == mapa.getNucleo()) {
            casilla.setBackground(new Color(200, 245, 200));   // verde: núcleo
        } else if (unidad.esCriatura()) {
            casilla.setBackground(new Color(255, 215, 170));   // naranja: criatura
        } else {
            casilla.setBackground(new Color(205, 225, 255));   // celeste: defensa
        }
    }

    // Clic en una casilla del mapa de batalla: muestra el registro de esa unidad.
    private void seleccionarUnidad(int fila, int columna) {
        UnidadCombate unidad = partida.getBatallaActual().getMapa().obtener(fila, columna);
        if (unidad != null) {
            unidadSeleccionada = unidad;
            lblUnidad.setText("Registro de " + unidad.getNombre() + ":");
            actualizarRegistro();
        }
    }

    // Escribe en txtRegistro la información y el registro de la unidad elegida.
    private void actualizarRegistro() {
        if (unidadSeleccionada == null) {
            return;
        }
        String texto = textoRegistro(unidadSeleccionada);
        if (!texto.equals(txtRegistro.getText())) {   // solo si cambió (para no mover la barra)
            txtRegistro.setText(texto);
        }
    }

    private String textoRegistro(UnidadCombate unidad) {
        String texto = unidad.getNombre();
        if (!unidad.estaViva()) {
            texto = texto + "  (destruida)";
        }
        texto = texto + "\nVida: " + unidad.getVidaActual() + " / " + unidad.getVidaMaxima()
                + "    Daño: " + unidad.getDaño() + "\n\nAtacó a:\n";
        // El hilo del mapa puede estar agregando entradas en ese mismo instante;
        // si justo choca, se deja el texto anterior y se intenta en el próximo tic.
        try {
            ArrayList<EntradaRegistro> atacados = unidad.getRegistro().getObjetivosAtacados();
            if (atacados.size() == 0) {
                texto = texto + "  (nadie)\n";
            }
            for (EntradaRegistro entrada : atacados) {
                texto = texto + "  - " + entrada + "\n";
            }
            texto = texto + "\nRecibió ataques de:\n";
            ArrayList<EntradaRegistro> recibidos = unidad.getRegistro().getAtacantesRecibidos();
            if (recibidos.size() == 0) {
                texto = texto + "  (nadie)\n";
            }
            for (EntradaRegistro entrada : recibidos) {
                texto = texto + "  - " + entrada + "\n";
            }
        } catch (Exception e) {
            return txtRegistro.getText();
        }
        return texto;
    }

    // =====================================================================
    //  RESULTADO
    // =====================================================================

    // Botón "Ver resultado". Muestra el resumen de la batalla y deja la
    // partida lista para lo que sigue:
    //  - si ganó: supera la misión (+capacidad y crecimiento al azar)
    //  - en los dos casos: reconstruye el perímetro y guarda
    // Así "Siguiente misión" y "Repetir misión" solo vuelven a la preparación.
    public void mostrarResultado() {
        btnVerResultado.setEnabled(false);   // para que no se pueda hacer dos veces
        Batalla batalla = partida.getBatallaActual();
        boolean gano = batalla.ganaronLasDefensas();
        int misionJugada = partida.getMisionActual();

        // ----- Resumen (se calcula ANTES de reconstruir el perímetro) -----
        int criaturasMuertas = 0;
        for (Criatura criatura : batalla.getCriaturas()) {
            if (!criatura.estaViva()) {
                criaturasMuertas = criaturasMuertas + 1;
            }
        }
        int defensasPerdidas = 0;
        for (Defensa defensa : batalla.getDefensas()) {
            if (!defensa.estaViva()) {
                defensasPerdidas = defensasPerdidas + 1;
            }
        }
        Nucleo nucleo = batalla.getMapa().getNucleo();
        lblResumenMision.setText("Misión jugada: " + misionJugada);
        String duracion = "Duración: " + batalla.getSegundos() + " segundos";
        if (batalla.isTiempoAgotado()) {
            duracion = duracion + " (se acabó el tiempo y el núcleo resistió)";
        }
        lblResumenTiempo.setText(duracion);
        lblResumenCriaturas.setText("Criaturas eliminadas: " + criaturasMuertas + " de " + batalla.getCriaturas().size());
        lblResumenDefensas.setText("Defensas perdidas: " + defensasPerdidas + " de " + batalla.getDefensas().size());
        lblResumenNucleo.setText("Vida final del núcleo: " + nucleo.getVidaActual() + " / " + nucleo.getVidaMaxima());
        txtRegistroCompleto.setText(textoRegistroCompleto(batalla));
        txtRegistroCompleto.setCaretPosition(0);   // que el texto empiece arriba

        // ----- Lo que sigue según el resultado -----
        if (gano) {
            partida.superarMision();   // +5 de capacidad y crecimiento de 5 % a 20 %
            if (misionJugada == Partida.MISIONES_INICIALES) {
                lblResultado.setText("¡Completaste las " + Partida.MISIONES_INICIALES + " misiones!");
                btnSiguiente.setText("Continuar con misión extra");
            } else {
                lblResultado.setText("¡Misión superada!");
                btnSiguiente.setText("Siguiente misión");
            }
            lblResumenCapacidad.setText("Capacidad: ahora tienes " + partida.getCapacidadTotal()
                    + " puntos (+" + Partida.CAPACIDAD_POR_MISION + ")");
            txtCrecimiento.setText(textoCrecimiento(partida.getMisionActual()));
        } else {
            lblResultado.setText("El núcleo fue destruido");
            lblResumenCapacidad.setText("Capacidad: sigue en " + partida.getCapacidadTotal() + " puntos");
            txtCrecimiento.setText("No hubo crecimiento: la misión se repite con las mismas unidades.");
        }
        txtCrecimiento.setCaretPosition(0);
        btnSiguiente.setVisible(gano);
        btnRepetir.setVisible(!gano);

        partida.reconstruirPerimetro();   // mismo perímetro, unidades nuevas con vida llena
        archivoPartidas.guardar(partida);
        mostrarPantalla("resultado");
    }

    // Una línea por plantilla con lo que creció al pasar a la misión nueva
    // (es la última línea de su historial de mejoras).
    private String textoCrecimiento(int misionNueva) {
        String texto = "Al pasar a la misión " + misionNueva + ":\n\nTus defensas:\n";
        for (Defensa tipo : partida.getTiposDefensas()) {
            texto = texto + "  " + tipo.getNombre() + " - " + ultimaMejora(tipo) + "\n";
        }
        texto = texto + "\nLas criaturas:\n";
        for (Criatura tipo : partida.getTiposCriaturas()) {
            texto = texto + "  " + tipo.getNombre() + " - " + ultimaMejora(tipo) + "\n";
        }
        return texto;
    }

    private String ultimaMejora(UnidadCombate tipo) {
        ArrayList<String> historial = tipo.getHistorialMejoras();
        if (historial.size() == 0) {
            return "sin cambios";
        }
        return historial.get(historial.size() - 1);
    }

    // Registro de TODAS las unidades de la batalla (defensas y criaturas).
    private String textoRegistroCompleto(Batalla batalla) {
        String texto = "=== DEFENSAS ===\n\n";
        for (Defensa defensa : batalla.getDefensas()) {
            texto = texto + textoRegistro(defensa) + "\n";
        }
        texto = texto + "=== CRIATURAS ===\n\n";
        for (Criatura criatura : batalla.getCriaturas()) {
            texto = texto + textoRegistro(criatura) + "\n";
        }
        return texto;
    }

    // =====================================================================
    //  AYUDANTES
    // =====================================================================

    // Imagen de la unidad reducida al tamaño de la casilla (null si no existe).
    // Se guarda la primera vez para no reducirla otra vez en cada refresco.
    private ImageIcon icono(String ruta) {
        if (ruta == null) {
            return null;
        }
        for (int i = 0; i < rutasIconos.size(); i++) {
            if (rutasIconos.get(i).equals(ruta)) {
                return iconos.get(i);
            }
        }
        if (!new File(ruta).exists()) {
            return null;
        }
        Image original = new ImageIcon(ruta).getImage();
        Image pequeña = original.getScaledInstance(TAMAÑO_CASILLA - 2, TAMAÑO_CASILLA - 2, Image.SCALE_DEFAULT);
        ImageIcon icono = new ImageIcon(pequeña);
        rutasIconos.add(ruta);
        iconos.add(icono);
        return icono;
    }

    private String textoMision(Partida p) {
        if (p.getMisionActual() > Partida.MISIONES_INICIALES) {
            return "Misión " + p.getMisionActual() + " (misión extra)";
        }
        return "Misión " + p.getMisionActual() + " de " + Partida.MISIONES_INICIALES;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelInicio = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        btnNueva = new javax.swing.JButton();
        btnCargar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        lblMensajeInicio = new javax.swing.JLabel();
        panelNueva = new javax.swing.JPanel();
        lblTituloNueva = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        btnComenzar = new javax.swing.JButton();
        btnVolverNueva = new javax.swing.JButton();
        lblMensajeNueva = new javax.swing.JLabel();
        panelCargar = new javax.swing.JPanel();
        lblTituloCargar = new javax.swing.JLabel();
        lblPartidas = new javax.swing.JLabel();
        cmbPartidas = new javax.swing.JComboBox<>();
        btnAbrir = new javax.swing.JButton();
        btnVolverCargar = new javax.swing.JButton();
        lblMensajeCargar = new javax.swing.JLabel();
        panelPreparacion = new javax.swing.JPanel();
        panelMapa = new javax.swing.JPanel();
        lblComandante = new javax.swing.JLabel();
        lblMision = new javax.swing.JLabel();
        lblCapacidad = new javax.swing.JLabel();
        barraCapacidad = new javax.swing.JProgressBar();
        lblDefensa = new javax.swing.JLabel();
        cmbDefensas = new javax.swing.JComboBox<>();
        lblInfoDefensa = new javax.swing.JLabel();
        lblAyuda1 = new javax.swing.JLabel();
        lblAyuda2 = new javax.swing.JLabel();
        lblAyuda3 = new javax.swing.JLabel();
        btnIniciar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnSalirPreparacion = new javax.swing.JButton();
        lblMensajePreparacion = new javax.swing.JLabel();
        panelBatalla = new javax.swing.JPanel();
        panelMapaBatalla = new javax.swing.JPanel();
        lblTituloBatalla = new javax.swing.JLabel();
        lblNucleo = new javax.swing.JLabel();
        barraNucleo = new javax.swing.JProgressBar();
        lblTiempo = new javax.swing.JLabel();
        lblCriaturasVivas = new javax.swing.JLabel();
        lblDefensasVivas = new javax.swing.JLabel();
        lblUnidad = new javax.swing.JLabel();
        scrollRegistro = new javax.swing.JScrollPane();
        txtRegistro = new javax.swing.JTextArea();
        btnVerResultado = new javax.swing.JButton();
        lblMensajeBatalla = new javax.swing.JLabel();
        panelResultado = new javax.swing.JPanel();
        lblResultado = new javax.swing.JLabel();
        lblResumenMision = new javax.swing.JLabel();
        lblResumenTiempo = new javax.swing.JLabel();
        lblResumenCriaturas = new javax.swing.JLabel();
        lblResumenDefensas = new javax.swing.JLabel();
        lblResumenNucleo = new javax.swing.JLabel();
        lblResumenCapacidad = new javax.swing.JLabel();
        lblCrecimiento = new javax.swing.JLabel();
        scrollCrecimiento = new javax.swing.JScrollPane();
        txtCrecimiento = new javax.swing.JTextArea();
        lblRegistroCompleto = new javax.swing.JLabel();
        scrollRegistroCompleto = new javax.swing.JScrollPane();
        txtRegistroCompleto = new javax.swing.JTextArea();
        btnSiguiente = new javax.swing.JButton();
        btnRepetir = new javax.swing.JButton();
        btnTerminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Colonia Marte");
        setResizable(false);
        getContentPane().setLayout(new java.awt.CardLayout());

        panelInicio.setPreferredSize(new java.awt.Dimension(1100, 690));
        panelInicio.setLayout(null);

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 36)); // NOI18N
        lblTitulo.setText("COLONIA MARTE");
        panelInicio.add(lblTitulo);
        lblTitulo.setBounds(60, 60, 400, 50);

        lblSubtitulo.setText("Defensa del núcleo de oxígeno - Año 2187");
        panelInicio.add(lblSubtitulo);
        lblSubtitulo.setBounds(62, 110, 400, 22);

        btnNueva.setText("Nueva partida");
        btnNueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaActionPerformed(evt);
            }
        });
        panelInicio.add(btnNueva);
        btnNueva.setBounds(60, 170, 220, 40);

        btnCargar.setText("Cargar partida");
        btnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarActionPerformed(evt);
            }
        });
        panelInicio.add(btnCargar);
        btnCargar.setBounds(60, 220, 220, 40);

        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        panelInicio.add(btnSalir);
        btnSalir.setBounds(60, 270, 220, 40);

        panelInicio.add(lblMensajeInicio);
        lblMensajeInicio.setBounds(60, 330, 600, 22);

        getContentPane().add(panelInicio, "inicio");

        panelNueva.setPreferredSize(new java.awt.Dimension(1100, 690));
        panelNueva.setLayout(null);

        lblTituloNueva.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblTituloNueva.setText("NUEVA PARTIDA");
        panelNueva.add(lblTituloNueva);
        lblTituloNueva.setBounds(60, 40, 300, 22);

        lblNombre.setText("Nombre del comandante:");
        panelNueva.add(lblNombre);
        lblNombre.setBounds(60, 90, 300, 22);

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });
        panelNueva.add(txtNombre);
        txtNombre.setBounds(60, 115, 300, 30);

        btnComenzar.setText("Comenzar");
        btnComenzar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComenzarActionPerformed(evt);
            }
        });
        panelNueva.add(btnComenzar);
        btnComenzar.setBounds(60, 165, 140, 35);

        btnVolverNueva.setText("Volver");
        btnVolverNueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverNuevaActionPerformed(evt);
            }
        });
        panelNueva.add(btnVolverNueva);
        btnVolverNueva.setBounds(220, 165, 140, 35);

        panelNueva.add(lblMensajeNueva);
        lblMensajeNueva.setBounds(60, 220, 700, 22);

        getContentPane().add(panelNueva, "nueva");

        panelCargar.setPreferredSize(new java.awt.Dimension(1100, 690));
        panelCargar.setLayout(null);

        lblTituloCargar.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblTituloCargar.setText("CARGAR PARTIDA");
        panelCargar.add(lblTituloCargar);
        lblTituloCargar.setBounds(60, 40, 300, 22);

        lblPartidas.setText("Partidas guardadas:");
        panelCargar.add(lblPartidas);
        lblPartidas.setBounds(60, 90, 300, 22);

        panelCargar.add(cmbPartidas);
        cmbPartidas.setBounds(60, 115, 300, 30);

        btnAbrir.setText("Cargar");
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });
        panelCargar.add(btnAbrir);
        btnAbrir.setBounds(60, 165, 140, 35);

        btnVolverCargar.setText("Volver");
        btnVolverCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverCargarActionPerformed(evt);
            }
        });
        panelCargar.add(btnVolverCargar);
        btnVolverCargar.setBounds(220, 165, 140, 35);

        panelCargar.add(lblMensajeCargar);
        lblMensajeCargar.setBounds(60, 220, 700, 22);

        getContentPane().add(panelCargar, "cargar");

        panelPreparacion.setPreferredSize(new java.awt.Dimension(1100, 690));
        panelPreparacion.setLayout(null);

        panelMapa.setLayout(new java.awt.GridLayout(25, 25));
        panelPreparacion.add(panelMapa);
        panelMapa.setBounds(15, 15, 650, 650);

        lblComandante.setText("Comandante:");
        panelPreparacion.add(lblComandante);
        lblComandante.setBounds(690, 15, 380, 22);

        lblMision.setText("Misión:");
        panelPreparacion.add(lblMision);
        lblMision.setBounds(690, 40, 380, 22);

        lblCapacidad.setText("Capacidad usada:");
        panelPreparacion.add(lblCapacidad);
        lblCapacidad.setBounds(690, 80, 380, 22);

        barraCapacidad.setStringPainted(true);
        panelPreparacion.add(barraCapacidad);
        barraCapacidad.setBounds(690, 105, 380, 25);

        lblDefensa.setText("Defensa a colocar:");
        panelPreparacion.add(lblDefensa);
        lblDefensa.setBounds(690, 150, 380, 22);

        cmbDefensas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDefensasActionPerformed(evt);
            }
        });
        panelPreparacion.add(cmbDefensas);
        cmbDefensas.setBounds(690, 175, 380, 30);

        panelPreparacion.add(lblInfoDefensa);
        lblInfoDefensa.setBounds(690, 210, 380, 22);

        lblAyuda1.setText("- Clic en una casilla vacía: coloca la defensa.");
        panelPreparacion.add(lblAyuda1);
        lblAyuda1.setBounds(690, 250, 400, 22);

        lblAyuda2.setText("- Clic en una defensa del mapa: la quita.");
        panelPreparacion.add(lblAyuda2);
        lblAyuda2.setBounds(690, 272, 400, 22);

        lblAyuda3.setText("- El borde (rosado) es para las criaturas.");
        panelPreparacion.add(lblAyuda3);
        lblAyuda3.setBounds(690, 294, 400, 22);

        btnIniciar.setText("Iniciar batalla");
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });
        panelPreparacion.add(btnIniciar);
        btnIniciar.setBounds(690, 340, 185, 35);

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        panelPreparacion.add(btnGuardar);
        btnGuardar.setBounds(885, 340, 185, 35);

        btnSalirPreparacion.setText("Salir");
        btnSalirPreparacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirPreparacionActionPerformed(evt);
            }
        });
        panelPreparacion.add(btnSalirPreparacion);
        btnSalirPreparacion.setBounds(690, 385, 380, 35);

        panelPreparacion.add(lblMensajePreparacion);
        lblMensajePreparacion.setBounds(690, 440, 400, 22);

        getContentPane().add(panelPreparacion, "preparacion");

        panelBatalla.setPreferredSize(new java.awt.Dimension(1100, 690));
        panelBatalla.setLayout(null);

        panelMapaBatalla.setLayout(new java.awt.GridLayout(25, 25));
        panelBatalla.add(panelMapaBatalla);
        panelMapaBatalla.setBounds(15, 15, 650, 650);

        lblTituloBatalla.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblTituloBatalla.setText("BATALLA");
        panelBatalla.add(lblTituloBatalla);
        lblTituloBatalla.setBounds(690, 15, 380, 24);

        lblNucleo.setText("Núcleo de oxígeno:");
        panelBatalla.add(lblNucleo);
        lblNucleo.setBounds(690, 55, 380, 22);

        barraNucleo.setStringPainted(true);
        panelBatalla.add(barraNucleo);
        barraNucleo.setBounds(690, 80, 380, 25);

        lblTiempo.setText("Tiempo: 0 s");
        panelBatalla.add(lblTiempo);
        lblTiempo.setBounds(690, 115, 380, 22);

        lblCriaturasVivas.setText("Criaturas vivas: 0");
        panelBatalla.add(lblCriaturasVivas);
        lblCriaturasVivas.setBounds(690, 140, 380, 22);

        lblDefensasVivas.setText("Defensas vivas: 0");
        panelBatalla.add(lblDefensasVivas);
        lblDefensasVivas.setBounds(690, 165, 380, 22);

        lblUnidad.setText("Haz clic en una unidad del mapa para ver su registro.");
        panelBatalla.add(lblUnidad);
        lblUnidad.setBounds(690, 200, 400, 22);

        txtRegistro.setEditable(false);
        txtRegistro.setColumns(20);
        txtRegistro.setRows(5);
        scrollRegistro.setViewportView(txtRegistro);

        panelBatalla.add(scrollRegistro);
        scrollRegistro.setBounds(690, 225, 390, 330);

        btnVerResultado.setText("Ver resultado");
        btnVerResultado.setEnabled(false);
        btnVerResultado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerResultadoActionPerformed(evt);
            }
        });
        panelBatalla.add(btnVerResultado);
        btnVerResultado.setBounds(690, 570, 380, 35);

        panelBatalla.add(lblMensajeBatalla);
        lblMensajeBatalla.setBounds(690, 615, 400, 22);

        getContentPane().add(panelBatalla, "batalla");

        panelResultado.setPreferredSize(new java.awt.Dimension(1100, 690));
        panelResultado.setLayout(null);

        lblResultado.setFont(new java.awt.Font("SansSerif", 1, 26)); // NOI18N
        lblResultado.setText("RESULTADO");
        panelResultado.add(lblResultado);
        lblResultado.setBounds(60, 25, 980, 40);

        lblResumenMision.setText("Misión:");
        panelResultado.add(lblResumenMision);
        lblResumenMision.setBounds(60, 85, 480, 22);

        lblResumenTiempo.setText("Duración:");
        panelResultado.add(lblResumenTiempo);
        lblResumenTiempo.setBounds(60, 110, 480, 22);

        lblResumenCriaturas.setText("Criaturas eliminadas:");
        panelResultado.add(lblResumenCriaturas);
        lblResumenCriaturas.setBounds(60, 135, 480, 22);

        lblResumenDefensas.setText("Defensas perdidas:");
        panelResultado.add(lblResumenDefensas);
        lblResumenDefensas.setBounds(60, 160, 480, 22);

        lblResumenNucleo.setText("Vida final del núcleo:");
        panelResultado.add(lblResumenNucleo);
        lblResumenNucleo.setBounds(60, 185, 480, 22);

        lblResumenCapacidad.setText("Capacidad:");
        panelResultado.add(lblResumenCapacidad);
        lblResumenCapacidad.setBounds(60, 210, 480, 22);

        lblCrecimiento.setText("Crecimiento de las unidades:");
        panelResultado.add(lblCrecimiento);
        lblCrecimiento.setBounds(60, 250, 480, 22);

        txtCrecimiento.setEditable(false);
        txtCrecimiento.setColumns(20);
        txtCrecimiento.setRows(5);
        scrollCrecimiento.setViewportView(txtCrecimiento);

        panelResultado.add(scrollCrecimiento);
        scrollCrecimiento.setBounds(60, 275, 480, 300);

        lblRegistroCompleto.setText("Registro completo de la batalla:");
        panelResultado.add(lblRegistroCompleto);
        lblRegistroCompleto.setBounds(570, 85, 470, 22);

        txtRegistroCompleto.setEditable(false);
        txtRegistroCompleto.setColumns(20);
        txtRegistroCompleto.setRows(5);
        scrollRegistroCompleto.setViewportView(txtRegistroCompleto);

        panelResultado.add(scrollRegistroCompleto);
        scrollRegistroCompleto.setBounds(570, 110, 470, 465);

        btnSiguiente.setText("Siguiente misión");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });
        panelResultado.add(btnSiguiente);
        btnSiguiente.setBounds(60, 600, 230, 40);

        btnRepetir.setText("Repetir misión");
        btnRepetir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepetirActionPerformed(evt);
            }
        });
        panelResultado.add(btnRepetir);
        btnRepetir.setBounds(310, 600, 230, 40);

        btnTerminar.setText("Terminar campaña");
        btnTerminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTerminarActionPerformed(evt);
            }
        });
        panelResultado.add(btnTerminar);
        btnTerminar.setBounds(810, 600, 230, 40);

        getContentPane().add(panelResultado, "resultado");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraCapacidad;
    private javax.swing.JProgressBar barraNucleo;
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnComenzar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnNueva;
    private javax.swing.JButton btnRepetir;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnSalirPreparacion;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnTerminar;
    private javax.swing.JButton btnVerResultado;
    private javax.swing.JButton btnVolverCargar;
    private javax.swing.JButton btnVolverNueva;
    private javax.swing.JComboBox<String> cmbDefensas;
    private javax.swing.JComboBox<String> cmbPartidas;
    private javax.swing.JLabel lblAyuda1;
    private javax.swing.JLabel lblAyuda2;
    private javax.swing.JLabel lblAyuda3;
    private javax.swing.JLabel lblCapacidad;
    private javax.swing.JLabel lblComandante;
    private javax.swing.JLabel lblCrecimiento;
    private javax.swing.JLabel lblCriaturasVivas;
    private javax.swing.JLabel lblDefensa;
    private javax.swing.JLabel lblDefensasVivas;
    private javax.swing.JLabel lblInfoDefensa;
    private javax.swing.JLabel lblMensajeBatalla;
    private javax.swing.JLabel lblMensajeCargar;
    private javax.swing.JLabel lblMensajeInicio;
    private javax.swing.JLabel lblMensajeNueva;
    private javax.swing.JLabel lblMensajePreparacion;
    private javax.swing.JLabel lblMision;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNucleo;
    private javax.swing.JLabel lblPartidas;
    private javax.swing.JLabel lblRegistroCompleto;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblResumenCapacidad;
    private javax.swing.JLabel lblResumenCriaturas;
    private javax.swing.JLabel lblResumenDefensas;
    private javax.swing.JLabel lblResumenMision;
    private javax.swing.JLabel lblResumenNucleo;
    private javax.swing.JLabel lblResumenTiempo;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTiempo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloBatalla;
    private javax.swing.JLabel lblTituloCargar;
    private javax.swing.JLabel lblTituloNueva;
    private javax.swing.JLabel lblUnidad;
    private javax.swing.JPanel panelBatalla;
    private javax.swing.JPanel panelCargar;
    private javax.swing.JPanel panelInicio;
    private javax.swing.JPanel panelMapa;
    private javax.swing.JPanel panelMapaBatalla;
    private javax.swing.JPanel panelNueva;
    private javax.swing.JPanel panelPreparacion;
    private javax.swing.JPanel panelResultado;
    private javax.swing.JScrollPane scrollCrecimiento;
    private javax.swing.JScrollPane scrollRegistro;
    private javax.swing.JScrollPane scrollRegistroCompleto;
    private javax.swing.JTextArea txtCrecimiento;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextArea txtRegistro;
    private javax.swing.JTextArea txtRegistroCompleto;
    // End of variables declaration//GEN-END:variables
}
