package com.mycompany.mavenproject1.interfaz;

import com.mycompany.mavenproject1.*;
import com.mycompany.mavenproject1.archivos.ArchivoCatalogo;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

/**
 * PROGRAMA 2: administración del catálogo (JFrame Form de NetBeans).
 *
 * Permite, con usuario y contraseña, CREAR, MODIFICAR, CONSULTAR y
 * DESACTIVAR las configuraciones de defensas y criaturas que usa el
 * juego. Todo se guarda en catalogo.dat (ArchivoCatalogo).
 *
 * Pantallas (CardLayout, diseñadas en Design):
 *  - "login":    usuario y contraseña
 *  - "catalogo": lista de configuraciones + formulario
 *
 * La creación es POLIMÓRFICA: la FabricaUnidades crea la subclase
 * correcta según el tipo elegido en la lista de tipos.
 *
 * @author dylnr
 */
public class VentanaAdmin extends javax.swing.JFrame {

    // Acceso del administrador (decisión de diseño: constantes)
    public static final String USUARIO = "admin";
    public static final String CONTRASEÑA = "marte2187";

    private ArchivoCatalogo archivoCatalogo;
    private Catalogo catalogo;
    private FabricaUnidades fabrica;
    private ArrayList<String> nombresLista;   // misma posición que en cmbConfiguraciones
    private String nombreEditando;            // configuración elegida (null = una nueva)
    private boolean llenando;                 // true mientras el programa llena las listas

    public VentanaAdmin() {
        initComponents();   // arma todo lo diseñado en Design (lo escribe NetBeans)
        this.archivoCatalogo = new ArchivoCatalogo();
        this.fabrica = new FabricaUnidades();
        this.nombresLista = new ArrayList<String>();
        this.nombreEditando = null;
        this.llenando = false;
        lblVistaPrevia.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        // La lista de tipos: primero los de defensa y después los de criatura
        for (String tipo : FabricaUnidades.TIPOS_DEFENSA) {
            cmbTipo.addItem(tipo);
        }
        for (String tipo : FabricaUnidades.TIPOS_CRIATURA) {
            cmbTipo.addItem(tipo);
        }
        mostrarPantalla("login");
    }

    public static void main(String[] args) {
        VentanaAdmin ventana = new VentanaAdmin();
        ventana.setVisible(true);
    }

    private void mostrarPantalla(String nombre) {
        CardLayout cartas = (CardLayout) getContentPane().getLayout();
        cartas.show(getContentPane(), nombre);
    }

