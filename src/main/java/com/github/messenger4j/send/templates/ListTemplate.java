package com.github.messenger4j.send.templates;

import com.github.messenger4j.internal.Assert;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.buttons.UrlButton;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Jan Zarnikov
 * @since 0.6.2
 */
public final class ListTemplate extends Template {

    @SerializedName("top_element_style")
    private final TopElementStyle topElementStyle;
    private final List<Button> buttons;
    private final List<Element> elements;

    public static Builder newBuilder(TopElementStyle topElementStyle) {
        return new Builder(topElementStyle);
    }

    private ListTemplate(Builder builder) {
        super(TemplateType.LIST);
        this.topElementStyle = builder.topElementStyle;
        this.buttons = builder.buttons;
        this.elements = builder.elements;
    }

    public enum TopElementStyle {
        @SerializedName("large")
        LARGE,

        @SerializedName("compact")
        COMPACT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ListTemplate that = (ListTemplate) o;
        return topElementStyle == that.topElementStyle &&
                Objects.equals(buttons, that.buttons) &&
                Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), topElementStyle, buttons, elements);
    }

    @Override
    public String toString() {
        return "ListTemplate{" +
                "topElementStyle=" + topElementStyle +
                ", buttons=" + buttons +
                ", elements=" + elements +
                '}';
    }

    /**
     * @since 0.6.2
     */
    public static final class Builder {

        private static final int BUTTONS_LIMIT = 1;
        private final TopElementStyle topElementStyle;

        private List<Button> buttons;
        private List<Element> elements;

        public Builder(TopElementStyle topElementStyle) {
            this.topElementStyle = topElementStyle;
        }

        public Builder buttons(List<Button> buttons) {
            Assert.notNullOrEmpty(buttons, "buttons");
            Assert.sizeNotGreaterThan(buttons, BUTTONS_LIMIT, "buttons");
            this.buttons = buttons;
            return this;
        }

        public Element.ListBuilder addElements() {
            return new Element.ListBuilder(this);
        }

        private Builder elements(List<Element> elements) {
            this.elements = elements;
            return this;
        }

        public ListTemplate build() {
            Assert.notNullOrEmpty(this.elements, "elements");
            if (topElementStyle == TopElementStyle.LARGE) {
                Assert.notNullOrBlank(elements.get(0).imageUrl, "imageUrl");
            }
            return new ListTemplate(this);
        }
    }

    /**
     * @since 0.6.2
     */
    public static final class Element {

        private final String title;
        private final String subtitle;
        @SerializedName("image_url")
        private final String imageUrl;
        private final List<Button> buttons;
        @SerializedName("default_action")
        private final DefaultAction defaultAction;

        private Element(Builder builder) {
            this.title = builder.title;
            this.subtitle = builder.subtitle;
            this.imageUrl = builder.imageUrl;
            this.buttons = builder.buttons;
            this.defaultAction = builder.defaultAction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Element element = (Element) o;
            return Objects.equals(title, element.title) &&
                    Objects.equals(subtitle, element.subtitle) &&
                    Objects.equals(imageUrl, element.imageUrl) &&
                    Objects.equals(buttons, element.buttons) &&
                    Objects.equals(defaultAction, element.defaultAction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, subtitle, imageUrl, buttons, defaultAction);
        }

        @Override
        public String toString() {
            return "Element{" +
                    "title='" + title + '\'' +
                    ", subtitle='" + subtitle + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", buttons=" + buttons +
                    ", defaultAction=" + defaultAction +
                    '}';
        }

        /**
         * @since 0.6.2
         */
        public static final class ListBuilder {

            private final List<Element> elements;
            private final ListTemplate.Builder listTemplateBuilder;

            private static final int MIN_ELEMENTS = 2;
            private static final int MAX_ELEMENTS = 4;

            private ListBuilder(ListTemplate.Builder listTemplateBuilder) {
                this.listTemplateBuilder = listTemplateBuilder;
                this.elements = new ArrayList<>();
            }

            public Builder addElement(String title) {
                return new Builder(title, this);
            }

            public ListTemplate.Builder done() {
                Assert.sizeNotGreaterThan(this.elements, MAX_ELEMENTS, "elements", IllegalStateException.class);
                Assert.sizeNotLessThan(this.elements, MIN_ELEMENTS, "elements", IllegalStateException.class);
                return this.listTemplateBuilder.elements(Collections.unmodifiableList(new ArrayList<>(this.elements)));
            }

            private void addElementToList(Element element) {
                this.elements.add(element);
            }
        }

        /**
         * @since 0.6.2
         */
        public static final class Builder {

            private static final int BUTTONS_LIMIT = 1;
            private static final int TITLE_CHARACTER_LIMIT = 80;
            private static final int SUBTITLE_CHARACTER_LIMIT = 80;

            private final String title;
            private final ListBuilder listBuilder;
            private String subtitle;
            private String imageUrl;
            private List<Button> buttons;
            private DefaultAction defaultAction;

            public Builder(String title, ListBuilder listBuilder) {
                Assert.lengthNotGreaterThan(title, TITLE_CHARACTER_LIMIT, "title");
                this.title = title;
                this.listBuilder = listBuilder;
            }

            public Builder subtitle(String subtitle) {
                Assert.lengthNotGreaterThan(title, SUBTITLE_CHARACTER_LIMIT, "subtitle");
                this.subtitle = subtitle;
                return this;
            }

            public Builder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public Builder buttons(List<Button> buttons) {
                Assert.notNullOrEmpty(buttons, "buttons");
                Assert.sizeNotGreaterThan(buttons, BUTTONS_LIMIT, "buttons", IllegalStateException.class);
                this.buttons = buttons;
                return this;
            }

            public DefaultAction.Builder defaultAction(String url) {
                return new DefaultAction.Builder(url, this);
            }

            private Builder defaulAction(DefaultAction defaultAction) {
                this.defaultAction = defaultAction;
                return this;
            }

            public ListBuilder build() {
                listBuilder.addElementToList(new Element(this));
                return listBuilder;
            }

        }

        /**
         * @since 0.6.2
         */
        public static final class DefaultAction {

            private final Button.ButtonType type;
            private final String url;
            @SerializedName("webview_height_ratio")
            private final UrlButton.WebviewHeightRatio webviewHeightRatio;

            public DefaultAction(Builder builder) {
                this.url = builder.url;
                this.webviewHeightRatio = builder.webviewHeightRatio;
                this.type = Button.ButtonType.URL;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                DefaultAction that = (DefaultAction) o;
                return type == that.type &&
                        Objects.equals(url, that.url) &&
                        webviewHeightRatio == that.webviewHeightRatio;
            }

            @Override
            public int hashCode() {
                return Objects.hash(type, url, webviewHeightRatio);
            }

            @Override
            public String toString() {
                return "DefaultAction{" +
                        "type=" + type +
                        ", url='" + url + '\'' +
                        ", webviewHeightRatio=" + webviewHeightRatio +
                        '}';
            }

            /**
             * @since 0.6.2
             */
            public static final class Builder {
                private final String url;
                private final Element.Builder elementBuilder;
                private UrlButton.WebviewHeightRatio webviewHeightRatio;

                public Builder(String url, Element.Builder builder) {
                    this.url = url;
                    elementBuilder = builder;
                }

                public Builder webviewHeightRatio(UrlButton.WebviewHeightRatio webviewHeightRatio) {
                    this.webviewHeightRatio = webviewHeightRatio;
                    return this;
                }

                public Element.Builder build() {
                    return elementBuilder.defaulAction(new DefaultAction(this));
                }
            }

        }
    }
}
