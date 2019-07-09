import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfTransactionGetComponent } from './accinf-transaction-get.component';
import {Component, Input, Pipe, PipeTransform} from '@angular/core';
import {LineCommandComponent} from '../../../../../custom-elements/line-command/line-command.component';

describe('AccinfTransactionGetComponent', () => {
  let component: AccinfTransactionGetComponent;
  let fixture: ComponentFixture<AccinfTransactionGetComponent>;

  @Component({
    selector: 'app-play-wth-data',
    template: ''
  })
  class MockPlayWithDataComponent {
    @Input() headers: object;
    @Input() accountIdFlag: boolean;
    @Input() transactionIdFlag: boolean;
    @Input() variablePathEnd: string;
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
        AccinfTransactionGetComponent,
        LineCommandComponent,
        TranslatePipe,
        MockPlayWithDataComponent
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfTransactionGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (not all)', () => {
    expect(component).toBeTruthy();
  });
});
