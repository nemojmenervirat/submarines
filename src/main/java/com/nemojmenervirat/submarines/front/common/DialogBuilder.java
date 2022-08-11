package com.nemojmenervirat.submarines.front.common;

import java.util.function.Consumer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DialogBuilder {

  private Dialog dialog = new Dialog();
  private DialogBuilderEvent event = new DialogBuilderEvent(dialog);
  private Button buttonOk = new Button("OK", VaadinIcon.CHECK.create());
  private Button buttonCancel = new Button("Cancel", VaadinIcon.CLOSE.create());
  private Button buttonClose = new Button("Close", VaadinIcon.CLOSE.create());
  private Button buttonDelete = new Button("Remove", VaadinIcon.TRASH.create());

  private DialogBuilder(String title, Component content) {
    dialog.add(content);
    dialog.setMinWidth("30%");
    dialog.setHeaderTitle(title);
    dialog.getFooter().add(buttonDelete, buttonOk, buttonCancel, buttonClose);

    buttonOk.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    buttonDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
    buttonOk.addClassName("ms-auto");
    buttonClose.addClassName("ms-auto");

    buttonOk.setVisible(false);
    buttonDelete.setVisible(false);
    buttonCancel.setVisible(false);
    buttonClose.setVisible(false);
  }

  public static DialogBuilder from(String title, Component content) {
    return new DialogBuilder(title, content);
  }

  public static DialogBuilder from(String title, String content) {
    return new DialogBuilder(title, new Paragraph(content));
  }

  public DialogBuilder draggable() {
    dialog.setDraggable(true);
    return this;
  }

  public DialogBuilder modal() {
    dialog.setModal(true);
    dialog.setCloseOnEsc(false);
    dialog.setCloseOnOutsideClick(false);
    return this;
  }

  public DialogBuilder withOk(Runnable okAction) {
    return withOk(e -> {
      okAction.run();
      e.close();
    });
  }

  public DialogBuilder withOk(Consumer<DialogBuilderEvent> okAction) {
    buttonOk.setVisible(true);
    buttonOk.addClickListener(e -> okAction.accept(event));
    return this;
  }

  public DialogBuilder okText(String text) {
    buttonOk.setText(text);
    return this;
  }

  public DialogBuilder withDelete(Consumer<DialogBuilderEvent> deleteAction) {
    buttonDelete.setVisible(true);
    buttonDelete.addClickListener(e -> deleteAction.accept(event));
    return this;
  }

  public DialogBuilder withCancel() {
    return withCancel(e -> e.close());
  }

  public DialogBuilder withCancel(Runnable cancelAction) {
    return withCancel(e -> {
      cancelAction.run();
      e.close();
    });
  }

  public DialogBuilder cancelText(String text) {
    buttonCancel.setText(text);
    return this;
  }

  public DialogBuilder withCancel(Consumer<DialogBuilderEvent> cancelAction) {
    buttonCancel.setVisible(true);
    buttonCancel.addClickListener(e -> cancelAction.accept(event));
    return this;
  }

  public DialogBuilder withClose() {
    return withClose(() -> dialog.close());
  }

  public DialogBuilder withClose(Runnable closeAction) {
    buttonClose.setVisible(true);
    buttonClose.addClickListener(e -> closeAction.run());
    return this;
  }

  public Dialog open() {
    dialog.setOpened(true);
    return dialog;
  }

  public static class DialogBuilderEvent {

    private final Dialog dialog;

    public DialogBuilderEvent(Dialog dialog) {
      this.dialog = dialog;
    }

    public void close() {
      dialog.close();
    }

  }

}
