package cs3773.application.views.items;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cs3773.application.data.entity.Item;
import cs3773.application.data.service.ItemService;
import cs3773.application.views.MainLayout;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriUtils;

@PageTitle("Items")
@Route(value = "items/:itemID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ItemsView extends Div implements BeforeEnterObserver {

    private final String ITEM_ID = "itemID";
    private final String ITEM_EDIT_ROUTE_TEMPLATE = "items/%s/edit";

    private Grid<Item> grid = new Grid<>(Item.class, false);

    private TextField name;
    private TextField stock;
    private TextField itemType;
    private TextField price;
    private Upload imgURL;
    private Image imgURLPreview;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Item> binder;

    private Item item;

    private final ItemService itemService;

    @Autowired
    public ItemsView(ItemService itemService) {
        this.itemService = itemService;
        addClassNames("items-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("stock").setAutoWidth(true);
        grid.addColumn("itemType").setAutoWidth(true);
        grid.addColumn("price").setAutoWidth(true);
        LitRenderer<Item> imgURLRenderer = LitRenderer.<Item>of("<img style='height: 64px' src=${item.imgURL} />")
                .withProperty("imgURL", Item::getImgURL);
        grid.addColumn(imgURLRenderer).setHeader("Img URL").setWidth("68px").setFlexGrow(0);

        grid.setItems(query -> itemService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ITEM_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ItemsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Item.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(stock).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("stock");
        binder.forField(price).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("price");

        binder.bindInstanceFields(this);

        attachImageUpload(imgURL, imgURLPreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.item == null) {
                    this.item = new Item();
                }
                binder.writeBean(this.item);
                this.item.setImgURL(imgURLPreview.getSrc());

                itemService.update(this.item);
                clearForm();
                refreshGrid();
                Notification.show("Item details stored.");
                UI.getCurrent().navigate(ItemsView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the item details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> itemId = event.getRouteParameters().get(ITEM_ID).map(UUID::fromString);
        if (itemId.isPresent()) {
            Optional<Item> itemFromBackend = itemService.get(itemId.get());
            if (itemFromBackend.isPresent()) {
                populateForm(itemFromBackend.get());
            } else {
                Notification.show(String.format("The requested item was not found, ID = %s", itemId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ItemsView.class);
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
        stock = new TextField("Stock");
        itemType = new TextField("Item Type");
        price = new TextField("Price");
        Label imgURLLabel = new Label("Img URL");
        imgURLPreview = new Image();
        imgURLPreview.setWidth("100%");
        imgURL = new Upload();
        imgURL.getStyle().set("box-sizing", "border-box");
        imgURL.getElement().appendChild(imgURLPreview.getElement());
        Component[] fields = new Component[]{name, stock, itemType, price, imgURLLabel, imgURL};

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

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            String mimeType = e.getMIMEType();
            String base64ImageData = Base64.getEncoder().encodeToString(uploadBuffer.toByteArray());
            String dataUrl = "data:" + mimeType + ";base64,"
                    + UriUtils.encodeQuery(base64ImageData, StandardCharsets.UTF_8);
            upload.getElement().setPropertyJson("files", Json.createArray());
            preview.setSrc(dataUrl);
            uploadBuffer.reset();
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Item value) {
        this.item = value;
        binder.readBean(this.item);
        this.imgURLPreview.setVisible(value != null);
        if (value == null) {
            this.imgURLPreview.setSrc("");
        } else {
            this.imgURLPreview.setSrc(value.getImgURL());
        }

    }
}
