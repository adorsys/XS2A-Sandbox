import {AbstractControl, FormArray, ValidationErrors} from "@angular/forms";

export class CheckboxValidators {
    static checkboxShouldBeSelected(control: AbstractControl): ValidationErrors {


        const length = (control.value as any).length;
        let valid: boolean = true;
        for (let i = 0; i < length; i++) {
            console.log('########', control.value);

            if (control.value[i].value === true) {
                return null;
            }
        }

        return {checkboxShouldBeSelected: true};
    }
}
