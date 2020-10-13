package view;

import domain.MonthlyData;
import domain.SectorName;
import domain.YearName;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.json.simple.parser.ParseException;

import static util.SectorDataUtil.getSectorsDataMap;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DisplayData {

    private ComboBox<SectorName> sectorComboBox;
    private ComboBox<YearName> yearlyComboBox;
    private ListView<MonthlyData> listView;
    private final Map<SectorName, Map<YearName, List<MonthlyData>>> sectorDataMap;
    private final ObservableList<SectorName> sectorNames;
    private Map<YearName, List<MonthlyData>> yearlyMap;

    public DisplayData() throws IOException, ParseException {

        sectorDataMap = getSectorsDataMap();
        sectorNames = FXCollections.observableArrayList(sectorDataMap.keySet());
        listView = new ListView<>();
        initSetUp();
    }

    private void initSetUp() {

        setUpYearComboBox();
        setUpSectorComboBox();
        yearCbListener();
        handleYearlyCbPrompt();
        setStringConverterToListView();
    }

    private class SectorNameStringConverter extends StringConverter<SectorName> {

        private final String separator = ", Symbol: ";

        @Override
        public String toString(SectorName object) {

            if( object == null )
                return null;
            else
                return object.name().substring(0,1).toUpperCase() + object.name().substring(1).toLowerCase() + separator + object.getSector();
        }

        @Override
        public SectorName fromString(String string) {

            String sectorName = string.split(separator)[1];

            for (SectorName sn : SectorName.values())
                if( sn.getSector().equals(sectorName))
                    return sn;
            return null;
        }
    }

    private void setStringConverterToListView() {

        listView.setCellFactory(lv->{
            TextFieldListCell<MonthlyData> cell = new TextFieldListCell<>();
            cellDataStringConversion(cell);
            return cell ;
        });
    }

    private void cellDataStringConversion(TextFieldListCell<MonthlyData> cell) {

        cell.setConverter(new StringConverter<MonthlyData>() {

            @Override
            public String toString(MonthlyData object) {

                if(object == null)
                    return null;
                else
                    return  object.getDate()+"\nOpening price: " + object.getOpen()+
                            "\nClosing price: " +object.getClose()+"\n\n\n";
            }

            @Override
            public MonthlyData fromString(String string) {
                return null;
            }

        });
    }

    private void setUpSectorComboBox() {

        sectorComboBox = new ComboBox<>();
        sectorComboBox.getItems().addAll( sortSectors() );
        sectorComboBox.setPromptText("---Select a Sector---");
        sectorComboBox.setConverter(new SectorNameStringConverter());
        sectorCbListener();
    }

    private ObservableList<SectorName> sortSectors() {
        return sectorNames.sorted(new Comparator<SectorName>() {
            @Override
            public int compare(SectorName o1, SectorName o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
    }

    private void sectorCbListener() {

        sectorComboBox.valueProperty().addListener(new ChangeListener<SectorName>() {
            @Override
            public void changed(ObservableValue<? extends SectorName> observable, SectorName oldValue, SectorName newValue ) {

                if( newValue != null ) {
                    listView.setVisible(false);
                    addSelectedSectorData( newValue );
                }
            }
        });
    }

    private void addSelectedSectorData( SectorName selectedSector ) {

        addYearlyDataToComboBox(selectedSector);
        yearlyMap = sectorDataMap.get(selectedSector);
    }

    private void setUpYearComboBox() {

        yearlyComboBox = new ComboBox<>();
        yearlyComboBox.setPromptText("---Select a Year---");
        yearlyComboBox.setVisible(true);
    }

    private void handleYearlyCbPrompt() {

        yearlyComboBox.setButtonCell(new ListCell<>(){

            @Override
            protected  void updateItem(YearName yearName, boolean empty){
                super.updateItem(yearName, empty);
                if( empty || yearName == null )
                    setText("---Select a Year---");
            }
        });
    }

    private void addYearlyDataToComboBox( SectorName selectedSector ) {

        yearlyComboBox.getItems().clear();
        ObservableList<YearName> yearRanges = FXCollections.observableArrayList( sectorDataMap.get(selectedSector).keySet() );
        yearlyComboBox.getItems().addAll( sortYears(yearRanges));
    }

    private ObservableList<YearName> sortYears(ObservableList<YearName> yearRanges) {
        return yearRanges.sorted(new Comparator<YearName>() {
            @Override
            public int compare(YearName o1, YearName o2) {
                return Integer.compare(o1.getYear(), o2.getYear());
            }
        });
    }

    private void  yearCbListener() {

        yearlyComboBox.valueProperty().addListener(new ChangeListener<YearName>() {
            @Override
            public void changed(ObservableValue<? extends YearName> observable, YearName oldValue, YearName newValue) {

                if (newValue != null) {
                    listView.getItems().clear();
                    listView.getItems().addAll(sortMonths(yearlyMap.get(newValue)));
                    listView.setVisible(true);
                }
            }
        });
    }

    private List<MonthlyData> sortMonths(List<MonthlyData> monthlyDataList) {

        monthlyDataList.sort(new Comparator<MonthlyData>() {
            @Override
            public int compare(MonthlyData o1, MonthlyData o2) {
                return compareMonths(o1, o2);
            }
        });
        return monthlyDataList;
    }

    private int compareMonths(MonthlyData o1, MonthlyData o2) {

        SimpleDateFormat format = new SimpleDateFormat("MMM", Locale.US );

        try {
            return format.parse(o1.getDate()).compareTo(format.parse(o2.getDate()));

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ComboBox<SectorName> getSectorComboBox() {
        return sectorComboBox;
    }

    public ComboBox<YearName> getYearlyComboBox() {
        return yearlyComboBox;
    }

    public ListView<MonthlyData> getListView() {
        return listView;
    }
}