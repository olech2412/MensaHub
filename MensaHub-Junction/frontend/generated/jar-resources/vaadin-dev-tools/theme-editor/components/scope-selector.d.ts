import {LitElement, PropertyValues} from 'lit';
import {ComponentMetadata} from '../metadata/model';
import {ThemeScope} from '../model';

export declare class ScopeChangeEvent extends CustomEvent<{
    value: ThemeScope;
}> {
    constructor(value: ThemeScope);
}

export declare class ScopeSelector extends LitElement {
    value: ThemeScope;
    metadata?: ComponentMetadata;
    private select?;
    private selectRenderer;
    private handleValueChange;

    static get styles(): import("lit").CSSResult;

    render(): import("lit").TemplateResult<1>;

    protected update(changedProperties: PropertyValues): void;
}
