package cs3773.application.views.sales;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cs3773.application.data.entity.Sale;
import cs3773.application.data.service.SaleService;
import cs3773.application.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Sales")
@Route(value = "sales/:saleID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class SalesView extends Div implements BeforeEnterObserver {

    private final String SALE_ID = "saleID";
    private final String SALE_EDIT_ROUTE_TEMPLATE = "sales/%s/edit";

    private Grid<Sale> grid = new Grid<>(Sale.class, false);

    private TextField itemId;
    private TextField percentOff;
    private DatePicker startDate;
    private DatePicker expirationDate;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Sale> binder;

    private Sale sale;

    private final SaleService saleService;

    @Autowired
    public SalesView(SaleService saleService) {
        this.saleService = saleService;
        addClassNames("sales-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("itemId").setAutoWidth(true);
        grid.addColumn("percentOff").setAutoWidth(true);
        grid.addColumn("startDate").setAutoWidth(true);
        grid.addColumn("expirationDate").setAutoWidth(true);
        grid.setItems(query -> saleService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SALE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SalesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Sale.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(itemId).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("itemId");
        binder.forField(percentOff).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("percentOff");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sale == null) {
                    this.sale = new Sale();
                }
                binder.writeBean(this.sale);

                saleService.update(this.sale);
                clearForm();
                refreshGrid();
                Notification.show("Sale details stored.");
                UI.getCurrent().navigate(SalesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the sale details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> saleId = event.getRouteParameters().get(SALE_ID).map(UUID::fromString);
        if (saleId.isPresent()) {
            Optional<Sale> saleFromBackend = saleService.get(saleId.get());
            if (saleFromBackend.isPresent()) {
                populateForm(saleFromBackend.get());
            } else {
                Notification.show(String.format("The requested sale was not found, ID = %s", saleId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SalesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        itemId = new TextField("Item Id");
        percentOff = new TextField("Percent Off");
        startDate = new DatePicker("Start Date");
        expirationDate = new DatePicker("Expiration Date");
        Component[] fields = new Component[]{itemId, percentOff, startDate, expirationDate};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Sale value) {
        this.sale = value;
        binder.readBean(this.sale);

    }
}
