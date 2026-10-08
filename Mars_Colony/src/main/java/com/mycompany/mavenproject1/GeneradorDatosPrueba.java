package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Crea los ARCHIVOS DE PRUEBA que pide el enunciado:
 *  - catalogo.dat: configuraciones de defensas y criaturas
 *  - partidas/Comandante Prueba.dat: una partida en la misión 3
 *
 * Se ejecuta aparte (clic derecho > Run File). Hay que volver a
 * ejecutarlo si se cambian los atributos de las clases, porque los
 * archivos viejos dejan de cargar.
 *
 * Los valores de las 4 defensas del enunciado son los del documento.
 * Los demás valores son decisión de diseño.
 *
 * @author dylnr
 */
public class GeneradorDatosPrueba {

    // main es static (así lo pide Java), por eso crea un objeto
    // generador y usa sus métodos normales.
    public static void main(String[] args) {
        GeneradorDatosPrueba generador = new GeneradorDatosPrueba();
        generador.generar();
    }

    public void generar() {
        Catalogo catalogo = crearCatalogo();

        ArchivoCatalogo archivoCatalogo = new ArchivoCatalogo();
        if (archivoCatalogo.guardar(catalogo)) {
            System.out.println("Catálogo guardado en " + ArchivoCatalogo.ARCHIVO);
        }
        mostrarCatalogo(catalogo);

        Partida partida = crearPartida(catalogo);
        ArchivoPartidas archivoPartidas = new ArchivoPartidas();
        if (archivoPartidas.guardar(partida)) {
            System.out.println("\nPartida guardada: " + partida.getNombreComandante()
                    + " (misión " + partida.getMisionActual()
                    + ", capacidad " + partida.getCapacidadTotal() + ")");
        }
        Batalla batalla = partida.getBatallaActual();
        System.out.println("Perímetro: " + batalla.getDefensas().size() + " defensas, usando "
                + partida.capacidadUsada(batalla) + " de " + partida.getCapacidadTotal() + " puntos");
        System.out.println("Partidas guardadas: " + archivoPartidas.listar());
    }

    private Catalogo crearCatalogo() {
        Catalogo catalogo = new Catalogo();

        // ----- Defensas del enunciado (valores del documento) -----
        Impacto emp = new Impacto("Pulso EMP", 4, 18, 1, 1, 1.0, 3);
        emp.setMisionMinima(4);
        ponerImagenes(emp, "pulso_emp");
        catalogo.agregarDefensa(emp);

        Alcance_medio laser = new Alcance_medio("Torre láser", 12, 3, 1, 2, 1.0, 6);
        ponerImagenes(laser, "torre_laser");
        catalogo.agregarDefensa(laser);

        Dron dron = new Dron("Dron Centinela", 9, 2, 1, 3, 1.0, 1);
        dron.setMisionMinima(3);
        ponerImagenes(dron, "dron_centinela");
        catalogo.agregarDefensa(dron);

        Barrera muro = new Barrera("Muro de titanio", 30, 1, 1);
        ponerImagenes(muro, "muro_titanio");
        catalogo.agregarDefensa(muro);

        // ----- Defensas extra para tener los 6 tipos (decisión de diseño) -----
        Contacto torreta = new Contacto("Torreta de choque", 15, 4, 1, 2, 1.0);
        ponerImagenes(torreta, "torreta_choque");
        catalogo.agregarDefensa(torreta);

        Ataque_multiple ametralladora = new Ataque_multiple("Ametralladora", 10, 2, 1, 3, 1.5, 4, 3);
        ametralladora.setMisionMinima(2);
        ponerImagenes(ametralladora, "ametralladora");
        catalogo.agregarDefensa(ametralladora);

        // Desactivada: el juego NO la debe usar
        Contacto vieja = new Contacto("Torreta antigua", 8, 2, 1, 1, 1.0);
        ponerImagenes(vieja, "torreta_antigua");
        catalogo.agregarDefensa(vieja);
        catalogo.desactivar("Torreta antigua");

        // ----- Criaturas (decisión de diseño) -----
        Acechador acechador = new Acechador("Acechador", 10, 2, 1, 1, 1.0);
        ponerImagenes(acechador, "acechador");
        catalogo.agregarCriatura(acechador);

        Escupidor escupidor = new Escupidor("Escupidor", 7, 2, 1, 2, 1.0, 3);
        ponerImagenes(escupidor, "escupidor");
        catalogo.agregarCriatura(escupidor);

        Volador volador = new Volador("Volador", 6, 2, 1, 2, 1.0);
        volador.setMisionMinima(2);
        ponerImagenes(volador, "volador");
        catalogo.agregarCriatura(volador);

        Enjambre enjambre = new Enjambre("Enjambre", 8, 1, 1, 2, 2.0, 2, 3);
        enjambre.setMisionMinima(3);
        ponerImagenes(enjambre, "enjambre");
        catalogo.agregarCriatura(enjambre);

        Demoledor demoledor = new Demoledor("Demoledor", 12, 10, 1, 3, 0.5, 2);
        demoledor.setMisionMinima(5);
        ponerImagenes(demoledor, "demoledor");
        catalogo.agregarCriatura(demoledor);

        // Desactivada: el juego NO la debe usar
        Acechador gigante = new Acechador("Acechador gigante", 40, 6, 1, 5, 0.5);
        ponerImagenes(gigante, "acechador_gigante");
        catalogo.agregarCriatura(gigante);
        catalogo.desactivar("Acechador gigante");

        return catalogo;
    }

