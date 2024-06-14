import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

class Faal {
    @JsonProperty("Poem")
    private String poem;

    @JsonProperty("Interpretation")
    private String interpretation;

    public String getPoem() {
        return poem;
    }

    public void setPoem(String poem) {
        this.poem = poem;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }
}

class HttpClientHelper {
    public static String sendGet(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }
}

class JsonHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Faal parseFaal(String json) throws IOException {
        return objectMapper.readValue(json, Faal.class);
    }
}

public class FalApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fal App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);


            JPanel welcomePanel = new JPanel();
            welcomePanel.setLayout(new BorderLayout());
            JLabel welcomeLabel = new JLabel("نیت کنید...");
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

            JPanel displayPanel = new JPanel();
            displayPanel.setLayout(new BorderLayout());

            JTextArea poemTextArea = new JTextArea();
            poemTextArea.setLineWrap(true);
            poemTextArea.setWrapStyleWord(true);
            poemTextArea.setEditable(false);

            JTextArea interpretationTextArea = new JTextArea();
            interpretationTextArea.setLineWrap(true);
            interpretationTextArea.setWrapStyleWord(true);
            interpretationTextArea.setEditable(false);
            interpretationTextArea.setPreferredSize(new Dimension(400, 100));

            displayPanel.add(new JScrollPane(poemTextArea), BorderLayout.CENTER);
            displayPanel.add(new JScrollPane(interpretationTextArea), BorderLayout.SOUTH);

            JButton getFaalButton = new JButton("دریافت فال");
            getFaalButton.addActionListener(e -> {
                try {
                    String json = HttpClientHelper.sendGet("https://faal.spclashers.workers.dev/api");
                    Faal faal = JsonHelper.parseFaal(json);
                    poemTextArea.setText(faal.getPoem());
                    interpretationTextArea.setText(faal.getInterpretation());
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(frame, "Error fetching Fal: " + ioException.getMessage());
                }
            });

            frame.setLayout(new BorderLayout());
            frame.add(welcomePanel, BorderLayout.NORTH);
            frame.add(displayPanel, BorderLayout.CENTER);
            frame.add(getFaalButton, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}
