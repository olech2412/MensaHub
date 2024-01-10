import {PropertyValues, TemplateResult} from 'lit';
import './color-picker';
import {BasePropertyEditor} from './base-property-editor';

export declare class ColorPropertyEditor extends BasePropertyEditor {
    private presets;
    private handleInputChange;
    private handleColorPickerChange;
    private handleColorPickerCommit;
    private handleColorPickerCancel;

    static get styles(): import("lit").CSSResultGroup[];

    protected update(changedProperties: PropertyValues): void;

    protected renderEditor(): TemplateResult;

    protected dispatchChange(value: string): void;

    protected updateValueFromTheme(): void;
}