    // =====================================================================
    //  EVENTOS DE LOS BOTONES DISEÑADOS (los crea NetBeans con doble clic)
    // =====================================================================

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        txtContrasena.requestFocusInWindow();   // Enter en usuario pasa a la contraseña
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void txtContrasenaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContrasenaActionPerformed
        entrar();   // Enter en la contraseña = Entrar
    }//GEN-LAST:event_txtContrasenaActionPerformed

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrarActionPerformed
        entrar();
    }//GEN-LAST:event_btnEntrarActionPerformed

    private void cmbConfiguracionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbConfiguracionesActionPerformed
        mostrarSeleccionada();
    }//GEN-LAST:event_cmbConfiguracionesActionPerformed

    private void cmbTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoActionPerformed
        actualizarCampos();   // cambia qué campos se pueden llenar
    }//GEN-LAST:event_cmbTipoActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        nuevo();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnActivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActivarActionPerformed
        cambiarEstado();
    }//GEN-LAST:event_btnActivarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    // =====================================================================
    //  LOGIN
    // =====================================================================

    private void entrar() {
        String usuario = txtUsuario.getText().trim();
        // getPassword() devuelve los caracteres escritos; new String(...) los vuelve texto
        String contraseña = new String(txtContrasena.getPassword());
        if (!usuario.equals(USUARIO) || !contraseña.equals(CONTRASEÑA)) {
            lblMensajeLogin.setText("Usuario o contraseña incorrectos.");
            txtContrasena.setText("");
            return;
        }
        catalogo = archivoCatalogo.cargar();
        if (catalogo == null) {
            catalogo = new Catalogo();   // no existía: se empieza con uno vacío
        }
        mostrarPantalla("catalogo");
        actualizarLista();
        nuevo();
        lblMensajeAdmin.setText("Bienvenido. Elige una configuración o crea una nueva.");
    }

    // =====================================================================
    //  CONSULTAR
    // =====================================================================

    // Llena la lista de configuraciones (defensas y luego criaturas).
    private void actualizarLista() {
        llenando = true;   // para que cmbConfiguraciones no reaccione mientras se llena
        cmbConfiguraciones.removeAllItems();
        nombresLista = new ArrayList<String>();
        for (Defensa defensa : catalogo.getDefensas()) {
            cmbConfiguraciones.addItem(textoEnLista(defensa, "defensa"));
            nombresLista.add(defensa.getNombre());
        }
        for (Criatura criatura : catalogo.getCriaturas()) {
            cmbConfiguraciones.addItem(textoEnLista(criatura, "criatura"));
            nombresLista.add(criatura.getNombre());
        }
        cmbConfiguraciones.setSelectedIndex(-1);   // ninguna elegida
        llenando = false;
    }

    private String textoEnLista(UnidadCombate unidad, String categoria) {
        String texto = unidad.getNombre() + " (" + categoria + ")";
        if (!unidad.isActivo()) {
            texto = texto + " [inactiva]";
        }
        return texto;
    }

    // Busca una configuración por nombre (defensa o criatura).
    private UnidadCombate buscar(String nombre) {
        Defensa defensa = catalogo.buscarDefensa(nombre);
        if (defensa != null) {
            return defensa;
        }
        return catalogo.buscarCriatura(nombre);
    }

    // Se eligió una configuración en la lista: se muestra y se pasa al formulario.
    private void mostrarSeleccionada() {
        int indice = cmbConfiguraciones.getSelectedIndex();
        if (llenando || indice < 0) {
            return;
        }
        UnidadCombate unidad = buscar(nombresLista.get(indice));
        if (unidad == null) {
            return;
        }
        nombreEditando = unidad.getNombre();
        txtDetalle.setText(textoDetalle(unidad));
        txtDetalle.setCaretPosition(0);
        llenarFormulario(unidad);
        lblMensajeAdmin.setText("Editando " + unidad.getNombre() + ". Cambia los datos y presiona Guardar.");
    }

    private String textoDetalle(UnidadCombate unidad) {
        String estado = "activa";
        if (!unidad.isActivo()) {
            estado = "INACTIVA (el juego no la usa)";
        }
        String categoria = "criatura";
        if (fabrica.esDefensa(unidad.getTipo())) {
            categoria = "defensa";
        }
        String texto = unidad.getNombre() + "\n"
                + "Tipo: " + unidad.getTipo() + " (" + categoria + ")\n"
                + "Estado: " + estado + "\n\n"
                + "Vida: " + unidad.getVidaMaxima() + "\n"
                + "Daño: " + unidad.getDaño() + "\n"
                + "Costo: " + unidad.getCosto() + "\n"
                + "Frecuencia: " + unidad.getFrecuencia() + "\n"
                + "Alcance: " + unidad.getAlcance() + "\n"
                + "Radio: " + textoRadio(unidad) + "\n";
        if (fabrica.usaCantidad(unidad.getTipo())) {
            texto = texto + "Cantidad: " + unidad.getCantidad() + "\n";
        }
        texto = texto + "Misión mínima: " + unidad.getMisionMinima() + "\n\n"
                + "Imágenes:\n"
                + "  normal: " + unidad.getRutaImagenNormal() + "\n"
                + "  movimiento: " + unidad.getRutaImagenMovimiento() + "\n"
                + "  ataque: " + unidad.getRutaImagenAtaque() + "\n";
        return texto;
    }

    // Solo las unidades que explotan (IExplosivo) tienen radio.
    // La fábrica dice si el tipo explota; entonces es seguro el cast.
    private String textoRadio(UnidadCombate unidad) {
        if (!fabrica.usaRadio(unidad.getTipo())) {
            return "no aplica";
        }
        IExplosivo explosivo = (IExplosivo) unidad;
        return "" + explosivo.getRadio();
    }

    private void llenarFormulario(UnidadCombate unidad) {
        cmbTipo.setSelectedItem(unidad.getTipo());   // polimorfismo: la unidad dice su tipo
        txtNombre.setText(unidad.getNombre());
        txtVida.setText("" + unidad.getVidaMaxima());
        txtDano.setText("" + unidad.getDaño());
        txtCosto.setText("" + unidad.getCosto());
        txtFrecuencia.setText("" + unidad.getFrecuencia());
        txtAlcance.setText("" + unidad.getAlcance());
        if (fabrica.usaRadio(unidad.getTipo())) {
            txtRadio.setText(textoRadio(unidad));
        } else {
            txtRadio.setText("" + IExplosivo.RADIO_MINIMO);   // por si le cambian el tipo a uno que explota
        }
        txtCantidad.setText("" + unidad.getCantidad());
        txtMision.setText("" + unidad.getMisionMinima());
        txtImagenNormal.setText(unidad.getRutaImagenNormal());
        txtImagenMovimiento.setText(unidad.getRutaImagenMovimiento());
        txtImagenAtaque.setText(unidad.getRutaImagenAtaque());
        chkActiva.setSelected(unidad.isActivo());
        btnActivar.setEnabled(true);
        if (unidad.isActivo()) {
            btnActivar.setText("Desactivar");
        } else {
            btnActivar.setText("Activar");
        }
        actualizarCampos();
        actualizarVistaPrevia();
    }

    // =====================================================================
    //  CREAR / MODIFICAR
    // =====================================================================

    // Deja el formulario limpio para crear una configuración nueva.
    private void nuevo() {
        nombreEditando = null;
        llenando = true;
        cmbConfiguraciones.setSelectedIndex(-1);
        llenando = false;
        cmbTipo.setSelectedIndex(0);
        txtNombre.setText("");
        txtVida.setText("");
        txtDano.setText("");
        txtCosto.setText("");
        txtFrecuencia.setText("1.0");
        txtAlcance.setText("1");
        txtRadio.setText("1");
        txtCantidad.setText("1");
        txtMision.setText("1");
        txtImagenNormal.setText("imagenes/");
        txtImagenMovimiento.setText("imagenes/");
        txtImagenAtaque.setText("imagenes/");
        chkActiva.setSelected(true);
        txtDetalle.setText("");
        btnActivar.setEnabled(false);   // todavía no existe: no hay nada que desactivar
        btnActivar.setText("Desactivar");
        actualizarCampos();
        actualizarVistaPrevia();
        lblMensajeAdmin.setText("Nueva configuración: elige el tipo, llena los datos y presiona Guardar.");
    }

    // Activa solo los campos que usa el tipo elegido.
    private void actualizarCampos() {
        String tipo = (String) cmbTipo.getSelectedItem();
        if (tipo == null) {
            return;
        }
        txtDano.setEnabled(fabrica.usaDaño(tipo));
        txtFrecuencia.setEnabled(fabrica.usaFrecuencia(tipo));
        txtAlcance.setEnabled(fabrica.usaAlcance(tipo));
        txtRadio.setEnabled(fabrica.usaRadio(tipo));
        txtCantidad.setEnabled(fabrica.usaCantidad(tipo));
        if (fabrica.esDefensa(tipo)) {
            lblCategoria.setText("(defensa)");
        } else {
            lblCategoria.setText("(criatura)");
        }
    }

    private void guardar() {
        String tipo = (String) cmbTipo.getSelectedItem();
        String nombre = txtNombre.getText().trim();
        if (nombre.equals("")) {
            lblMensajeAdmin.setText("Escribe un nombre.");
            return;
        }
        // Si se está modificando, no se puede pasar de defensa a criatura ni al revés
        if (nombreEditando != null) {
            boolean eraDefensa = catalogo.buscarDefensa(nombreEditando) != null;
            if (eraDefensa != fabrica.esDefensa(tipo)) {
                lblMensajeAdmin.setText("No se puede cambiar de defensa a criatura (o al revés): crea una nueva.");
                return;
            }
        }
        // Si es nueva, o si le cambiaron el nombre, el nombre no puede estar repetido
        if ((nombreEditando == null || !nombreEditando.equals(nombre)) && catalogo.existeNombre(nombre)) {
            lblMensajeAdmin.setText("Ya existe una configuración llamada " + nombre + ".");
            return;
        }
        try {
            int vida = leerEntero(txtVida);
            int daño = leerEntero(txtDano);
            int costo = leerEntero(txtCosto);
            int alcance = leerEntero(txtAlcance);
            int radio = leerEntero(txtRadio);
            int cantidad = leerEntero(txtCantidad);
            int mision = leerEntero(txtMision);
            double frecuencia = 0;
            if (txtFrecuencia.isEnabled()) {
                frecuencia = Double.parseDouble(txtFrecuencia.getText().trim());
            }
            if (vida <= 0 || costo <= 0 || mision <= 0) {
                lblMensajeAdmin.setText("Vida, costo y misión mínima deben ser mayores que 0.");
                return;
            }
            if ((txtFrecuencia.isEnabled() && frecuencia <= 0) || (txtAlcance.isEnabled() && alcance <= 0)
                    || (txtRadio.isEnabled() && radio <= 0) || (txtCantidad.isEnabled() && cantidad <= 0)) {
                lblMensajeAdmin.setText("Frecuencia, alcance, radio y cantidad deben ser mayores que 0.");
                return;
            }

            // CREACIÓN POLIMÓRFICA: la fábrica elige la subclase según el tipo
            if (fabrica.esDefensa(tipo)) {
                Defensa defensa = fabrica.crearDefensa(tipo, nombre, vida, daño, costo, frecuencia, alcance, radio, cantidad);
                completarDatos(defensa, mision);
                if (nombreEditando == null) {
                    catalogo.agregarDefensa(defensa);
                } else {
                    catalogo.modificarDefensa(nombreEditando, defensa);
                }
            } else {
                Criatura criatura = fabrica.crearCriatura(tipo, nombre, vida, daño, costo, frecuencia, alcance, radio, cantidad);
                completarDatos(criatura, mision);
                if (nombreEditando == null) {
                    catalogo.agregarCriatura(criatura);
                } else {
                    catalogo.modificarCriatura(nombreEditando, criatura);
                }
            }
        } catch (Exception e) {
            // parseInt / parseDouble fallan si el texto no es un número,
            // y los constructores fallan si algún dato no es válido
            lblMensajeAdmin.setText("Datos inválidos: revisa que los números estén bien escritos y sean positivos.");
            return;
        }
        if (!archivoCatalogo.guardar(catalogo)) {
            lblMensajeAdmin.setText("No se pudo guardar catalogo.dat.");
            return;
        }
        actualizarLista();
        seleccionar(nombre);
        lblMensajeAdmin.setText("Configuración " + nombre + " guardada en el catálogo.");
    }

    // Misión mínima, rutas de imagen y estado (no van en los constructores).
    private void completarDatos(UnidadCombate unidad, int mision) {
        unidad.setMisionMinima(mision);
        unidad.setRutaImagenNormal(txtImagenNormal.getText().trim());
        unidad.setRutaImagenMovimiento(txtImagenMovimiento.getText().trim());
        unidad.setRutaImagenAtaque(txtImagenAtaque.getText().trim());
        unidad.setActivo(chkActiva.isSelected());
    }

    // Número entero del campo; si el campo está desactivado (ese tipo no lo usa) vale 0.
    private int leerEntero(JTextField campo) {
        if (!campo.isEnabled()) {
            return 0;
        }
        return Integer.parseInt(campo.getText().trim());
    }

    // Deja elegida en la lista la configuración con ese nombre.
    private void seleccionar(String nombre) {
        int indice = nombresLista.indexOf(nombre);
        if (indice >= 0) {
            cmbConfiguraciones.setSelectedIndex(indice);   // esto llama a mostrarSeleccionada
        }
    }

    // =====================================================================
    //  DESACTIVAR / ACTIVAR
    // =====================================================================

    private void cambiarEstado() {
        if (nombreEditando == null) {
            return;
        }
        UnidadCombate unidad = buscar(nombreEditando);
        if (unidad.isActivo()) {
            catalogo.desactivar(nombreEditando);
        } else {
            catalogo.activar(nombreEditando);
        }
        archivoCatalogo.guardar(catalogo);
        String nombre = nombreEditando;
        actualizarLista();
        seleccionar(nombre);
        if (unidad.isActivo()) {
            lblMensajeAdmin.setText(nombre + " está activa: el juego la puede usar.");
        } else {
            lblMensajeAdmin.setText(nombre + " quedó desactivada: el juego ya no la usa (no se borró).");
        }
    }

    // =====================================================================
    //  AYUDANTES
    // =====================================================================

    private void actualizarVistaPrevia() {
        String ruta = txtImagenNormal.getText().trim();
        if (ruta.equals("") || !new File(ruta).isFile()) {
            lblVistaPrevia.setIcon(null);
            lblVistaPrevia.setText(" sin imagen");
            return;
        }
        Image imagen = new ImageIcon(ruta).getImage().getScaledInstance(96, 96, Image.SCALE_DEFAULT);
        lblVistaPrevia.setText("");
        lblVistaPrevia.setIcon(new ImageIcon(imagen));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelLogin = new javax.swing.JPanel();
        lblTituloAdmin = new javax.swing.JLabel();
        lblSubtituloAdmin = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblContrasena = new javax.swing.JLabel();
        txtContrasena = new javax.swing.JPasswordField();
        btnEntrar = new javax.swing.JButton();
        lblMensajeLogin = new javax.swing.JLabel();
        panelCatalogo = new javax.swing.JPanel();
        lblConfiguraciones = new javax.swing.JLabel();
        cmbConfiguraciones = new javax.swing.JComboBox<>();
        scrollDetalle = new javax.swing.JScrollPane();
        txtDetalle = new javax.swing.JTextArea();
        lblTipo = new javax.swing.JLabel();
        cmbTipo = new javax.swing.JComboBox<>();
        lblCategoria = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblVida = new javax.swing.JLabel();
        txtVida = new javax.swing.JTextField();
        lblDano = new javax.swing.JLabel();
        txtDano = new javax.swing.JTextField();
        lblCosto = new javax.swing.JLabel();
        txtCosto = new javax.swing.JTextField();
        lblFrecuencia = new javax.swing.JLabel();
        txtFrecuencia = new javax.swing.JTextField();
        lblAlcance = new javax.swing.JLabel();
        txtAlcance = new javax.swing.JTextField();
        lblRadio = new javax.swing.JLabel();
        txtRadio = new javax.swing.JTextField();
        lblCantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        lblMision = new javax.swing.JLabel();
        txtMision = new javax.swing.JTextField();
        lblImagenNormal = new javax.swing.JLabel();
        txtImagenNormal = new javax.swing.JTextField();
        lblImagenMovimiento = new javax.swing.JLabel();
        txtImagenMovimiento = new javax.swing.JTextField();
        lblImagenAtaque = new javax.swing.JLabel();
        txtImagenAtaque = new javax.swing.JTextField();
        chkActiva = new javax.swing.JCheckBox();
        lblVistaPreviaTitulo = new javax.swing.JLabel();
        lblVistaPrevia = new javax.swing.JLabel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnActivar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        lblMensajeAdmin = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Colonia Marte - Administración");
        setResizable(false);
        getContentPane().setLayout(new java.awt.CardLayout());

        panelLogin.setPreferredSize(new java.awt.Dimension(900, 600));
        panelLogin.setLayout(null);

        lblTituloAdmin.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTituloAdmin.setText("ADMINISTRACIÓN - COLONIA MARTE");
        panelLogin.add(lblTituloAdmin);
        lblTituloAdmin.setBounds(60, 50, 700, 40);

        lblSubtituloAdmin.setText("Configuración de defensas y criaturas");
        panelLogin.add(lblSubtituloAdmin);
        lblSubtituloAdmin.setBounds(62, 90, 500, 22);

        lblUsuario.setText("Usuario:");
        panelLogin.add(lblUsuario);
        lblUsuario.setBounds(60, 140, 200, 22);

        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });
        panelLogin.add(txtUsuario);
        txtUsuario.setBounds(60, 165, 260, 30);

        lblContrasena.setText("Contraseña:");
        panelLogin.add(lblContrasena);
        lblContrasena.setBounds(60, 210, 200, 22);

        txtContrasena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContrasenaActionPerformed(evt);
            }
        });
        panelLogin.add(txtContrasena);
        txtContrasena.setBounds(60, 235, 260, 30);

        btnEntrar.setText("Entrar");
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrarActionPerformed(evt);
            }
        });
        panelLogin.add(btnEntrar);
        btnEntrar.setBounds(60, 290, 260, 35);

        panelLogin.add(lblMensajeLogin);
        lblMensajeLogin.setBounds(60, 340, 700, 22);

        getContentPane().add(panelLogin, "login");

        panelCatalogo.setPreferredSize(new java.awt.Dimension(900, 600));
        panelCatalogo.setLayout(null);

        lblConfiguraciones.setText("Configuraciones del catálogo:");
        panelCatalogo.add(lblConfiguraciones);
        lblConfiguraciones.setBounds(20, 15, 330, 22);

        cmbConfiguraciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbConfiguracionesActionPerformed(evt);
            }
        });
        panelCatalogo.add(cmbConfiguraciones);
        cmbConfiguraciones.setBounds(20, 40, 330, 30);

        txtDetalle.setEditable(false);
        txtDetalle.setColumns(20);
        txtDetalle.setRows(5);
        scrollDetalle.setViewportView(txtDetalle);

        panelCatalogo.add(scrollDetalle);
        scrollDetalle.setBounds(20, 80, 330, 440);

        lblTipo.setText("Tipo:");
        panelCatalogo.add(lblTipo);
        lblTipo.setBounds(380, 15, 110, 22);

        cmbTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoActionPerformed(evt);
            }
        });
        panelCatalogo.add(cmbTipo);
        cmbTipo.setBounds(500, 15, 220, 28);

        panelCatalogo.add(lblCategoria);
        lblCategoria.setBounds(730, 15, 140, 22);

        lblNombre.setText("Nombre:");
        panelCatalogo.add(lblNombre);
        lblNombre.setBounds(380, 50, 110, 22);

        panelCatalogo.add(txtNombre);
        txtNombre.setBounds(500, 50, 220, 28);

        lblVida.setText("Vida:");
        panelCatalogo.add(lblVida);
        lblVida.setBounds(380, 85, 110, 22);

        panelCatalogo.add(txtVida);
        txtVida.setBounds(500, 85, 100, 28);

        lblDano.setText("Daño:");
        panelCatalogo.add(lblDano);
        lblDano.setBounds(380, 120, 110, 22);

        panelCatalogo.add(txtDano);
        txtDano.setBounds(500, 120, 100, 28);

        lblCosto.setText("Costo:");
        panelCatalogo.add(lblCosto);
        lblCosto.setBounds(380, 155, 110, 22);

        panelCatalogo.add(txtCosto);
        txtCosto.setBounds(500, 155, 100, 28);

        lblFrecuencia.setText("Frecuencia:");
        panelCatalogo.add(lblFrecuencia);
        lblFrecuencia.setBounds(380, 190, 110, 22);

        panelCatalogo.add(txtFrecuencia);
        txtFrecuencia.setBounds(500, 190, 100, 28);

        lblAlcance.setText("Alcance:");
        panelCatalogo.add(lblAlcance);
        lblAlcance.setBounds(380, 225, 110, 22);

        panelCatalogo.add(txtAlcance);
        txtAlcance.setBounds(500, 225, 100, 28);

        lblRadio.setText("Radio:");
        panelCatalogo.add(lblRadio);
        lblRadio.setBounds(380, 260, 110, 22);

        panelCatalogo.add(txtRadio);
        txtRadio.setBounds(500, 260, 100, 28);

        lblCantidad.setText("Cantidad:");
        panelCatalogo.add(lblCantidad);
        lblCantidad.setBounds(380, 295, 110, 22);

        panelCatalogo.add(txtCantidad);
        txtCantidad.setBounds(500, 295, 100, 28);

        lblMision.setText("Misión mínima:");
        panelCatalogo.add(lblMision);
        lblMision.setBounds(380, 330, 110, 22);

        panelCatalogo.add(txtMision);
        txtMision.setBounds(500, 330, 100, 28);

        lblImagenNormal.setText("Imagen normal:");
        panelCatalogo.add(lblImagenNormal);
        lblImagenNormal.setBounds(380, 365, 120, 22);

        panelCatalogo.add(txtImagenNormal);
        txtImagenNormal.setBounds(500, 365, 240, 28);

        lblImagenMovimiento.setText("Imagen movimiento:");
        panelCatalogo.add(lblImagenMovimiento);
        lblImagenMovimiento.setBounds(380, 400, 120, 22);

        panelCatalogo.add(txtImagenMovimiento);
        txtImagenMovimiento.setBounds(500, 400, 240, 28);

        lblImagenAtaque.setText("Imagen ataque:");
        panelCatalogo.add(lblImagenAtaque);
        lblImagenAtaque.setBounds(380, 435, 120, 22);

        panelCatalogo.add(txtImagenAtaque);
        txtImagenAtaque.setBounds(500, 435, 240, 28);

        chkActiva.setText("Activa");
        panelCatalogo.add(chkActiva);
        chkActiva.setBounds(500, 470, 100, 25);

        lblVistaPreviaTitulo.setText("Vista previa:");
        panelCatalogo.add(lblVistaPreviaTitulo);
        lblVistaPreviaTitulo.setBounds(760, 60, 120, 22);

        panelCatalogo.add(lblVistaPrevia);
        lblVistaPrevia.setBounds(760, 85, 100, 100);

        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        panelCatalogo.add(btnNuevo);
        btnNuevo.setBounds(20, 530, 150, 35);

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        panelCatalogo.add(btnGuardar);
        btnGuardar.setBounds(180, 530, 150, 35);

        btnActivar.setText("Desactivar");
        btnActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActivarActionPerformed(evt);
            }
        });
        panelCatalogo.add(btnActivar);
        btnActivar.setBounds(340, 530, 150, 35);

        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        panelCatalogo.add(btnSalir);
        btnSalir.setBounds(720, 530, 150, 35);

        panelCatalogo.add(lblMensajeAdmin);
        lblMensajeAdmin.setBounds(20, 570, 860, 22);

        getContentPane().add(panelCatalogo, "catalogo");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnEntrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnSalir;
    private javax.swing.JCheckBox chkActiva;
    private javax.swing.JComboBox<String> cmbConfiguraciones;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel lblAlcance;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblConfiguraciones;
    private javax.swing.JLabel lblContrasena;
    private javax.swing.JLabel lblCosto;
    private javax.swing.JLabel lblDano;
    private javax.swing.JLabel lblFrecuencia;
    private javax.swing.JLabel lblImagenAtaque;
    private javax.swing.JLabel lblImagenMovimiento;
    private javax.swing.JLabel lblImagenNormal;
    private javax.swing.JLabel lblMensajeAdmin;
    private javax.swing.JLabel lblMensajeLogin;
    private javax.swing.JLabel lblMision;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblRadio;
    private javax.swing.JLabel lblSubtituloAdmin;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTituloAdmin;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JLabel lblVida;
    private javax.swing.JLabel lblVistaPrevia;
    private javax.swing.JLabel lblVistaPreviaTitulo;
    private javax.swing.JPanel panelCatalogo;
    private javax.swing.JPanel panelLogin;
    private javax.swing.JScrollPane scrollDetalle;
    private javax.swing.JTextField txtAlcance;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtCosto;
    private javax.swing.JTextField txtDano;
    private javax.swing.JTextArea txtDetalle;
    private javax.swing.JTextField txtFrecuencia;
    private javax.swing.JTextField txtImagenAtaque;
    private javax.swing.JTextField txtImagenMovimiento;
    private javax.swing.JTextField txtImagenNormal;
    private javax.swing.JTextField txtMision;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtRadio;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txtVida;
    // End of variables declaration//GEN-END:variables
}
