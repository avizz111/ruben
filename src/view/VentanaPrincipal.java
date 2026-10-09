package view;

import controller.CatalogoController;
import controller.LibroController;
import model.Autor;
import model.ConexionBD;
import model.Editorial;
import model.Genero;
import model.Libro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

/**
 * Interfaz Gráfica Principal (Capa Vista en patrón MVC).
 * Permite Insertar, Actualizar, Eliminar (Lógica) y Consultar libros
 * reflejando los cambios en tiempo real con la base de datos MySQL.
 */
public class VentanaPrincipal extends JFrame {

    // Controladores (MVC)
    private final LibroController libroController;
    private final CatalogoController catalogoController;

    // Componentes del Formulario
    private JTextField txtIdLibro;
    private JTextField txtTitulo;
    private JComboBox<Autor> cmbAutor;
    private JComboBox<Editorial> cmbEditorial;
    private JComboBox<Genero> cmbGenero;
    private JTextField txtIsbn;
    private JComboBox<String> cmbEstatus;
    private JLabel lblFotoPreview;
    private String fotoBase64Actual = "";

    // Botones de acción
    private JButton btnNuevo;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminarLogico;
    private JButton btnReactivar;
    private JButton btnSeleccionarFoto;
    private JButton btnQuitarFoto;

    // Componentes de Consulta / Tabla
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnLimpiarBusqueda;
    private JCheckBox chkSoloActivos;
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstadoConexion;
    private JLabel lblTotalRegistros;

    // Libro actualmente seleccionado
    private Libro libroSeleccionado = null;

