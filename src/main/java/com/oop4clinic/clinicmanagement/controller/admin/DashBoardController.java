package com.oop4clinic.clinicmanagement.controller.admin;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.service.impl.DashBoardServiceImpl;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashBoardController implements  Initializable{
    @FXML private ScrollPane mainScrollPane;
    @FXML private Label totalPatient;
    @FXML private Label totalDoctor;
    @FXML private Label totalCompletedAP;
    @FXML private Label totalPendingAP;
    @FXML private BarChart<String, Number> weeklyChart;
    @FXML private CategoryAxis dayAxis;
    @FXML private NumberAxis countAxis;
    @FXML private TableView<AppointmentDTO> upcomingAppointmentsTable;
    @FXML private TableColumn<AppointmentDTO, String> patientNameCol;
    @FXML private TableColumn<AppointmentDTO, String> doctorNameCol;
    @FXML private TableColumn<AppointmentDTO, LocalDateTime> startTimeCol;
    @FXML private TableColumn<AppointmentDTO, AppointmentStatus> statusCol;
    @FXML private TableColumn<AppointmentDTO, String> reasonCol;

    private DashBoardServiceImpl dbService = new DashBoardServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTotalStats();
        loadWeeklyStats();
        setupAppointmentTableColumns();
        loadUpcomingAppointments();
        Platform.runLater(() -> {
            mainScrollPane.setVvalue(0.0);
        });



    }

    private void loadTotalStats() {
        setLabel(totalPatient, dbService.countPaitent());
        setLabel(totalDoctor, dbService.countDoctor());
        setLabel(totalCompletedAP, dbService.countCompletedAP());
        setLabel(totalPendingAP, dbService.countPendingAP());
    }

    private void setLabel(Label label, long value) {
        Platform.runLater(() -> label.setText(Long.toString(value)));
    }

    private void loadWeeklyStats() {
        LocalDateTime start = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();

        List<AppointmentDTO> list = dbService.getWeeklyAppointments(start, end);
        System.out.println(list.size()+"__________________________________________");
        Map<LocalDate, Integer> countMap = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            countMap.put(start.plusDays(i).toLocalDate(), 0);
        }
        for (AppointmentDTO a : list) {
            LocalDate d = a.getStartTime().toLocalDate();
            countMap.put(d, countMap.getOrDefault(d, 0) + 1);
        }

        var series = new javafx.scene.chart.XYChart.Series<String, Number>();
        countMap.forEach((date, count) ->
                series.getData().add(new javafx.scene.chart.XYChart.Data<>(
                        date.getDayOfMonth() + "/" + date.getMonthValue(), count))
        );

        weeklyChart.setCategoryGap(20);
        weeklyChart.setBarGap(0);


        Platform.runLater(() -> {
            weeklyChart.getData().clear();
            weeklyChart.getData().add(series);
        });

        NumberAxis yAxis = (NumberAxis) weeklyChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(
                Math.max(20, list.stream()
                        .collect(Collectors.groupingBy(a -> a.getStartTime().toLocalDate(), Collectors.counting()))
                        .values().stream().mapToLong(Long::longValue).max().orElse(1))
        );
//        yAxis.setUpperBound(10);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);

    }

    private void loadUpcomingAppointments() {
        List<AppointmentDTO> upcommingList = dbService.getUpcomingAppointments();

        ObservableList<AppointmentDTO> observableList;
        if (upcommingList != null) {
            observableList = FXCollections.observableArrayList(upcommingList);
        } else {
            observableList = FXCollections.observableArrayList();
        }

        upcomingAppointmentsTable.setItems(observableList);

        upcomingAppointmentsTable.setPlaceholder(new Label("Không có lịch hẹn sắp tới."));
    }

    private void setupAppointmentTableColumns() {
        patientNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPatientName())
        );

        doctorNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDoctorName())
        );

        startTimeCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getStartTime())
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        startTimeCol.setCellFactory(column -> new TableCell<AppointmentDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        statusCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getStatus())
        );

        reasonCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getReason())
        );

    }
}