    // Rutas de las 3 imágenes que pide el enunciado (las imágenes se agregan en la etapa 7)
    private void ponerImagenes(UnidadCombate unidad, String clave) {
        unidad.setRutaImagenNormal("imagenes/" + clave + "_normal.gif");
        unidad.setRutaImagenMovimiento("imagenes/" + clave + "_movimiento.gif");
        unidad.setRutaImagenAtaque("imagenes/" + clave + "_ataque.gif");
    }

    // Partida de ejemplo: ya va por la misión 3 y tiene el perímetro armado
    private Partida crearPartida(Catalogo catalogo) {
        Partida partida = new Partida("Comandante Prueba");
        partida.cargarPlantillas(catalogo);
        partida.superarMision(); // misión 2
        partida.superarMision(); // misión 3 (capacidad 30)

        Batalla batalla = partida.prepararBatalla();
        ArrayList<Defensa> disponibles = partida.defensasDisponibles();
        // Casillas alrededor del núcleo (12, 12)
        int[][] casillas = {
            {10, 12}, {14, 12}, {12, 10}, {12, 14},
            {10, 10}, {10, 14}, {14, 10}, {14, 14},
            {11, 11}, {11, 13}, {13, 11}, {13, 13}
        };
        int indice = 0;
        for (int i = 0; i < casillas.length; i++) {
            Defensa tipo = disponibles.get(indice);
            partida.colocarDefensa(batalla, tipo.copiar(), casillas[i][0], casillas[i][1]);
            indice = indice + 1;
            if (indice == disponibles.size()) {
                indice = 0; // vuelve a empezar la lista
            }
        }
        return partida;
    }

    private void mostrarCatalogo(Catalogo catalogo) {
        System.out.println("\nDefensas:");
        for (Defensa d : catalogo.getDefensas()) {
            System.out.println("  " + d.getNombre() + " | vida " + d.getVidaMaxima() + ", daño " + d.getDaño()
                    + ", costo " + d.getCosto() + ", misión " + d.getMisionMinima() + textoEstado(d));
        }
        System.out.println("Criaturas:");
        for (Criatura c : catalogo.getCriaturas()) {
            System.out.println("  " + c.getNombre() + " | vida " + c.getVidaMaxima() + ", daño " + c.getDaño()
                    + ", costo " + c.getCosto() + ", misión " + c.getMisionMinima() + textoEstado(c));
        }
    }

    private String textoEstado(UnidadCombate unidad) {
        if (unidad.isActivo()) {
            return "";
        }
        return "  (DESACTIVADA)";
    }
}
