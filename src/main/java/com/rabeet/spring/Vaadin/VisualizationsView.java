package com.rabeet.spring.Vaadin;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "visualizations", layout = RouterLayout.class)
public class VisualizationsView extends VerticalLayout {

    public static final String LOC = "images//visualization_images//";

    public VisualizationsView() {

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(img("v1.png"), img("v2.png"), img("v3.png"), img("v4.png"), img("v5.png"), img("v6.png"));
    }

    private Image img(String name) {
        Image image = new Image();
        image.setSrc(LOC + name);
        image.setMaxWidth("80%");
        return image;
    }

}
