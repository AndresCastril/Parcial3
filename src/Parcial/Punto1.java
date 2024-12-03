package Parcial;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Punto1 {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Personas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(null);

        JLabel lblIdentificacion = new JLabel("Identificación:");
        lblIdentificacion.setBounds(10, 20, 100, 25);
        frame.add(lblIdentificacion);

        JTextField campoIdentificacion = new JTextField();
        campoIdentificacion.setBounds(120, 20, 165, 25);
        frame.add(campoIdentificacion);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(10, 60, 100, 25);
        frame.add(lblNombre);

        JTextField campoNombre = new JTextField();
        campoNombre.setBounds(120, 60, 165, 25);
        frame.add(campoNombre);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setBounds(10, 100, 100, 25);
        frame.add(lblCorreo);

        JTextField campoCorreo = new JTextField();
        campoCorreo.setBounds(120, 100, 165, 25);
        frame.add(campoCorreo);

        JButton btnGuardarJson = new JButton("Guardar JSON");
        btnGuardarJson.setBounds(10, 140, 120, 25);
        frame.add(btnGuardarJson);

        JButton btnLeerJson = new JButton("Leer JSON");
        btnLeerJson.setBounds(140, 140, 120, 25);
        frame.add(btnLeerJson);

        JButton btnGuardarTxt = new JButton("Guardar TXT");
        btnGuardarTxt.setBounds(270, 140, 120, 25);
        frame.add(btnGuardarTxt);

        JButton btnLeerTxt = new JButton("Leer TXT");
        btnLeerTxt.setBounds(400, 140, 120, 25);
        frame.add(btnLeerTxt);

        JButton btnGuardarXml = new JButton("Guardar XML");
        btnGuardarXml.setBounds(530, 140, 120, 25);
        frame.add(btnGuardarXml);

        JButton btnLeerXml = new JButton("Leer XML");
        btnLeerXml.setBounds(660, 140, 120, 25);
        frame.add(btnLeerXml);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Identificación");
        model.addColumn("Nombre");
        model.addColumn("Correo");

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 200, 860, 450);
        frame.add(scrollPane);

        btnGuardarJson.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                guardarPersonaJson(campoIdentificacion.getText(), campoNombre.getText(), campoCorreo.getText());
                actualizarTablaJson(model);
                limpiarCampos(campoIdentificacion, campoNombre, campoCorreo);
            }
        });

        btnLeerJson.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actualizarTablaJson(model);
            }
        });

        btnGuardarTxt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                guardarPersonaTxt(campoIdentificacion.getText(), campoNombre.getText(), campoCorreo.getText());
                actualizarTablaTxt(model);
                limpiarCampos(campoIdentificacion, campoNombre, campoCorreo);
            }
        });

        btnLeerTxt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actualizarTablaTxt(model);
            }
        });

        btnGuardarXml.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                guardarPersonaXml(campoIdentificacion.getText(), campoNombre.getText(), campoCorreo.getText());
                actualizarTablaXml(model);
                limpiarCampos(campoIdentificacion, campoNombre, campoCorreo);
            }
        });

        btnLeerXml.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actualizarTablaXml(model);
            }
        });

        frame.setVisible(true);
    }

    private static void guardarPersonaJson(String id, String nombre, String correo) {
        try {
            ValidarCorreo CorreoValidar = new ValidarCorreo();
            CorreoValidar.validar(correo);
            List<JsonObject> personas = cargarPersonasJson();
            JsonObject nuevaPersona = new JsonObject();
            nuevaPersona.addProperty("identificacion", id);
            nuevaPersona.addProperty("nombre", nombre);
            nuevaPersona.addProperty("correo", correo);
            personas.add(nuevaPersona);
            try (Writer writer = new FileWriter("personas.json")) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(personas, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch(ExcepcionCorreo e) {
            System.out.println(e.getMessage());
        }

    }

    private static void guardarPersonaTxt(String id, String nombre, String correo) {
        try{
            ValidarCorreo CorreoValidar = new ValidarCorreo();
            CorreoValidar.validar(correo);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("personas.txt", true))) {
            
            writer.write(id + "," + nombre + "," + correo);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }catch(ExcepcionCorreo e) {
            System.out.println(e.getMessage());
        }
    }

    private static void guardarPersonaXml(String id, String nombre, String correo) {
        try{
            ValidarCorreo CorreoValidar = new ValidarCorreo();
            CorreoValidar.validar(correo);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;

            File file = new File("personas.xml");
            Element root;

            if (file.exists()) {
                document = builder.parse(file);
                root = document.getDocumentElement();
            } else {
                document = builder.newDocument();
                root = document.createElement("Personas");
                document.appendChild(root);
            }

            Element persona = document.createElement("Persona");
            root.appendChild(persona);

            Element identificacion = document.createElement("Identificacion");
            identificacion.appendChild(document.createTextNode(id));
            persona.appendChild(identificacion);

            Element nombreTag = document.createElement("Nombre");
            nombreTag.appendChild(document.createTextNode(nombre));
            persona.appendChild(nombreTag);

            Element correoTag = document.createElement("Correo");
            correoTag.appendChild(document.createTextNode(correo));
            persona.appendChild(correoTag);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }catch(ExcepcionCorreo e) {
            System.out.println(e.getMessage());
        }
    }


    private static List<JsonObject> cargarPersonasJson() {
        List<JsonObject> personas = new ArrayList<>();
        File file = new File("personas.json");

        if (file.exists()) {
            try (Reader reader = new FileReader("personas.json")) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<JsonObject>>() {
                }.getType();
                personas = gson.fromJson(reader, listType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return personas;
    }

    private static void actualizarTablaJson(DefaultTableModel model) {
        model.setRowCount(0);
        List<JsonObject> personas = cargarPersonasJson();

        for (JsonObject persona : personas) {
            String id = persona.get("identificacion").getAsString();
            String nombre = persona.get("nombre").getAsString();
            String correo = persona.get("correo").getAsString();
            model.addRow(new Object[]{id, nombre, correo});
        }
    }

    private static List<String[]> cargarPersonasTxt() {
        List<String[]> personas = new ArrayList<>();
        File file = new File("personas.txt");

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader("personas.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 3) {
                        personas.add(data);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return personas;
    }

    private static void actualizarTablaTxt(DefaultTableModel model) {
        model.setRowCount(0); // Limpiar la tabla
        List<String[]> personas = cargarPersonasTxt();

        for (String[] persona : personas) {
            model.addRow(new Object[]{persona[0], persona[1], persona[2]});
        }
    }

    private static List<String[]> cargarPersonasXml() {
        List<String[]> personas = new ArrayList<>();
        File file = new File("personas.xml");

        if (file.exists()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                document.getDocumentElement().normalize();

                NodeList nodeList = document.getElementsByTagName("Persona");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String id = element.getElementsByTagName("Identificacion").item(0).getTextContent();
                        String nombre = element.getElementsByTagName("Nombre").item(0).getTextContent();
                        String correo = element.getElementsByTagName("Correo").item(0).getTextContent();
                        personas.add(new String[]{id, nombre, correo});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return personas;
    }

    private static void actualizarTablaXml(DefaultTableModel model) {
        model.setRowCount(0);
        List<String[]> personas = cargarPersonasXml();

        for (String[] persona : personas) {
            model.addRow(new Object[]{persona[0], persona[1], persona[2]});
        }
    }

    private static void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText(""); 
        }
    }

}
