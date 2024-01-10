import {PropertyValues, TemplateResult} from 'lit';
import {BasePropertyEditor} from './base-property-editor';

export declare class RangePropertyEditor extends BasePropertyEditor {
    private selectedPresetIndex;
    private presets;
    private handleSliderInput;
    private handleSliderChange;
    private handleValueChange;
    private updateSliderValue;

    static get styles(): import("lit").CSSResultGroup[];

    protected update(changedProperties: PropertyValues): void;

    protected renderEditor(): TemplateResult;

    protected dispatchChange(value: string): void;

    protected updateValueFromTheme(): void;
}
