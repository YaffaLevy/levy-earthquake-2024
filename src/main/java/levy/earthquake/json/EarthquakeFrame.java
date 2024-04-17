package levy.earthquake.json;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import levy.earthquake.EarthquakeService;
import levy.earthquake.EarthquakeServiceFactory;
import levy.earthquake.json.FeatureCollection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class EarthquakeFrame extends JFrame {
    private JList<String> jlist = new JList<>();
    private Disposable disposable;
    private Feature[] features;

    public EarthquakeFrame() {
        setSize(400, 600);
        setTitle("");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JRadioButton oneHourRadioButton = new JRadioButton("One Hour");
        JRadioButton thirtyDaysRadioButton = new JRadioButton("Thirty Days");

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(oneHourRadioButton);
        radioButtonGroup.add(thirtyDaysRadioButton);

        JPanel radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioButtonPanel.add(oneHourRadioButton);
        radioButtonPanel.add(thirtyDaysRadioButton);
        add(radioButtonPanel, BorderLayout.NORTH);
        add(new JScrollPane(jlist), BorderLayout.CENTER);

        EarthquakeService service = new EarthquakeServiceFactory().getService();

        oneHourRadioButton.addActionListener(e -> {
            if (oneHourRadioButton.isSelected()) {
                fetchEarthquakeData(service.oneHour());
            }
        });
        thirtyDaysRadioButton.addActionListener(e -> {
            if (thirtyDaysRadioButton.isSelected()) {
                fetchEarthquakeData(service.significantLast30Days());
            }
        });
        jlist.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = jlist.getSelectedIndex();
                    if (selectedIndex != -1) {
                        Feature selectedFeature = features[selectedIndex];
                        double latitude = selectedFeature.geometry.coordinates[1];
                        double longitude = selectedFeature.geometry.coordinates[0];
                        openGoogleMaps(latitude, longitude);
                    }
                }
            }
        });
    }

    private void fetchEarthquakeData(Single<FeatureCollection> data) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        disposable = data.subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(
                        this::handleResponse,
                        Throwable::printStackTrace);
    }

    private void handleResponse(FeatureCollection response) {
        features = response.features;

        DefaultListModel<String> listModel = new DefaultListModel<>();
        Arrays.stream(response.features)
                .forEach(feature -> {
                    String item = feature.properties.mag + " " + feature.properties.place;
                    listModel.addElement(item);
                });
        jlist.setModel(listModel);
    }

    private void openGoogleMaps(double latitude, double longitude) {
        String url = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EarthquakeFrame().setVisible(true);
    }
}
