package levy.earthquake.json;


import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import levy.earthquake.EarthquakeService;
import levy.earthquake.EarthquakeServiceFactory;
import levy.earthquake.json.FeatureCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class EarthquakeFrame extends JFrame {
    private JList<String> jlist = new JList<>();
    private Disposable disposable;

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
    }

    private void fetchEarthquakeData(Single<FeatureCollection> data) {

        if ( disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        disposable = data.subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(
                        this::handleResponse,
                        Throwable::printStackTrace);
    }
    private void handleResponse(FeatureCollection response) {
        String[] listData = Arrays.stream(response.features)
                .map(feature -> feature.properties.mag + " " + feature.properties.place)
                .toList()
                .toArray(new String[0]);
        jlist.setListData(listData);
    }
    public static void main(String[] args) {
        new EarthquakeFrame().setVisible(true);

    }
}




