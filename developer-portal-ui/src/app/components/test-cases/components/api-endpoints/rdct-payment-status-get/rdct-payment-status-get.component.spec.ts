import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Component, Input, Pipe, PipeTransform } from '@angular/core';
import { RdctPaymentStatusGetComponent } from './rdct-payment-status-get.component';
import {LineCommandComponent} from "../../../../common/line-command/line-command.component";

describe('RdctPaymentStatusGetComponent', () => {
  let component: RdctPaymentStatusGetComponent;
  let fixture: ComponentFixture<RdctPaymentStatusGetComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: '',
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() paymentServiceFlag: boolean;
    @Input() paymentProductFlag: boolean;
    @Input() paymentIdFlag: boolean;
    @Input() variablePathEnd: '/status';
  }

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        RdctPaymentStatusGetComponent,
        MockPlayWithDataComponent,
        TranslatePipe,
        LineCommandComponent,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdctPaymentStatusGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be right headers', () => {
    const headers: object = {
      'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
      'PSU-IP-Address': '1.1.1.1',
    };
    expect(typeof component.headers).toBe('object');
    for (const key in component.headers) {
      if (component.headers.hasOwnProperty(key)) {
        expect(headers.hasOwnProperty(key)).toBeTruthy();
        expect(headers[key]).toBe(component.headers[key]);
      }
    }
  });

  it('should change segment', () => {
    expect(component.activeSegment).toBe('documentation');

    component.changeSegment('play-data');
    expect(component.activeSegment).toBe('play-data');

    component.changeSegment('documentation');
    expect(component.activeSegment).toBe('documentation');

    component.changeSegment('wrong-segment');
    expect(component.activeSegment).not.toBe('wrong-segment');
  });
});
