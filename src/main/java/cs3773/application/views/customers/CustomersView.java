package cs3773.application.views.customers;

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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cs3773.application.data.entity.Customer;
import cs3773.application.data.service.CustomerService;
import cs3773.application.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Customers")
@Route(value = "customers/:customerID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class CustomersView extends Div implements BeforeEnterObserver {

    private final String CUSTOMER_ID = "customerID";
    private final String CUSTOMER_EDIT_ROUTE_TEMPLATE = "customers/%s/edit";

    private Grid<Customer> grid = new Grid<>(Customer.class, false);

    private TextField name;
    private TextField state;
    private TextField birthDate;
    private DatePicker createDate;
    private TextField gender;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Customer> binder;

    private Customer customer;

    private final CustomerService customerService;

    @Autowired
    public CustomersView(CustomerService customerService) {
        this.customerService = customerService;
        addClassNames("customers-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("state").setAutoWidth(true);
        grid.addColumn("birthDate").setAutoWidth(true);
        grid.addColumn("createDate").setAutoWidth(true);
        grid.addColumn("gender").setAutoWidth(true);
        grid.setItems(query -> customerService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CUSTOMER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CustomersView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Customer.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.customer == null) {
                    this.customer = new Customer();
                }
                binder.writeBean(this.customer);

                customerService.update(this.customer);
                clearForm();
                refreshGrid();
                Notification.show("Customer details stored.");
                UI.getCurrent().navigate(CustomersView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the customer details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> customerId = event.getRouteParameters().get(CUSTOMER_ID).map(UUID::fromString);
        if (customerId.isPresent()) {
            Optional<Customer> customerFromBackend = customerService.get(customerId.get());
            if (customerFromBackend.isPresent()) {
                populateForm(customerFromBackend.get());
            } else {
                Notification.show(String.format("The requested customer was not found, ID = %s", customerId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CustomersView.class);
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
        name = new TextField("Name");
        state = new TextField("State");
        birthDate = new TextField("Birth Date");
        createDate = new DatePicker("Create Date");
        gender = new TextField("Gender");
        Component[] fields = new Component[]{name, state, birthDate, createDate, gender};

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

    private void populateForm(Customer value) {
        this.customer = value;
        binder.readBean(this.customer);

    }
}
