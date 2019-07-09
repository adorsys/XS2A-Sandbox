import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentInitAuthPostComponent } from './emb-payment-init-auth-post.component';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';

describe('EmbPaymentInitAuthPostComponent', () => {
  let component: EmbPaymentInitAuthPostComponent;
  let fixture: ComponentFixture<EmbPaymentInitAuthPostComponent>;

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
        EmbPaymentInitAuthPostComponent,
        TranslatePipe,
        MockPlayWithDataComponent,
        LineCommandComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentInitAuthPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
