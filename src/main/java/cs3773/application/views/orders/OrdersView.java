package cs3773.application.views.orders;

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
import cs3773.application.data.entity.Orders;
import cs3773.application.data.service.OrdersService;
import cs3773.application.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Orders")
@Route(value = "orders/:ordersID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class OrdersView extends Div implements BeforeEnterObserver {

    private final String ORDERS_ID = "ordersID";
    private final String ORDERS_EDIT_ROUTE_TEMPLATE = "orders/%s/edit";

    private Grid<Orders> grid = new Grid<>(Orders.class, false);

    private TextField custId;
    private TextField totalPrice;
    private TextField status;
    private DatePicker orderDate;
    private DatePicker deliveryDate;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Orders> binder;

    private Orders orders;

    private final OrdersService ordersService;

    @Autowired
    public OrdersView(OrdersService ordersService) {
        this.ordersService = ordersService;
        addClassNames("orders-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("custId").setAutoWidth(true);
        grid.addColumn("totalPrice").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);
        grid.addColumn("discountCode").setAutoWidth(true);
        grid.addColumn("orderDate").setAutoWidth(true);
        grid.addColumn("deliveryDate").setAutoWidth(true);
        grid.setItems(query -> ordersService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ORDERS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(OrdersView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Orders.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(custId).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("custId");
        binder.forField(totalPrice).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("totalPrice");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.orders == null) {
                    this.orders = new Orders();
                }
                binder.writeBean(this.orders);

                ordersService.update(this.orders);
                clearForm();
                refreshGrid();
                Notification.show("Orders details stored.");
                UI.getCurrent().navigate(OrdersView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the orders details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> ordersId = event.getRouteParameters().get(ORDERS_ID).map(UUID::fromString);
        if (ordersId.isPresent()) {
            Optional<Orders> ordersFromBackend = ordersService.get(ordersId.get());
            if (ordersFromBackend.isPresent()) {
                populateForm(ordersFromBackend.get());
            } else {
                Notification.show(String.format("The requested orders was not found, ID = %s", ordersId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(OrdersView.class);
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
        custId = new TextField("Cust Id");
        totalPrice = new TextField("Total Price");
        status = new TextField("Status");
        orderDate = new DatePicker("Order Date");
        deliveryDate = new DatePicker("Delivery Date");
        Component[] fields = new Component[]{custId, totalPrice, status, orderDate, deliveryDate};

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

    private void populateForm(Orders value) {
        this.orders = value;
        binder.readBean(this.orders);

    }
}
