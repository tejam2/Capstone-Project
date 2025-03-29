package com.rabeet.spring.Vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = RouterLayout.class)
@PageTitle("GC Insights App")
public class HomePageView extends VerticalLayout {

    private Dialog tutorialDialog = new Dialog();

    public HomePageView() {
        HorizontalLayout layout = new HorizontalLayout();
        HorizontalLayout[] inners = new HorizontalLayout[3];

        for (int i = 0; i < inners.length; i++) {
            inners[i] = new HorizontalLayout();
            inners[i].setMinWidth("25%");
            inners[i].setHeight("100px");
            inners[i].setWidthFull();
            inners[i].getStyle().set("border", "1px solid #9E9E9E");
            inners[i].setJustifyContentMode(JustifyContentMode.EVENLY);
            inners[i].setDefaultVerticalComponentAlignment(Alignment.CENTER);
            inners[i].setPadding(true);

            layout.add(inners[i]);
        }

        H3[] h3Label = new H3[3];
        h3Label[0] = new H3("83,196 measurements");
        h3Label[0].getStyle().set("color", "#FFFFFF");
        inners[0].add(new HorizontalLayout(h3Label[0]));

        h3Label[1] = new H3("2,771 features");
        h3Label[1].getStyle().set("color", "#FFFFFF");
        inners[1].add(new HorizontalLayout(h3Label[1]));

        h3Label[2] = new H3("5 developmental stages");
        h3Label[2].getStyle().set("color", "#FFFFFF");
        inners[2].add(new HorizontalLayout(h3Label[2]));

        Icon icon = new Icon(VaadinIcon.ABACUS);
        icon.setSize("70px");
        inners[0].add(icon);
        icon = new Icon(VaadinIcon.AUTOMATION); icon.setSize("70px");
        inners[1].add(icon);
        icon = new Icon(VaadinIcon.CHART); icon.setSize("70px");
        inners[2].add(icon);
        inners[0].getStyle().set("background-color", "#605ca8");
        inners[1].getStyle().set("background-color", "#00a65a");
        inners[2].getStyle().set("background-color", "#f39c12");
        inners[0].getStyle().set("color", "#FFFFFF");
        inners[1].getStyle().set("color", "#FFFFFF");
        inners[2].getStyle().set("color", "#FFFFFF");

        String text = "Neuronal Growth Cone Multi-Omics Insight (GC-Insights)\n" +
                "\n" +
                "A web-based tool to navigate proteomics and lipidomics data\n" +
                "\n" +
                "GC-Insights offers tools that enable the analysis and output of high resolution mass spectrometric data of growth cone proteins and lipids generated after extensive fractionation. Please click for tutorial and overview here. \n" +
                "\n" +
                "Study workflow is detailed below\n";

        layout.setWidthFull();
        setPadding(true);
        layout.getStyle().set("flex-shrink", "0");
        add(layout);
        add(new H3("Neuronal Growth Cone Multi-Omics Insight (GC-Insights)"));
        add(new Paragraph("A web-based tool to navigate proteomics and lipidomics data"));
        add(new Paragraph("GC-Insights offers tools that enable the analysis and output of high resolution mass spectrometric data of growth cone proteins and lipids generated after extensive fractionation. Please click for tutorial and overview below."));

        Image image = new Image();
        image.setSrc("images//home_page.png");
        image.setMaxWidth("100%");

        // Tutorial
        tutorialDialog.add(new VideoEmbed());
        tutorialDialog.setWidth("1100px");
        tutorialDialog.setHeight("700px");
        HorizontalLayout buttonHolder = new HorizontalLayout(playButton());
        buttonHolder.setWidthFull();
        buttonHolder.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonHolder.getStyle().set("flex-shrink", "0");

        add(buttonHolder);
        add("Study workflow is detailed below");
        add(image);

    }

    private Button playButton() {
        Image thumbnail = new Image("images//video//play.png", "Play tutorial");
        thumbnail.setWidth("380px");
        thumbnail.setHeight("210px");
        Button play = new Button(thumbnail);
        play.addClickListener(e -> tutorialDialog.open());
        play.setWidth("380px");
        play.setHeight("240px");
        play.getStyle().set("cursor", "pointer");
        return play;
    }

}