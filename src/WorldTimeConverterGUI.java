import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorldTimeConverterGUI extends JFrame {
    private final JComboBox<String> regionComboBox;
    private final JTextArea countryTextArea;
    private final Map<String, ArrayList<String>> countriesByRegion;
    private final JProgressBar loadingProgressBar;

    public WorldTimeConverterGUI() {
        setTitle("World Time Converter");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        countriesByRegion = new HashMap<>();
        initializeCountriesByRegion();

        regionComboBox = new JComboBox<>(countriesByRegion.keySet().toArray(new String[0]));
        JButton filterButton = new JButton("Filtrar");
        countryTextArea = new JTextArea();
        countryTextArea.setEditable(false);

        JPanel topPanel = new JPanel();
        topPanel.add(regionComboBox);
        topPanel.add(filterButton);

        JScrollPane scrollPane = new JScrollPane(countryTextArea);

        loadingProgressBar = new JProgressBar();
        loadingProgressBar.setIndeterminate(true); // Indicador de progresso indeterminado
        loadingProgressBar.setVisible(false); // Inicialmente invisível
        add(loadingProgressBar, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRegion = (String) regionComboBox.getSelectedItem();
                if (selectedRegion != null) {
                    ArrayList<String> countries = countriesByRegion.get(selectedRegion);
                    displayCountries(countries);
                }
            }
        });
    }

    private void initializeCountriesByRegion() {
        countriesByRegion.put("América do Sul", new ArrayList<>(Arrays.asList(
                "America/Argentina/Buenos_Aires",
                "America/Sao_Paulo",
                "America/Santiago",
                "America/Montevideo",
                "America/Bogota",
                "America/Lima",
                "America/La_Paz",
                "America/Caracas"

        )));
        countriesByRegion.put("América Central", new ArrayList<>(Arrays.asList(
                "America/Costa_Rica"
        )));
        countriesByRegion.put("América do Norte", new ArrayList<>(Arrays.asList(
                "America/Los_Angeles",
                "America/Indiana/Indianapolis",
                "America/Chicago",
                "America/Denver",
                "America/Detroit",
                "America/Kentucky/Louisville",
                "America/Mexico_City",
                "America/New_York",
                "America/North_Dakota/Center",
                "America/Vancouver",
                "America/Toronto"
        )));

        countriesByRegion.put("Europa", new ArrayList<>(Arrays.asList(
                "Europe/Lisbon",
                "Europe/London",
                "Europe/Luxembourg",
                "Europe/Kyiv",
                "Europe/Moscow",
                "Europe/Oslo",
                "Europe/Paris",
                "Europe/Prague",
                "Europe/Dublin",
                "Europe/Rome",
                "Europe/Berlin",
                "Europe/Madrid",
                "Europe/Zurich",
                "Europe/Amsterdam"
        )));

        countriesByRegion.put("Asia", new ArrayList<>(Arrays.asList(

                "Asia/Jakarta",
                "Asia/Manila",
                "Asia/Bangkok",
                "Asia/Kuala_Lumpur",
                "Asia/Ho_Chi_Minh",
                "Asia/Tokyo",
                "Asia/Seoul"
        )));

        countriesByRegion.put("Oceania", new ArrayList<>(Arrays.asList(
                "Australia/Melbourne",
                "Australia/Sydney",
                "Pacific/Auckland"

        )));







    }

    private String getCurrentTimeForZone(String timeZoneId) {
        try {
            // Adiciona um atraso de 1 segundo (1000 milissegundos) entre as chamadas à API
            Thread.sleep(2000);

            String apiKey = "SUA_CHAVE_API_AQUI";
            String apiUrl = "http://api.timezonedb.com/v2.1/get-time-zone";
            String urlString = apiUrl + "?key=" + apiKey + "&format=json&by=zone&zone=" + timeZoneId;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String status = jsonResponse.getString("status");

            if (status.equals("OK")) {
                long timestamp = jsonResponse.getLong("timestamp");
                LocalDateTime currentTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return currentTime.format(formatter);
            } else {
                return "Erro ao obter o horário.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao obter o horário.";
        }
    }


    private void displayCountries(ArrayList<String> countries) {
        loadingProgressBar.setVisible(true);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                StringBuilder sb = new StringBuilder();
                for (String timeZoneId : countries) {
                    String[] parts = timeZoneId.split("/");
                    String countryName = parts[parts.length - 1].replace("_", " ");
                    String currentTime = getCurrentTimeForZone(timeZoneId);
                    sb.append(countryName).append(" [").append(currentTime).append("]\n");
                }
                countryTextArea.setText(sb.toString());
                return null;
            }

            @Override
            protected void done() {
                loadingProgressBar.setVisible(false);
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WorldTimeConverterGUI gui = new WorldTimeConverterGUI();
            gui.setVisible(true);
        });
    }
}

