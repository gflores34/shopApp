package cs3773.application.views.discountcodes;

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
import cs3773.application.data.entity.DiscountCode;
import cs3773.application.data.service.DiscountCodeService;
import cs3773.application.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Discount Codes")
@Route(value = "discountCodes/:discountCodeID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class DiscountCodesView extends Div implements BeforeEnterObserver {

    private final String DISCOUNTCODE_ID = "discountCodeID";
    private final String DISCOUNTCODE_EDIT_ROUTE_TEMPLATE = "discountCodes/%s/edit";

    private Grid<DiscountCode> grid = new Grid<>(DiscountCode.class, false);

    private TextField code;
    private TextField percentOff;
    private TextField maxDollarAmount;
    private TextField status;
    private DatePicker expirationDate;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<DiscountCode> binder;

    private DiscountCode discountCode;

    private final DiscountCodeService discountCodeService;

    @Autowired
    public DiscountCodesView(DiscountCodeService discountCodeService) {
        this.discountCodeService = discountCodeService;
        addClassNames("discount-codes-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("code").setAutoWidth(true);
        grid.addColumn("percentOff").setAutoWidth(true);
        grid.addColumn("maxDollarAmount").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);
        grid.addColumn("expirationDate").setAutoWidth(true);
        grid.setItems(query -> discountCodeService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DISCOUNTCODE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DiscountCodesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(DiscountCode.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(code).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("code");
        binder.forField(percentOff).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("percentOff");
        binder.forField(maxDollarAmount).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("maxDollarAmount");
        binder.forField(status).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("status");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.discountCode == null) {
                    this.discountCode = new DiscountCode();
                }
                binder.writeBean(this.discountCode);

                discountCodeService.update(this.discountCode);
                clearForm();
                refreshGrid();
                Notification.show("DiscountCode details stored.");
                UI.getCurrent().navigate(DiscountCodesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the discountCode details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> discountCodeId = event.getRouteParameters().get(DISCOUNTCODE_ID).map(UUID::fromString);
        if (discountCodeId.isPresent()) {
            Optional<DiscountCode> discountCodeFromBackend = discountCodeService.get(discountCodeId.get());
            if (discountCodeFromBackend.isPresent()) {
                populateForm(discountCodeFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested discountCode was not found, ID = %s", discountCodeId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DiscountCodesView.class);
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
        code = new TextField("Code");
        percentOff = new TextField("Percent Off");
        maxDollarAmount = new TextField("Max Dollar Amount");
        status = new TextField("Status");
        expirationDate = new DatePicker("Expiration Date");
        Component[] fields = new Component[]{code, percentOff, maxDollarAmount, status, expirationDate};

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

    private void populateForm(DiscountCode value) {
        this.discountCode = value;
        binder.readBean(this.discountCode);

    }
}
