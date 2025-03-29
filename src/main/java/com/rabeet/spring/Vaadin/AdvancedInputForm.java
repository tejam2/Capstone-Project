package com.rabeet.spring.Vaadin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rabeet.spring.Data.DataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rabeet.spring.Utilities.*;

public class AdvancedInputForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(AdvancedInputForm.class);

    private ComboBox<String> omics = new ComboBox<>("Omics Layer");
    private ComboBox<String> fraction = new ComboBox<>("Fraction");
    private MultiselectComboBox<String> algorithms = new MultiselectComboBox<>();
    private Button go = new Button("Compare Algorithms");
    private H3 clusterLabel = new H3("Cluster settings");
    private TextField numberOfClusters = new TextField("Number of clusters (2 - 10)");
    private TextField minClusterSize = new TextField("Minimum cluster size (0.1 - 0.5)");
    private TextField minClusterSizeIncrement = new TextField("Minimum cluster size increment (0.01 - 0.10)");

    private Image image = new Image();
    private Anchor downloadLink = new Anchor();
    private Dialog dialog = new Dialog(image, downloadLink);

    private Cloudinary cloudinary;

    public AdvancedInputForm(Cloudinary cloudinary) {

        this.cloudinary = cloudinary;

        go.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        go.setWidthFull();
        go.addClickListener(event -> analyze());

        omics.setItems(DataService.PROTEIN, DataService.LIPID, "BOTH"); omics.setRequired(true); omics.setWidthFull();
        fraction.setItems(DataService.GCP_FRACTION, DataService.GCM_FRACTION, "BOTH"); fraction.setRequired(true); fraction.setWidthFull();

        algorithms.setLabel("Algorithms to compare (2 or more)"); algorithms.setItems(DataService.ALGORITHMS); algorithms.setRequired(true);

        numberOfClusters.setValue("5"); numberOfClusters.setSizeFull(); numberOfClusters.setRequired(true);
        minClusterSize.setValue(".10"); minClusterSize.setSizeFull(); minClusterSize.setRequired(true);
        minClusterSizeIncrement.setValue(".01"); minClusterSizeIncrement.setSizeFull(); minClusterSizeIncrement.setRequired(true);
        VerticalLayout cluster = new VerticalLayout(clusterLabel, numberOfClusters, minClusterSize, minClusterSizeIncrement);

        dialog.setWidth("1100px");
        dialog.setHeight("750px");
        image.setSizeFull();
        downloadLink.setText("Download full size image");

        add(new VerticalLayout(omics, fraction, algorithms, new VerticalLayout(go), cluster));
    }

    private void analyze() {
        if (!checkInputs()) return;

        String omics = this.omics.getValue();
        if (omics.equals("BOTH")) omics = "both";
        else if (omics.equals(DataService.PROTEIN)) omics = "protein";
        else omics = "lipids";

        String fraction = this.fraction.getValue();
        if (fraction.equals("BOTH")) fraction = "both";
        else if (fraction.equals(DataService.GCP_FRACTION)) fraction = "gcp";
        else fraction = "gcm";

        List<String> algorithms = new ArrayList<>(this.algorithms.getSelectedItems());
        String numberOfClusters = this.numberOfClusters.getValue();
        String minClusterSize = this.minClusterSize.getValue();
        String minClusterSizeIncrement = this.minClusterSizeIncrement.getValue();

        String filename = omics+"_"+System.currentTimeMillis()+".png";
        constructAndCall(omics, fraction, algorithms, numberOfClusters, minClusterSize, minClusterSizeIncrement, filename);

        // Move generated image to the cloud
        try {
            Map uploadResult = cloudinary.uploader().upload(filename, ObjectUtils.emptyMap());
            String publicId = (String) uploadResult.get("public_id");
            String url = cloudinary.url().generate(publicId + ".png");
            image.setSrc(url);
            downloadLink.setHref(url);
            downloadLink.setTarget("_blank");
        } catch (IOException e) {
            logger.error("Error uploading to Cloudinary", e);
        }

        dialog.open();
    }

    private void constructAndCall(String omics, String fraction, List<String> algorithms, String numberOfClusters, String minClusterSize, String minClusterSizeIncrement, String filename) {

        String algorthmsToRun = Strings.join((algorithms.stream().map(x -> x.substring(0, x.indexOf(":"))).collect(Collectors.toList())), ' ');

        // --eval command line argument must be the last option. It is the only option required
        StringBuilder command = new StringBuilder("python /app/scripts/gci_cc.py")
                .append(" --omic ").append(omics)
                .append(" --fraction ").append(fraction)
                .append(" --clusters ").append(numberOfClusters)
                .append(" --mcs ").append(minClusterSize)
                .append(" --mcsi ").append(minClusterSizeIncrement)
                .append(" --filename ").append(filename)
                .append(" --eval ").append(algorthmsToRun);

        logger.info("Running: {}", command);

        String callerScriptName = "caller_"+System.currentTimeMillis()+".sh";
        writeToFile(callerScriptName, command.toString(), false);

        try {
            executeSystemCommand("bash " + callerScriptName);
            executeSystemCommand("rm " + callerScriptName);
        } catch (IOException | InterruptedException e) {
            logger.error("Error running script", e);
        }
    }

    private boolean checkInputs() {
        if (omics.isEmpty()) {
            omics.setInvalid(true);
            return false;
        } else if (fraction.isEmpty()) {
            fraction.setInvalid(true);
            return false;
        } else if (algorithms.getSelectedItems().size() < 2) {
            algorithms.setInvalid(true);
            return false;
        } else if (numberOfClusters.isEmpty()) {
            numberOfClusters.setInvalid(true);
            return false;
        } else if (minClusterSize.isEmpty()) {
            minClusterSize.setInvalid(true);
            return false;
        } else if (minClusterSizeIncrement.isEmpty()) {
            minClusterSizeIncrement.setInvalid(true);
            return false;
        }

        return true;
    }

}
