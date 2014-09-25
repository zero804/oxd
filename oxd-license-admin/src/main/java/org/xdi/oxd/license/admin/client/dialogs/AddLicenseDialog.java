package org.xdi.oxd.license.admin.client.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.xdi.oxd.license.admin.client.Admin;
import org.xdi.oxd.license.admin.client.ui.DetailsPresenter;
import org.xdi.oxd.license.admin.shared.License;
import org.xdi.oxd.license.admin.shared.LicenseType;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 25/09/2014
 */

public class AddLicenseDialog implements IsWidget {

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    interface MyUiBinder extends UiBinder<DialogBox, AddLicenseDialog> {
    }

    private final DetailsPresenter detailsPresenter;

    @UiField
    DialogBox dialog;
    @UiField
    TextBox threads;
    @UiField
    ListBox type;
    @UiField
    Button okButton;
    @UiField
    Button closeButton;
    @UiField
    HTML errorMessage;

    public AddLicenseDialog(DetailsPresenter detailsPresenter) {
        uiBinder.createAndBindUi(this);
        this.detailsPresenter = detailsPresenter;
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialog.hide();
            }
        });
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onAddLicense();
            }
        });
    }

    private void onAddLicense() {
        final License license = new License();

        try {
            license.setNumberOfThreads(Integer.parseInt(threads.getValue()));
        } catch (Exception e) {
            showError("Failed to parse number of threads.");
            return;
        }
        license.setType(LicenseType.valueOf(type.getValue(type.getSelectedIndex())));
        Admin.getService().addLicense(detailsPresenter.getCustomer(), license, new AsyncCallback<License>() {
            @Override
            public void onFailure(Throwable caught) {
                showError("Failed to add license.");
            }

            @Override
            public void onSuccess(License result) {
                detailsPresenter.getCustomer().getLicenses().add(result);
                detailsPresenter.reloadLicenseTable();
                dialog.hide();
            }
        });
    }

    private void showError(String message) {
        errorMessage.setVisible(true);
        errorMessage.setHTML("<span style='color:red;'>" + message + "</span>");
    }


    @Override
    public Widget asWidget() {
        return dialog;
    }

    public void show() {
        dialog.center();
        dialog.show();
    }
}
