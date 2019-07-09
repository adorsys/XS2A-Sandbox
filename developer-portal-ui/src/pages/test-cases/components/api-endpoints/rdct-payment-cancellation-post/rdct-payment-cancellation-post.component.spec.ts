import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdctPaymentCancellationPostComponent } from './rdct-payment-cancellation-post.component';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';
import {CodeAreaComponent} from "../../../../../custom-elements/code-area/code-area.component";

describe('RdctPaymentCancellationPostComponent', () => {
  let component: RdctPaymentCancellationPostComponent;
  let fixture: ComponentFixture<RdctPaymentCancellationPostComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: ''
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() body: object;
    @Input() paymentServiceFlag: boolean;
    @Input() paymentProductFlag: boolean;
    @Input() fieldsToCopy: string[];
  }

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  @Pipe({name: 'prettyJson'})
  class PrettyJsonPipe implements PipeTransform {
    transform(value) {
      return JSON.stringify(value, null, 4);
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        RdctPaymentCancellationPostComponent,
        MockPlayWithDataComponent,
        TranslatePipe,
        PrettyJsonPipe,
        LineCommandComponent,
        CodeAreaComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdctPaymentCancellationPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
