import {LitElement, PropertyValues} from 'lit';
import './editors/base-property-editor';

export declare class ClassNameChangeEvent extends CustomEvent<{
    value: string;
}> {
    constructor(value: string);
}

export declare class ClassNameEditor extends LitElement {
    className: string;
    private editedClassName;
    private invalid;
    private handleInputChange;

    static get styles(): import("lit").CSSResult[];

    render(): import("lit").TemplateResult<1>;

    protected update(changedProperties: PropertyValues): void;
}
