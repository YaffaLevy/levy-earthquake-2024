package levy.earthquake.json;


import hu.akarnokd.rxjava3.swing.SwingSchedulers;
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

    public EarthquakeFrame() {
        setSize(400, 600);
        setTitle("");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(jlist, BorderLayout.CENTER);

        EarthquakeService service = new EarthquakeServiceFactory().getService();
        Disposable disposable = service.oneHour()
                .subscribeOn(Schedulers.io())
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




