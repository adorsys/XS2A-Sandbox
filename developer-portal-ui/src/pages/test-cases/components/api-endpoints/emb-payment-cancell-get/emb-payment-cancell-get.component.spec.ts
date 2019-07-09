import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentCancellGetComponent } from './emb-payment-cancell-get.component';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';

describe('EmbPaymentCancellGetComponent', () => {
  let component: EmbPaymentCancellGetComponent;
  let fixture: ComponentFixture<EmbPaymentCancellGetComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: ''
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() paymentServiceFlag: boolean;
    @Input() paymentProductFlag: boolean;
    @Input() paymentIdFlag: boolean;
    @Input() variablePathEnd: string;
    @Input() cancellationIdFlag: boolean;
  }

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EmbPaymentCancellGetComponent,
        TranslatePipe,
        MockPlayWithDataComponent,
        LineCommandComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentCancellGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});

