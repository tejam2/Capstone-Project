package com.rabeet.spring.Vaadin;

import com.rabeet.spring.Chart.ChartService;
import com.rabeet.spring.Data.DataService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.JFreeChartWrapper;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.*;

public class InputForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(InputForm.class);
    private QueryView queryView;
    private DataService dataService;
    private ChartService chartService;

    private MultiselectComboBox<String> geneName = new MultiselectComboBox<>();
    private ComboBox<String> omics = new ComboBox<>("Omics Layer");
    private ComboBox<String> fraction = new ComboBox<>("Fraction");
    private MultiselectComboBox<String> age = new MultiselectComboBox<>();
    private Button go = new Button("Analyze");
    private Button download = new Button("Export");
    private Select<String> select = new Select<>("CSV", "PDF");


    private Set<String> geneResult = new LinkedHashSet<>();

    public InputForm(QueryView queryView, DataService dataService, ChartService chartService) {
        this.queryView = queryView;
        this.dataService = dataService;
        this.chartService = chartService;

        geneName.setLabel("Gene Name"); geneName.setRequired(true); geneName.setWidthFull();
        omics.setItems(DataService.PROTEIN, DataService.LIPID); omics.setRequired(true); omics.addValueChangeListener(c -> updateSelection()); omics.setWidthFull();
        fraction.setItems(DataService.GCP_FRACTION, DataService.GCM_FRACTION); fraction.setRequired(true); fraction.setWidthFull();
        age.setLabel("Developmental stage"); age.setItems(DataService.AGES); age.setRequired(true); age.setWidthFull();
        geneName.addSelectionListener( e -> {
            geneResult.addAll(e.getAddedSelection());
            geneResult.removeAll(e.getRemovedSelection());
        });

        go.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        go.setWidthFull();
        go.addClickListener(event -> analyze());

        download.setWidth("70%");
        download.setWidthFull();
        download.addClickListener(e -> download());

        select.setPlaceholder("Format");
        select.setWidth("30%");

        HorizontalLayout export = new HorizontalLayout();
        export.expand(download);
        export.add(download, select);
        export.setWidthFull();
        VerticalLayout buttons = new VerticalLayout(go, export);
        buttons.setWidthFull();
        add(new VerticalLayout(omics, geneName, fraction, age, buttons));

        testItems();

    }

    private void analyze() {
        if (!checkInputs()) return;
        String omics = this.omics.getValue();
        String fraction = this.fraction.getValue();
        List<String> age = new ArrayList<>(this.age.getSelectedItems()); Collections.sort(age);
        List<String> geneResult = new ArrayList<>(this.geneResult);

        JFreeChartWrapper chartWrapper = chartService.genChart(omics, geneResult, fraction,  age);
        queryView.updateGraph(chartWrapper);
    }

    private void download() {
        if (!checkInputs()) return;
        if (this.select.isEmpty()) {
            this.select.setInvalid(true);
            return;
        }
        String omics = this.omics.getValue();
        String fraction = this.fraction.getValue();
        List<String> age = new ArrayList<>(this.age.getSelectedItems()); Collections.sort(age);
        List<String> names = new ArrayList<>(this.geneResult);
        String select = this.select.getValue();

        String ageResult = StringUtils.join(age.toArray(), ",");
        String namesResult = StringUtils.join(names.toArray(), ",");

        StringBuilder query = select.equals("CSV") ? new StringBuilder("/exportCSV?") : new StringBuilder("/exportPDF?");
        query.append("omics=")
            .append(omics)
            .append("&fraction=")
            .append(fraction)
            .append("&age=")
            .append(ageResult)
            .append("&names=")
            .append(namesResult);

        UI.getCurrent().getPage().executeJavaScript("window.open(\""+query.toString()+"\", \"_self\");");
    }

    private boolean checkInputs() {
        if (this.omics.isEmpty()) {
            this.omics.setInvalid(true);
            return false;
        } else if (this.geneName.isEmpty()) {
            this.geneName.setInvalid(true);
            return false;
        } else if (this.fraction.isEmpty()) {
            this.fraction.setInvalid(true);
            return false;
        } else if (this.age.isEmpty()) {
            this.age.setInvalid(true);
            return false;
        }
        return true;
    }

    private void updateSelection() {
        String selection = omics.getValue();
        if (StringUtils.isEmpty(selection)) {
            omics.setInvalid(true);
        } else {
            this.geneName.setLabel(selection.equals(DataService.PROTEIN) ? "Gene Name" : "Class Name");
            geneName.setItems(dataService.getAllNames(selection));
        }
    }

    private void testItems() {
        this.omics.setValue(DataService.PROTEIN);
        this.geneName.setValue(new HashSet<>(Arrays.asList("Thy1")));
        this.fraction.setValue(DataService.GCP_FRACTION);
        this.age.setValue(new HashSet<>(Arrays.asList("E18")));
    }

}