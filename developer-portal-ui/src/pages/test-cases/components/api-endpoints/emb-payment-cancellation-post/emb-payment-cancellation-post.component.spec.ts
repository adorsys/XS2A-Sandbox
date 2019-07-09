import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentCancellationPostComponent } from './emb-payment-cancellation-post.component';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';
import {LineCommandComponent} from "../../../../../custom-elements/line-command/line-command.component";

describe('EmbPaymentCancellationPostComponent', () => {
  let component: EmbPaymentCancellationPostComponent;
  let fixture: ComponentFixture<EmbPaymentCancellationPostComponent>;

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
    @Input() fieldsToCopy: string[];
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
        EmbPaymentCancellationPostComponent,
        MockPlayWithDataComponent,
        TranslatePipe,
        LineCommandComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentCancellationPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
