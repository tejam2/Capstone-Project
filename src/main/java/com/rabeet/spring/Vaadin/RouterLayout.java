package com.rabeet.spring.Vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import de.kaesdingeling.hybridmenu.HybridMenu;
import de.kaesdingeling.hybridmenu.components.*;
import de.kaesdingeling.hybridmenu.data.MenuConfig;
import de.kaesdingeling.hybridmenu.data.interfaces.MenuComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

@Push
@BodySize(height = "100vh", width = "100vw")
public class RouterLayout extends HybridMenu {

	private static final Logger logger = LoggerFactory.getLogger(RouterLayout.class);

	private static final String TITLE = "<b>GCInsights</b> v1.6.3";
	private static final String LINK = "https://umiamihealth.org/bascom-palmer-eye-institute/research/laboratory-research/miami-integrative-metabolomics-research-center";
	private Map<String, MenuComponent> components;

	@Override
	public boolean init(VaadinSession vaadinSession, UI ui) {
		withConfig(MenuConfig.builder().build());

		TopMenu topMenu = getTopMenu();

		topMenu.add(HMButton.get()
				.withDescription("Collapse").withIcon(VaadinIcon.ALIGN_JUSTIFY).withClickListener(e -> getLeftMenu().setVisible(!getLeftMenu().isVisible())));

		components = new LinkedHashMap<>();

		components.put("title", HMLabel.get().withCaption(TITLE));
		components.put("Dashboard", HMButton.get().withCaption("Home").withIcon(VaadinIcon.HOME).withNavigateTo(HomePageView.class));
		components.put("Interactive Query", HMButton.get().withCaption("Interactive Query").withIcon(VaadinIcon.SEARCH).withNavigateTo(QueryView.class));
		components.put("Visualizations", HMButton.get().withCaption("Visualizations").withIcon(VaadinIcon.COMPILE).withNavigateTo(VisualizationsView.class));
		components.put("Advanced Analysis", HMButton.get().withCaption("Advanced Analysis").withIcon(VaadinIcon.CLUSTER).withNavigateTo(AdvancedAnalysisView.class));
		components.put("Deep ML", HMButton.get().withCaption("Deep ML").withIcon(VaadinIcon.MAGIC));

		LeftMenu leftMenu = getLeftMenu();
		for (MenuComponent menuComponent : components.values()) {
			if (menuComponent instanceof HMLabel)
				leftMenu.add((HMLabel)menuComponent);
			else if (menuComponent instanceof HMButton)
				leftMenu.add((HMButton)menuComponent);
		}

		leftMenu.add(HMLabel.get().withCaption(""));

		HMSubMenu demoSettings = leftMenu.add(HMSubMenu.get()
				.withCaption("Settings")
				.withIcon(VaadinIcon.COGS));

		demoSettings.add(HMButton.get()
				.withCaption("White Theme")
				.withIcon(VaadinIcon.PALETE)
				.withClickListener(e -> UI.getCurrent().getElement().setAttribute("theme", "")));

		demoSettings.add(HMButton.get()
				.withCaption("Dark Theme")
				.withIcon(VaadinIcon.PALETE)
				.withClickListener(e -> UI.getCurrent().getElement().setAttribute("theme", Lumo.DARK)));

//		demoSettings.add(HMButton.get()
//				.withCaption("Minimal")
//				.withIcon(VaadinIcon.COG)
//				.withClickListener(e -> minimize()));

		leftMenu.add(HMButton.get().withCaption("About").withIcon(VaadinIcon.AIRPLANE).withClickListener(e -> UI.getCurrent().getPage().executeJavaScript("window.open(\""+LINK+"\", \"_blank\");")));

		return true; // build menu
	}

	private void minimize() {
		for (Map.Entry<String, MenuComponent> entry : components.entrySet()) {
			MenuComponent v = entry.getValue();
			String s = entry.getKey();
			if (v instanceof HMButton) {
				if (((HMButton) v).getText().isEmpty())
					((HMButton) v).setText(s);
				else
					((HMButton) v).setText("");
			}

		}
		getLeftMenu().toggleSize();
	}
}