    public VentanaPrincipal() {
        this.libroController = new LibroController();
        this.catalogoController = new CatalogoController();

        configurarVentana();
        inicializarComponentes();
        cargarCatalogos();
        actualizarTabla();
        limpiarFormulario();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Biblioteca - Universidad Tecnológica de León");
        setSize(1180, 720);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Aplicar apariencia moderna del sistema o Nimbus si está disponible
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void inicializarComponentes() {
        // 1. Encabezado Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(24, 76, 120));
        panelHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTituloApp = new JLabel("UNIVERSIDAD TECNOLÓGICA DE LEÓN — SISTEMA BIBLIOTECARIO");
        lblTituloApp.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloApp.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Módulo de Gestión de Libros | Patrón MVC | Base de Datos MySQL");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(220, 235, 252));

        panelHeader.add(lblTituloApp, BorderLayout.NORTH);
        panelHeader.add(lblSubtitulo, BorderLayout.SOUTH);
        add(panelHeader, BorderLayout.NORTH);

        // 2. Contenedor Central Dividido (Formulario a la izquierda, Tabla a la derecha)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.38);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel panelIzquierdo = crearPanelFormulario();
        JPanel panelDerecho = crearPanelTabla();

        splitPane.setLeftComponent(panelIzquierdo);
        splitPane.setRightComponent(panelDerecho);
        add(splitPane, BorderLayout.CENTER);

        // 3. Barra de Estado Inferior
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBorder(new EmptyBorder(5, 15, 8, 15));
        panelFooter.setBackground(new Color(245, 245, 245));

        boolean dbOk = ConexionBD.testConnection();
        lblEstadoConexion = new JLabel(dbOk ? "● Conectado a MySQL (" + ConexionBD.getDatabaseName() + ")" : "○ Error de conexión MySQL");
        lblEstadoConexion.setForeground(dbOk ? new Color(28, 130, 48) : Color.RED);
        lblEstadoConexion.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblTotalRegistros = new JLabel("Registros cargados: 0");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panelFooter.add(lblEstadoConexion, BorderLayout.WEST);
        panelFooter.add(lblTotalRegistros, BorderLayout.EAST);
        add(panelFooter, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 195, 210), 1, true),
                        " Datos del Libro (Modelo) ",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(24, 76, 120)
                )
        ));

        // Panel con scroll para campos
        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID Libro
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        camposPanel.add(new JLabel("idLibro (PK):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        txtIdLibro = new JTextField();
        txtIdLibro.setEditable(false);
        txtIdLibro.setBackground(new Color(240, 240, 240));
        txtIdLibro.setToolTipText("Generado automáticamente por la base de datos");
        camposPanel.add(txtIdLibro, gbc);

        // Título
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        camposPanel.add(new JLabel("Título: *"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        txtTitulo = new JTextField();
        camposPanel.add(txtTitulo, gbc);

        // Autor
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        camposPanel.add(new JLabel("Autor (idAutor): *"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        cmbAutor = new JComboBox<>();
        camposPanel.add(cmbAutor, gbc);

        // Editorial
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        camposPanel.add(new JLabel("Editorial (idEditorial): *"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        cmbEditorial = new JComboBox<>();
        camposPanel.add(cmbEditorial, gbc);

        // Género
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        camposPanel.add(new JLabel("Género (idGenero): *"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        cmbGenero = new JComboBox<>();
        camposPanel.add(cmbGenero, gbc);

        // ISBN
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        camposPanel.add(new JLabel("ISBN: *"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        txtIsbn = new JTextField();
        camposPanel.add(txtIsbn, gbc);

        // Estatus
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        camposPanel.add(new JLabel("Estatus:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        cmbEstatus = new JComboBox<>(new String[]{"1 - Activo", "0 - Inactivo (Eliminado)"});
        camposPanel.add(cmbEstatus, gbc);

        // Sección de Foto (LONGTEXT / Base64)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        camposPanel.add(new JLabel("Foto / Portada:"), gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        JPanel panelFoto = new JPanel(new BorderLayout(5, 5));

        lblFotoPreview = new JLabel("Sin Imagen", SwingConstants.CENTER);
        lblFotoPreview.setPreferredSize(new Dimension(130, 160));
        lblFotoPreview.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        lblFotoPreview.setBackground(Color.WHITE);
        lblFotoPreview.setOpaque(true);

        JPanel panelBtnFoto = new JPanel(new GridLayout(2, 1, 4, 4));
        btnSeleccionarFoto = new JButton("Examinar...");
        btnSeleccionarFoto.addActionListener(e -> seleccionarFoto());
        btnQuitarFoto = new JButton("Quitar");
        btnQuitarFoto.addActionListener(e -> quitarFoto());
        panelBtnFoto.add(btnSeleccionarFoto);
        panelBtnFoto.add(btnQuitarFoto);

        panelFoto.add(lblFotoPreview, BorderLayout.CENTER);
        panelFoto.add(panelBtnFoto, BorderLayout.EAST);
        camposPanel.add(panelFoto, gbc);

        JScrollPane scrollCampos = new JScrollPane(camposPanel);
        scrollCampos.setBorder(null);
        panel.add(scrollCampos, BorderLayout.CENTER);

        // Panel de Botones CRUD
        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 6, 6));
        panelBotones.setBorder(new EmptyBorder(8, 8, 8, 8));

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnNuevo.addActionListener(e -> limpiarFormulario());

        btnInsertar = new JButton("Insertar");
        btnInsertar.setBackground(new Color(40, 167, 69));
        btnInsertar.setForeground(Color.WHITE);
        btnInsertar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnInsertar.addActionListener(e -> accionInsertar());

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(0, 123, 255));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.addActionListener(e -> accionActualizar());

        btnEliminarLogico = new JButton("Eliminar (Lógico)");
        btnEliminarLogico.setBackground(new Color(220, 53, 69));
        btnEliminarLogico.setForeground(Color.WHITE);
        btnEliminarLogico.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminarLogico.addActionListener(e -> accionEliminarLogico());

        btnReactivar = new JButton("Reactivar");
        btnReactivar.setBackground(new Color(23, 162, 184));
        btnReactivar.setForeground(Color.WHITE);
        btnReactivar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReactivar.addActionListener(e -> accionReactivar());

        JButton btnRefrescar = new JButton("Recargar");
        btnRefrescar.addActionListener(e -> actualizarTabla());

        panelBotones.add(btnNuevo);
        panelBotones.add(btnInsertar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminarLogico);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 195, 210), 1, true),
                        " Consulta de Libros (Base de Datos MySQL) ",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(24, 76, 120)
                )
        ));

        // Barra superior de búsqueda y filtros
        JPanel panelFiltros = new JPanel(new BorderLayout(6, 6));
        panelFiltros.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel panelBusqueda = new JPanel(new BorderLayout(6, 6));
        panelBusqueda.add(new JLabel("Buscar:"), BorderLayout.WEST);
        txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Buscar por título, ISBN o nombre del autor");
        txtBuscar.addActionListener(e -> accionBuscar());
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);

        JPanel panelBtnBusq = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> accionBuscar());
        btnLimpiarBusqueda = new JButton("Limpiar");
        btnLimpiarBusqueda.addActionListener(e -> {
            txtBuscar.setText("");
            actualizarTabla();
        });

        chkSoloActivos = new JCheckBox("Sólo activos (estatus=1)", true);
        chkSoloActivos.addActionListener(e -> actualizarTabla());

        panelBtnBusq.add(btnBuscar);
        panelBtnBusq.add(btnLimpiarBusqueda);
        panelBtnBusq.add(chkSoloActivos);

        panelFiltros.add(panelBusqueda, BorderLayout.CENTER);
        panelFiltros.add(panelBtnBusq, BorderLayout.EAST);
        panel.add(panelFiltros, BorderLayout.NORTH);

        // Definición de Columnas de la Tabla
        String[] columnas = {"ID", "Título", "Autor", "Editorial", "Género", "ISBN", "Estatus"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura para selección
            }
        };

        tablaLibros = new JTable(modeloTabla);
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.setRowHeight(26);
        tablaLibros.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaLibros.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Anchos preferidos de columnas
        tablaLibros.getColumnModel().getColumn(0).setPreferredWidth(45);
        tablaLibros.getColumnModel().getColumn(1).setPreferredWidth(210);
        tablaLibros.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaLibros.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaLibros.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaLibros.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaLibros.getColumnModel().getColumn(6).setPreferredWidth(90);

        // Renderizado personalizado para el estatus (Verde: Activo, Rojo: Inactivo)
        tablaLibros.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                String val = String.valueOf(value);
                if (val.contains("Activo") || val.equals("1")) {
                    c.setForeground(isSelected ? Color.WHITE : new Color(28, 130, 48));
                    c.setText("Activo (1)");
                } else {
                    c.setForeground(isSelected ? Color.WHITE : new Color(200, 30, 30));
                    c.setText("Inactivo (0)");
                }
                return c;
            }
        });

        // Evento de selección en la tabla
        tablaLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaLibros.getSelectedRow();
                if (fila >= 0) {
                    cargarFilaSeleccionada(fila);
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaLibros);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    private void cargarCatalogos() {
        // Cargar Autores
        cmbAutor.removeAllItems();
        List<Autor> autores = catalogoController.obtenerAutores();
        for (Autor a : autores) {
            cmbAutor.addItem(a);
        }

        // Cargar Editoriales
        cmbEditorial.removeAllItems();
        List<Editorial> editoriales = catalogoController.obtenerEditoriales();
        for (Editorial ed : editoriales) {
            cmbEditorial.addItem(ed);
        }

        // Cargar Géneros
        cmbGenero.removeAllItems();
        List<Genero> generos = catalogoController.obtenerGeneros();
        for (Genero g : generos) {
            cmbGenero.addItem(g);
        }
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        boolean soloActivos = chkSoloActivos.isSelected();
        List<Libro> libros = libroController.consultarTodos(soloActivos);

        for (Libro l : libros) {
            modeloTabla.addRow(new Object[]{
                    l.getIdLibro(),
                    l.getTitulo(),
                    l.getNombreAutor(),
                    l.getNombreEditorial(),
                    l.getNombreGenero(),
                    l.getIsbn(),
                    l.getEstatus() == 1 ? "Activo" : "Inactivo"
            });
        }

        lblTotalRegistros.setText("Registros cargados: " + libros.size() + (soloActivos ? " (Solo activos)" : " (Todos)"));
    }

    private void accionBuscar() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            actualizarTabla();
            return;
        }

        modeloTabla.setRowCount(0);
        boolean soloActivos = chkSoloActivos.isSelected();
        List<Libro> libros = libroController.buscar(criterio, soloActivos);

        for (Libro l : libros) {
            modeloTabla.addRow(new Object[]{
                    l.getIdLibro(),
                    l.getTitulo(),
                    l.getNombreAutor(),
                    l.getNombreEditorial(),
                    l.getNombreGenero(),
                    l.getIsbn(),
                    l.getEstatus() == 1 ? "Activo" : "Inactivo"
            });
        }

        lblTotalRegistros.setText("Resultados encontrados: " + libros.size());
    }

    private void cargarFilaSeleccionada(int fila) {
        int idLibro = (int) modeloTabla.getValueAt(fila, 0);
        Libro libro = libroController.consultarPorId(idLibro);
        if (libro != null) {
            libroSeleccionado = libro;
            txtIdLibro.setText(String.valueOf(libro.getIdLibro()));
            txtTitulo.setText(libro.getTitulo());
            txtIsbn.setText(libro.getIsbn());
            cmbEstatus.setSelectedIndex(libro.getEstatus() == 1 ? 0 : 1);

            // Seleccionar autor en combo
            for (int i = 0; i < cmbAutor.getItemCount(); i++) {
                if (cmbAutor.getItemAt(i).getIdAutor() == libro.getIdAutor()) {
                    cmbAutor.setSelectedIndex(i);
                    break;
                }
            }

            // Seleccionar editorial en combo
            for (int i = 0; i < cmbEditorial.getItemCount(); i++) {
                if (cmbEditorial.getItemAt(i).getIdEditorial() == libro.getIdEditorial()) {
                    cmbEditorial.setSelectedIndex(i);
                    break;
                }
            }

            // Seleccionar género en combo
            for (int i = 0; i < cmbGenero.getItemCount(); i++) {
                if (cmbGenero.getItemAt(i).getIdGenero() == libro.getIdGenero()) {
                    cmbGenero.setSelectedIndex(i);
                    break;
                }
            }

            // Cargar foto Base64
            fotoBase64Actual = libro.getFoto() != null ? libro.getFoto() : "";
            mostrarFotoPreview(fotoBase64Actual);

            btnActualizar.setEnabled(true);
            btnEliminarLogico.setEnabled(libro.getEstatus() == 1);
            btnReactivar.setEnabled(libro.getEstatus() == 0);
        }
    }

    private void accionInsertar() {
        if (!validarFormulario()) return;

        // Construir objeto del Modelo
        Autor autorSel = (Autor) cmbAutor.getSelectedItem();
        Editorial edSel = (Editorial) cmbEditorial.getSelectedItem();
        Genero genSel = (Genero) cmbGenero.getSelectedItem();
        int estatus = cmbEstatus.getSelectedIndex() == 0 ? 1 : 0;

        Libro nuevoLibro = new Libro(
                txtTitulo.getText().trim(),
                autorSel != null ? autorSel.getIdAutor() : 0,
                edSel != null ? edSel.getIdEditorial() : 0,
                genSel != null ? genSel.getIdGenero() : 0,
                txtIsbn.getText().trim(),
                fotoBase64Actual,
                estatus
        );

        // Llamada al Controlador pasando el objeto del Modelo
        boolean exito = libroController.insertar(nuevoLibro);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Libro insertado correctamente con ID: " + nuevoLibro.getIdLibro(),
                    "Inserción Exitosa", JOptionPane.INFORMATION_MESSAGE);
            actualizarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar el libro en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionActualizar() {
        if (libroSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un libro de la tabla para actualizar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarFormulario()) return;

        Autor autorSel = (Autor) cmbAutor.getSelectedItem();
        Editorial edSel = (Editorial) cmbEditorial.getSelectedItem();
        Genero genSel = (Genero) cmbGenero.getSelectedItem();
        int estatus = cmbEstatus.getSelectedIndex() == 0 ? 1 : 0;

        // Actualizar datos en el objeto del Modelo existente
        libroSeleccionado.setTitulo(txtTitulo.getText().trim());
        libroSeleccionado.setIdAutor(autorSel != null ? autorSel.getIdAutor() : 0);
        libroSeleccionado.setIdEditorial(edSel != null ? edSel.getIdEditorial() : 0);
        libroSeleccionado.setIdGenero(genSel != null ? genSel.getIdGenero() : 0);
        libroSeleccionado.setIsbn(txtIsbn.getText().trim());
        libroSeleccionado.setFoto(fotoBase64Actual);
        libroSeleccionado.setEstatus(estatus);

        // Llamada al Controlador con el objeto Modelo
        boolean exito = libroController.actualizar(libroSeleccionado);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Libro actualizado correctamente (ID: " + libroSeleccionado.getIdLibro() + ").",
                    "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            actualizarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar el libro en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminarLogico() {
        if (libroSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla para realizar la eliminación lógica.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar lógicamente el libro:\n\"" + libroSeleccionado.getTitulo() + "\" (ID: " + libroSeleccionado.getIdLibro() + ")?\n\n(El estatus cambiará a 0 en la base de datos)",
                "Confirmar Eliminación Lógica",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Llamada al Controlador con el objeto Modelo
            boolean exito = libroController.eliminarLogico(libroSeleccionado);
            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Eliminación lógica completada. El libro ahora tiene estatus = 0.",
                        "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al aplicar la eliminación lógica en la base de datos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionReactivar() {
        if (libroSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro inactivo para reactivar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = libroController.reactivar(libroSeleccionado);
        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Libro reactivado exitosamente (estatus = 1).",
                    "Reactivación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            actualizarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al reactivar el libro.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar Imagen de Portada del Libro");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de Imagen (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                // Redimensionar y convertir a Base64 para almacenar en campo foto LONGTEXT
                fotoBase64Actual = ImageUtil.fileToBase64(selectedFile, 300, 400);
                mostrarFotoPreview(fotoBase64Actual);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar la imagen: " + ex.getMessage(),
                        "Error de Imagen", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void quitarFoto() {
        fotoBase64Actual = "";
        lblFotoPreview.setIcon(null);
        lblFotoPreview.setText("Sin Imagen");
    }

    private void mostrarFotoPreview(String base64) {
        if (base64 == null || base64.trim().isEmpty()) {
            lblFotoPreview.setIcon(null);
            lblFotoPreview.setText("Sin Imagen");
        } else {
            ImageIcon icon = ImageUtil.base64ToImageIcon(base64, 130, 160);
            if (icon != null) {
                lblFotoPreview.setText("");
                lblFotoPreview.setIcon(icon);
            } else {
                lblFotoPreview.setIcon(null);
                lblFotoPreview.setText("Error en formato");
            }
        }
    }

    private boolean validarFormulario() {
        if (txtTitulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Título' es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtTitulo.requestFocus();
            return false;
        }
        if (cmbAutor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Autor.", "Validación", JOptionPane.WARNING_MESSAGE);
            cmbAutor.requestFocus();
            return false;
        }
        if (cmbEditorial.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una Editorial.", "Validación", JOptionPane.WARNING_MESSAGE);
            cmbEditorial.requestFocus();
            return false;
        }
        if (cmbGenero.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Género.", "Validación", JOptionPane.WARNING_MESSAGE);
            cmbGenero.requestFocus();
            return false;
        }
        if (txtIsbn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'ISBN' es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtIsbn.requestFocus();
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        libroSeleccionado = null;
        txtIdLibro.setText("(Nuevo)");
        txtTitulo.setText("");
        txtIsbn.setText("");
        if (cmbAutor.getItemCount() > 0) cmbAutor.setSelectedIndex(0);
        if (cmbEditorial.getItemCount() > 0) cmbEditorial.setSelectedIndex(0);
        if (cmbGenero.getItemCount() > 0) cmbGenero.setSelectedIndex(0);
        cmbEstatus.setSelectedIndex(0);
        quitarFoto();
        tablaLibros.clearSelection();

        btnInsertar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminarLogico.setEnabled(false);
        btnReactivar.setEnabled(false);
    }
}
