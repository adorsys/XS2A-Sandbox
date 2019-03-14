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
  selector: '[sbMin]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => MinValidatorDirective),
      multi: true,
    },
  ],
})
export class MinValidatorDirective implements Validator {
  private _validator: ValidatorFn;
  @Input()
  public set sbMin(value: string) {
    this._validator = Validators.min(parseInt(value, 10));
  }

  public validate(control: AbstractControl): { [key: string]: any } {
    return this._validator(control);
  }
}
