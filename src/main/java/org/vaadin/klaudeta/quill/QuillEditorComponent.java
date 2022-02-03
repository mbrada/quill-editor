package org.vaadin.klaudeta.quill;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.DisabledUpdateMode;
import com.vaadin.flow.function.SerializableConsumer;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * A custom RichText editor component for Flow using Quill library.
 */
@Tag("quill-editor")
@NpmPackage(value = "lit-element", version = "^2.2.1")
@NpmPackage(value = "lit-html", version = "^1.1.2")
@NpmPackage(value = "quill", version = "^1.3.6")
@JsModule("./quilleditor.js")
@CssImport("./quill.snow.css")
@CssImport("./custom-quillEditor.css")
public class QuillEditorComponent extends Component implements HasComponents, QuillToolbarConfigurator, HasSize, QuillValueChangeNotifier, HasStyle {

    public static final String EMPTY_VALUE = "<p><br></p>";

    private Div editor = new Div();
    private String htmlContent = "";

    public QuillEditorComponent() {
        initEditor();
    }

    /**
     * Initialize the frontend editor component applying all toolbar configuration
     * done through the toolbar properties assignable through the interface {@link QuillToolbarConfigurator}.
     * If no property have been configured through the {@link QuillToolbarConfigurator}
     * methods, the editor will be initialized including all the features of the toolbar.
     */
    public void initEditor() {
        this.removeAll();
        editor = new Div();
        editor.setId("editor-quill");
        add(editor);
        this.getElement().executeJs("$0.initEditor($1)", this, editor.getElement());
    }

    @ClientCallable(DisabledUpdateMode.ALWAYS)
    private void setHtml(String htmlContent) {
        final String noNewLineCharacter = htmlContent.replaceAll("\n", "");
        final String oldContent = this.htmlContent;
        if (!Objects.equals(oldContent, noNewLineCharacter)) {
            this.htmlContent = noNewLineCharacter;
            this.fireEvent(new QuillValueChangeNotifier.QuillValueChangeEvent(this, noNewLineCharacter));
        }
    }

    /**
     * Returns the editor's content in HTML format.
     *
     * @return a {@link String} instance of the current editor content
     * in HTML format.
     */
    public String getHtmlContent() {
        return htmlContent;
    }

    /**
     * Sets the editor content as an HTML format. Any unsupported
     * HTML content will be ignored and removed from the editor.
     *
     * @param htmlContent a {@link String} instance of the content in HTML format
     *                    that should appear on the editor.
     */
    public void setHtmlContent(String htmlContent) {
        final String oldContent = this.htmlContent;
        if (!Objects.equals(oldContent, htmlContent)) {
            this.htmlContent = htmlContent;
            runBeforeClientResponse(ui -> {
                editor.getElement().executeJs("$0.setHtml($1)", this, htmlContent);
            });
        }
    }

    public void format(final String name, final String value) {
        this.getElement().executeJs("$0.format($1,$2)", this, name, value);
    }

    /**
     * @param index
     * @param text
     * @param source "api", "user" or "silent"
     */
    public void insertText(int index, String text, String source) {
        this.getElement().executeJs("$0.insertText($1,$2,$3)", this, index, text, source);
    }

    public void appendText(String text, String source) {
        this.getElement().executeJs("$0.appendText($1,$2)", this, text, source);
    }

    private void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

    private HashMap<String, QuillKeyEventHandler> keyHandlersMap = new HashMap<>();

    @ClientCallable(DisabledUpdateMode.ONLY_WHEN_ENABLED)
    private void keyHandler(final String hash) {
        final QuillKeyEventHandler handler = keyHandlersMap.get(hash);

        if (handler != null) {
            handler.handle();
        }
    }

    public void removeBinding(String uuid) {
        keyHandlersMap.remove(uuid);
    }

    /**
     * Return binding UUID
     *
     * @param key
     * @param ctrl
     * @param shift
     * @param alt
     * @param eventHandler
     * @return
     */
    public String addBinding(String key, boolean ctrl, boolean shift, boolean alt, QuillKeyEventHandler eventHandler) {
        final String uuid = UUID.randomUUID().toString();

        keyHandlersMap.put(uuid, eventHandler);

        runBeforeClientResponse(ui -> {
            editor.getElement().executeJs("$0.addKeyEventHandler($1, $2, $3, $4, $5)", this, key, ctrl, shift, alt, uuid);
        });

        return uuid;
    }

}
