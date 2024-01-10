import {CSSResultGroup, LitElement, PropertyValues, TemplateResult} from 'lit';
import {ComponentElementMetadata, CssPropertyMetadata} from '../../metadata/model';
import {ComponentTheme, ThemePropertyValue} from '../../model';

export declare class ThemePropertyValueChangeEvent extends CustomEvent<{
    element: ComponentElementMetadata;
    property: CssPropertyMetadata;
    value: string;
}> {
    constructor(element: ComponentElementMetadata, property: CssPropertyMetadata, value: string);
}

export declare abstract class BasePropertyEditor extends LitElement {
    elementMetadata: ComponentElementMetadata;
    propertyMetadata: CssPropertyMetadata;
    theme: ComponentTheme;
    protected propertyValue?: ThemePropertyValue;
    protected value: string;

    static get styles(): CSSResultGroup;

    render(): TemplateResult<1>;

    protected update(changedProperties: PropertyValues): void;

    protected abstract renderEditor(): TemplateResult;

    protected updateValueFromTheme(): void;

    protected dispatchChange(value: string): void;
}

export declare class PropertyPresets {
    constructor(propertyMetadata?: CssPropertyMetadata);

    private _values;

    get values(): string[];

    private _rawValues;

    get rawValues(): {
        [key: string]: string;
    };

    tryMapToRawValue(presetOrValue: string): string;

    tryMapToPreset(value: string): string;

    findPreset(rawValue: string): string | undefined;
}

export declare class TextInputChangeEvent extends CustomEvent<{
    value: string;
}> {
    constructor(value: string);
}

export declare class TextInput extends LitElement {
    value: string;
    showClearButton: boolean;
    private handleInputChange;
    private handleClearClick;

    static get styles(): import("lit").CSSResult;

    render(): TemplateResult<1>;

    protected update(changedProperties: PropertyValues): void;
}
