import {LitElement, PropertyValues} from 'lit';

export declare class ColorPickerChangeEvent extends CustomEvent<{
    value: string;
}> {
    constructor(value: string);
}
export declare class ColorPicker extends LitElement {
    static get styles(): import("lit").CSSResult[];
    value: string;
    presets?: string[];
    private toggle;
    private overlay;
    private commitValue;

    render(): import("lit").TemplateResult<1>;

    protected update(changedProperties: PropertyValues): void;

    protected firstUpdated(): void;
    private open;
    private renderOverlayContent;
    private handleColorChange;
    private handleOverlayEscape;
    private handleOverlayClose;
}
export declare class ColorPickerOverlayContent extends LitElement {
    value: string;
    presets?: string[];
    static get styles(): import("lit").CSSResult[];
    render(): import("lit").TemplateResult<1>;
    renderSwatches(): import("lit").TemplateResult<1> | undefined;

    private handlePickerChange;
    private selectPreset;
}
