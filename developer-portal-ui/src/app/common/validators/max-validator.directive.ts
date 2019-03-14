import { Directive, forwardRef, Input } from '@angular/core';
import {
  NG_VALIDATORS,
  Validator,
  FormControl,
  ValidatorFn,
  Validators,
  AbstractControl,
} from '@angular/forms';

@Directive({
  selector: '[sbMax]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => MaxValidatorDirective),
      multi: true,
    },
  ],
})
export class MaxValidatorDirective implements Validator {
  private _validator: ValidatorFn;
  @Input()
  public set sbMax(value: string) {
    this._validator = Validators.max(parseInt(value, 10));
  }

  public validate(control: AbstractControl): { [key: string]: any } {
    return this._validator(control);
  }
}
